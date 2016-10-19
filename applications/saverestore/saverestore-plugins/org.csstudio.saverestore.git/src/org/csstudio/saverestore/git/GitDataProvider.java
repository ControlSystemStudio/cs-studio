/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.csstudio.saverestore.CompletionNotifier;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.SearchCriterion;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.git.Result.ChangeType;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * <code>GitDataProvider</code> the data provider implementation that uses git repository to store all the data.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class GitDataProvider implements DataProvider {

    public static final String ID = "org.csstudio.saverestore.git.dataprovider";

    private static final SearchCriterion COMMENT = SearchCriterion.of("Snapshot Comment",false, false);
    private static final SearchCriterion TAG_NAME = SearchCriterion.of("Snapshot tag name",true, false);
    private static final SearchCriterion TAG_MESSAGE = SearchCriterion.of("Snapshot tag message",false, false);
    private static final SearchCriterion USER = SearchCriterion.of("User",false, false);
    private static final List<SearchCriterion> SEARCH_CRITERIA = Collections
        .unmodifiableList(Arrays.asList(COMMENT, TAG_NAME, TAG_MESSAGE, USER));

    private final GitManager grm;
    private final List<CompletionNotifier> notifiers;
    private boolean initialized = false;

    /**
     * Constructs a new GitDataProvider.
     */
    public GitDataProvider() {
        this(new GitManager());
    }

    /**
     * Constructs a new GitDataProvider using the provider git manager.
     *
     * @param grm git manager which implements all underlying git stuff
     */
    public GitDataProvider(GitManager grm) {
        notifiers = new ArrayList<>();
        this.grm = grm;
        Activator.getInstance().getPreferenceStore().addPropertyChangeListener(e -> {
            String property = e.getProperty();
            if (Activator.PREF_URL.equals(property) || Activator.PREF_DESTINATION.equals(property)) {
                SaveRestoreService.getInstance().reselectDataProvider();
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#initialise()
     */
    @Override
    public void initialise() throws DataProviderException {
        try {
            URI remote = Activator.getInstance().getGitURI();
            if (remote.toString().isEmpty()) {
                throw new DataProviderException("Repository was not defined.");
            }
            File dest = Activator.getInstance().getDestination();
            grm.initialise(remote, dest);
            initialized = true;
        } catch (RuntimeException | GitAPIException e) {
            throw new DataProviderException("Could not instantiate git data provider.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#reinitialise()
     */
    @Override
    public boolean reinitialise() throws DataProviderException {
        if (grm.isLocalOnly()) {
            throw new DataProviderException("You are working with a local git copy. Automatic reinitialisation "
                + "is not supported because you will lose all data.");
        }
        try {
            initialized = false;
            URI remote = Activator.getInstance().getGitURI();
            File dest = Activator.getInstance().getDestination();
            GitManager.deleteFolder(dest);
            boolean b = grm.initialise(remote, dest);
            initialized = true;
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
            return b;
        } catch (RuntimeException | GitAPIException e) {
            throw new DataProviderException("Could not initialise git repository.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#addCompletionNotifier(org.csstudio.saverestore.CompletionNotifier)
     */
    @Override
    public void addCompletionNotifier(CompletionNotifier notifier) {
        synchronized (notifiers) {
            notifiers.add(notifier);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#removeCompletionNotifier(org.csstudio.saverestore.CompletionNotifier)
     */
    @Override
    public void removeCompletionNotifier(CompletionNotifier notifier) {
        synchronized (notifiers) {
            notifiers.remove(notifier);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getBranches()
     */
    @Override
    public Branch[] getBranches() throws DataProviderException {
        checkInitialised();
        try {
            List<Branch> branches = grm.getBranches();
            return branches.toArray(new Branch[branches.size()]);
        } catch (RuntimeException | GitAPIException e) {
            throw new DataProviderException("Error loading the branches list.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getBaseLevels(org.csstudio.saverestore.Branch)
     */
    @Override
    public BaseLevel[] getBaseLevels(Branch branch) throws DataProviderException {
        checkInitialised();
        try {
            List<BaseLevel> bls = grm.getBaseLevels(branch);
            return bls.toArray(new BaseLevel[bls.size()]);
        } catch (RuntimeException | GitAPIException | IOException e) {
            throw new DataProviderException("Could not read the base levels.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSaveSets(java.util.Optional,
     * org.csstudio.saverestore.data.Branch)
     */
    @Override
    public SaveSet[] getSaveSets(Optional<BaseLevel> baseLevel, Branch branch) throws DataProviderException {
        checkInitialised();
        try {
            List<SaveSet> sets = grm.getSaveSets(baseLevel, branch);
            return sets.toArray(new SaveSet[sets.size()]);
        } catch (IOException | GitAPIException | RuntimeException e) {
            throw new DataProviderException("Error loading the save set list.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSnapshots(org.csstudio.saverestore.data.SaveSet, boolean,
     * java.util.Optional)
     */
    @Override
    public Snapshot[] getSnapshots(SaveSet set, boolean all, Optional<Snapshot> fromThisOneBack)
        throws DataProviderException {
        checkInitialised();
        try {
            List<Snapshot> snapshots = grm.getSnapshots(set,
                all ? 0 : SaveRestoreService.getInstance().getNumberOfSnapshots(), fromThisOneBack);
            return snapshots.toArray(new Snapshot[snapshots.size()]);
        } catch (RuntimeException | IOException | GitAPIException e) {
            throw new DataProviderException("Error retrieving the snapshots list for '" + set.getPathAsString() + "'.",
                e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSaveSetContent(org.csstudio.saverestore.SaveSet)
     */
    @Override
    public SaveSetData getSaveSetContent(SaveSet set) throws DataProviderException {
        checkInitialised();
        try {
            return grm.loadSaveSetData(set, Optional.empty());
        } catch (RuntimeException | IOException | GitAPIException e) {
            throw new DataProviderException("Error loading the save set data for '" + set.getPathAsString() + "'.",
                e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#createNewBranch(org.csstudio.saverestore.Branch, java.lang.String)
     */
    @Override
    public Branch createNewBranch(Branch originalBranch, String newBranchName) throws DataProviderException {
        checkInitialised();
        Branch branch = null;
        try {
            branch = grm.createBranch(originalBranch, newBranchName);
        } catch (RuntimeException | GitAPIException | IOException e) {
            throw new DataProviderException("Error creating branch '" + newBranchName + "'.", e);
        }
        for (CompletionNotifier n : getNotifiers()) {
            n.branchCreated(branch);
        }
        return branch;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#saveSaveSet(org.csstudio.saverestore.SaveSetData,
     * java.lang.String)
     */
    @Override
    public SaveSetData saveSaveSet(SaveSetData set, String comment) throws DataProviderException {
        checkInitialised();
        Result<SaveSetData> answer = null;
        try {
            answer = grm.saveSaveSet(set, comment);
        } catch (RuntimeException | IOException | GitAPIException e) {
            throw new DataProviderException(
                "Error storing save set '" + set.getDescriptor().getPathAsString() + "'.", e);
        }
        if (answer.change == ChangeType.PULL) {
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
        } else if (answer.change == ChangeType.SAVE) {
            for (CompletionNotifier n : getNotifiers()) {
                n.saveSetSaved(answer.data);
            }
        }
        return answer.data;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#deleteSaveSet(org.csstudio.saverestore.data.SaveSet,
     * java.lang.String)
     */
    @Override
    public boolean deleteSaveSet(SaveSet set, String comment) throws DataProviderException {
        checkInitialised();
        Result<SaveSet> answer = null;
        try {
            answer = grm.deleteSaveSet(set, comment);
        } catch (RuntimeException | IOException | GitAPIException e) {
            throw new DataProviderException("Error deleting save set '" + set.getPathAsString() + "'.", e);
        }
        if (answer.change == ChangeType.PULL) {
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
        } else if (answer.change == ChangeType.SAVE) {
            for (CompletionNotifier n : getNotifiers()) {
                n.saveSetDeleted(answer.data);
            }
        }
        return answer.data != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#saveSnapshot(org.csstudio.saverestore.VSnapshot, java.lang.String)
     */
    @Override
    public VSnapshot saveSnapshot(VSnapshot data, String comment) throws DataProviderException {
        checkInitialised();
        Result<VSnapshot> answer = null;
        try {
            answer = grm.saveSnapshot(data, comment);
        } catch (RuntimeException | IOException | GitAPIException e) {
            throw new DataProviderException(
                "Error saving snapshot set for '" + data.getSaveSet().getPathAsString() + "'.", e);
        }
        if (answer.change == ChangeType.PULL) {
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
        } else if (answer.change == ChangeType.SAVE) {
            for (CompletionNotifier n : getNotifiers()) {
                n.snapshotSaved(answer.data);
            }
        }
        return answer.data;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#tagSnapshot(org.csstudio.saverestore.data.Snapshot,
     * java.util.Optional, java.util.Optional)
     */
    @Override
    public Snapshot tagSnapshot(Snapshot snapshot, Optional<String> tagName, Optional<String> tagMessage)
        throws DataProviderException {
        checkInitialised();
        Result<Snapshot> answer = null;
        try {
            answer = grm.tagSnapshot(snapshot, tagName.orElse(null), tagMessage.orElse(null));
        } catch (RuntimeException | IOException | GitAPIException e) {
            throw new DataProviderException("Error creating the tag for snapshot '" + snapshot.getDate() + "'.", e);
        }
        if (answer.change == ChangeType.PULL) {
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
        } else if (answer.change == ChangeType.SAVE) {
            for (CompletionNotifier n : getNotifiers()) {
                n.snapshotTagged(answer.data);
            }
        }
        return answer.data;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSnapshotContent(org.csstudio.saverestore.Snapshot)
     */
    @Override
    public VSnapshot getSnapshotContent(Snapshot snapshot) throws DataProviderException {
        checkInitialised();
        try {
            return grm.loadSnapshotData(snapshot);
        } catch (ParseException | IOException | GitAPIException | RuntimeException e) {
            throw new DataProviderException("Error loading the snapshot content for snapshot '"
                + snapshot.getSaveSet().getPathAsString() + "[" + snapshot.getDate() + "]'.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#findSnapshots(java.lang.String, org.csstudio.saverestore.data.Branch,
     * java.util.List, java.util.Optional, java.util.Optional)
     */
    @Override
    public Snapshot[] findSnapshots(String expression, Branch branch, List<SearchCriterion> criteria,
        Optional<Date> start, Optional<Date> end) throws DataProviderException {
        checkInitialised();
        Set<Snapshot> list = new LinkedHashSet<>();
        boolean sort = false;
        try {
            if (criteria.contains(COMMENT) || criteria.contains(USER)) {
                list.addAll(grm.findSnapshotsByCommentOrUser(expression, branch, criteria.contains(COMMENT),
                    criteria.contains(USER), start, end));
            } else if (!(criteria.contains(TAG_MESSAGE) || criteria.contains(TAG_NAME))
                && (start.isPresent() || end.isPresent())) {
                list.addAll(grm.findSnapshotsByCommentOrUser(expression, branch, false, false, start, end));
            }

            int size = list.size();
            if (criteria.contains(TAG_MESSAGE) && criteria.contains(TAG_NAME)) {
                list.addAll(grm.findSnapshotsByTag(expression, branch, start, end));
            } else {
                if (criteria.contains(TAG_MESSAGE)) {
                    list.addAll(grm.findSnapshotsByTagMessage(expression, branch, start, end));
                } else if (criteria.contains(TAG_NAME)) {
                    list.addAll(grm.findSnapshotsByTagName(expression, branch, start, end));
                }
            }
            sort = size > 0 && list.size() != size;
        } catch (RuntimeException | GitAPIException | IOException e) {
            throw new DataProviderException("Error search for snapshot that match the expression '" + expression
                + "' using criteria '" + criteria.toString() + ".", e);
        }
        Snapshot[] snapshots = list.toArray(new Snapshot[list.size()]);
        if (sort) {
            // there is a natural order of snapshots provided by GitManager. Sort only if the search was performed more
            // than once.
            Arrays.sort(snapshots);
        }
        return snapshots;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#importData(org.csstudio.saverestore.data.SaveSet,
     * org.csstudio.saverestore.data.Branch, java.util.Optional, org.csstudio.saverestore.DataProvider.ImportType)
     */
    @Override
    public boolean importData(SaveSet source, Branch toBranch, Optional<BaseLevel> toBaseLevel, ImportType type)
        throws DataProviderException {
        checkInitialised();
        Result<Boolean> answer = null;
        try {
            answer = grm.importData(source, toBranch, toBaseLevel, type);
        } catch (RuntimeException | IOException | GitAPIException | ParseException e) {
            throw new DataProviderException("Error importing data from '" + source.getPathAsString() + "'.", e);
        }

        if (answer.change == ChangeType.PULL) {
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
        } else if (answer.change == ChangeType.SAVE) {
            for (CompletionNotifier n : getNotifiers()) {
                n.dataImported(source, toBranch, toBaseLevel);
            }
        }
        return answer.data;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#synchronise()
     */
    @Override
    public boolean synchronise() throws DataProviderException {
        checkInitialised();
        boolean sync = false;
        try {
            sync = grm.synchronise(Optional.empty());
        } catch (RuntimeException | GitAPIException e) {
            throw new DataProviderException("Error synchronising local repository with remote.", e);
        }
        for (CompletionNotifier n : getNotifiers()) {
            n.synchronised();
        }
        return sync;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSupportedSearchCriteria()
     */
    @Override
    public List<SearchCriterion> getSupportedSearchCriteria() {
        return SEARCH_CRITERIA;
    }

    private void checkInitialised() throws DataProviderException {
        if (!initialized) {
            throw new DataProviderException("Git Data Provider has not been initialised.");
        }
    }

    private CompletionNotifier[] getNotifiers() {
        CompletionNotifier[] nots;
        synchronized (notifiers) {
            nots = notifiers.toArray(new CompletionNotifier[notifiers.size()]);
        }
        return nots;
    }
}
