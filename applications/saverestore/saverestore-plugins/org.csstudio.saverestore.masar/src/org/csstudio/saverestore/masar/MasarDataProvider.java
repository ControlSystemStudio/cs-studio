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
package org.csstudio.saverestore.masar;

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
import org.csstudio.saverestore.SearchCriterion;
import org.csstudio.saverestore.UnsupportedActionException;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;

/**
 * <code>MasarDataProvider</code> the data provider implementation that uses MASAR service.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class MasarDataProvider implements DataProvider {

    public static final String ID = "org.csstudio.saverestore.masar.dataprovider";
    private static final SearchCriterion COMMENT = SearchCriterion.of("Snapshot Comment", true, false);
    private static final SearchCriterion USER = SearchCriterion.of("User", true, false);
    private static final SearchCriterion SNAPSHOT_ID = SearchCriterion.of("Snapshot ID", false, true);
    private static final List<SearchCriterion> SEARCH_CRITERIA = Collections
        .unmodifiableList(Arrays.asList(COMMENT, USER, SNAPSHOT_ID));

    private final MasarClient mc;
    private final List<CompletionNotifier> notifiers;

    private final CompletionNotifier connectionListener = new CompletionNotifier() {
        @Override
        public void synchronised() {
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
        }
    };

    /**
     * Constructs a new GitDataProvider.
     */
    public MasarDataProvider() {
        this(new MasarClient());
    }

    /**
     * Constructs a new GitDataProvider.
     *
     * @param client the masar client to use
     */
    public MasarDataProvider(MasarClient client) {
        notifiers = new ArrayList<>();
        mc = client;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#initialise()
     */
    @Override
    public void initialise() throws DataProviderException {
        try {
            mc.initialise(Activator.getInstance().getServices(), connectionListener);
        } catch (RuntimeException | MasarException e) {
            throw new DataProviderException("Could not instantiate masar data provider.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#isReinitSupported()
     */
    @Override
    public boolean isReinitSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#reinitialise()
     */
    @Override
    public boolean reinitialise() throws DataProviderException {
        try {
            boolean b = mc.initialise(Activator.getInstance().getServices(), connectionListener);
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
            return b;
        } catch (RuntimeException | MasarException e) {
            throw new DataProviderException("Could not initialise masar service.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getBranches()
     */
    @Override
    public Branch[] getBranches() throws DataProviderException {
        List<Branch> branches = mc.getServices();
        return branches.toArray(new Branch[branches.size()]);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getBaseLevels(org.csstudio.saverestore.Branch)
     */
    @Override
    public BaseLevel[] getBaseLevels(Branch branch) throws DataProviderException {
        try {
            List<BaseLevel> bls = mc.getSystemConfigs(branch);
            return bls.toArray(new BaseLevel[bls.size()]);
        } catch (RuntimeException | MasarException e) {
            throw new DataProviderException("Could not load the systems list.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSaveSets(java.util.Optional, org.csstudio.saverestore.data.Branch)
     */
    @Override
    public SaveSet[] getSaveSets(Optional<BaseLevel> baseLevel, Branch branch) throws DataProviderException {
        try {
            List<SaveSet> sets = mc.getSaveSets(baseLevel, branch);
            return sets.toArray(new SaveSet[sets.size()]);
        } catch (RuntimeException | MasarException e) {
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
        try {
            List<Snapshot> snapshots = mc.getSnapshots(set);
            return snapshots.toArray(new Snapshot[snapshots.size()]);
        } catch (RuntimeException | MasarException | ParseException e) {
            throw new DataProviderException(
                String.format("Error retrieving the snapshots list for '%s'.", set.getPathAsString()), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSaveSetContent(org.csstudio.saverestore.SaveSet)
     */
    @Override
    public SaveSetData getSaveSetContent(SaveSet set) throws DataProviderException {
        try {
            return mc.loadSaveSetData(set);
        } catch (RuntimeException | MasarException | ParseException e) {
            throw new DataProviderException("Error loading contents of a save set.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#createNewBranch(org.csstudio.saverestore.Branch, java.lang.String)
     */
    @Override
    public Branch createNewBranch(Branch originalService, String newServiceName) throws DataProviderException {
        Branch service = null;
        try {
            service = mc.createService(newServiceName);
        } catch (RuntimeException | MasarException e) {
            throw new DataProviderException(String.format("Error connecting to service '%s'.", newServiceName), e);
        }
        for (CompletionNotifier n : getNotifiers()) {
            n.branchCreated(service);
        }
        return service;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#takeSnapshot(org.csstudio.saverestore.data.SaveSet)
     */
    @Override
    public VSnapshot takeSnapshot(SaveSet saveSet) throws DataProviderException, UnsupportedActionException {
        try {
            return mc.takeSnapshot(saveSet);
        } catch (MasarResponseException e) {
            throw new DataProviderException(
                String.format("Error taking a snapshot for '%s'.\nService responded with message: %s.",
                    saveSet.getPathAsString(), e.getMessage()));
        } catch (RuntimeException | MasarException e) {
            throw new DataProviderException(
                String.format("Error taking a snapshot for '%s'.", saveSet.getPathAsString()), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#saveSaveSet(org.csstudio.saverestore.SaveSetData, java.lang.String)
     */
    @Override
    public SaveSetData saveSaveSet(SaveSetData set, String comment) throws DataProviderException {
        try {
            return mc.createSaveSets(set, comment);
        } catch (Exception e) {
            throw new DataProviderException(
                    String.format("Error creating snapshot config for '%s'.", set.toString()),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#deleteSaveSet(org.csstudio.saverestore.data.SaveSet, java.lang.String)
     */
    @Override
    public boolean deleteSaveSet(SaveSet set, String comment) throws DataProviderException {
        throw new UnsupportedActionException("MASAR does not support save set editing.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#saveSnapshot(org.csstudio.saverestore.VSnapshot, java.lang.String)
     */
    @Override
    public VSnapshot saveSnapshot(VSnapshot data, String comment) throws DataProviderException {
        VSnapshot snapshot = null;
        try {
            snapshot = mc.saveSnapshot(data, comment);
        } catch (MasarResponseException e) {
            throw new DataProviderException(
                String.format("Error saving a snapshot for '%s'.\nService responded with message: %s.",
                    data.getSaveSet().getPathAsString(), e.getMessage()));
        } catch (RuntimeException | MasarException e) {
            throw new DataProviderException(
                String.format("Error saving snapshot for '%s'.", data.getSaveSet().getPathAsString()), e);
        }

        for (CompletionNotifier n : getNotifiers()) {
            n.snapshotSaved(snapshot);
        }

        return snapshot;
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
        throw new UnsupportedActionException("MASAR does not support tagging.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSnapshotContent(org.csstudio.saverestore.Snapshot)
     */
    @Override
    public VSnapshot getSnapshotContent(Snapshot snapshot) throws DataProviderException {
        try {
            return mc.loadSnapshotData(snapshot);
        } catch (RuntimeException | MasarException e) {
            throw new DataProviderException(String.format("Error loading the snapshot content for snapshot '%s [%s]'.",
                snapshot.getSaveSet().getPathAsString(), String.valueOf(snapshot.getDate())), e);
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
        Set<Snapshot> list = new LinkedHashSet<>();
        boolean sort = false;
        try {
            if (criteria.contains(SNAPSHOT_ID)) {
                try {
                    int id = Integer.parseInt(expression.trim());
                    mc.findSnapshotById(branch, id).ifPresent(e -> list.add(e));
                } catch (NumberFormatException e) {
                    throw new DataProviderException(String.format(
                        "'%s' is not a valid expression for search by snapshot ID. Number is required.", expression));
                }
            } else if (criteria.contains(COMMENT) && criteria.contains(USER)) {
                // we want OR between user and comments matches, but the MasarClient does AND
                list.addAll(mc.findSnapshots(branch, expression, true, false, start, end));
                int size = list.size();
                list.addAll(mc.findSnapshots(branch, expression, false, true, start, end));
                sort = size > 0 && list.size() != size;
            } else if (criteria.contains(COMMENT)) {
                list.addAll(mc.findSnapshots(branch, expression, false, true, start, end));
            } else if (criteria.contains(USER)) {
                list.addAll(mc.findSnapshots(branch, expression, true, false, start, end));
            } else if (start.isPresent() || end.isPresent()) {
                list.addAll(mc.findSnapshots(branch, expression, false, false, start, end));
            }
        } catch (MasarException | ParseException e) {
            throw new DataProviderException(
                String.format("Error searching for snapshots that match the expression '%s' using criteria '%s'.",
                    expression, criteria.toString()),
                e);
        }
        Snapshot[] snapshots = list.toArray(new Snapshot[list.size()]);
        if (sort) {
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
        throw new UnsupportedActionException("MASAR does not provide facilities to import save sets.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#synchronise()
     */
    @Override
    public boolean synchronise() throws DataProviderException {
        // there is no synchronisation but still signal that synchronisation is completed, so that client re-fetches the
        // base levels
        for (CompletionNotifier n : getNotifiers()) {
            n.synchronised();
        }
        return true;
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

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#isImportSupported()
     */
    @Override
    public boolean isImportSupported() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#isSaveSetSavingSupported()
     */
    @Override
    public boolean isSaveSetSavingSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#isTaggingSupported()
     */
    @Override
    public boolean isTaggingSupported() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#isSearchSupported()
     */
    @Override
    public boolean isSearchSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#areBranchesSupported()
     */
    @Override
    public boolean areBranchesSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#areBaseLevelsSupported()
     */
    @Override
    public boolean areBaseLevelsSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#isTakingSnapshotsSupported()
     */
    @Override
    public boolean isTakingSnapshotsSupported() {
        return true;
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

    private CompletionNotifier[] getNotifiers() {
        CompletionNotifier[] nots;
        synchronized (notifiers) {
            nots = notifiers.toArray(new CompletionNotifier[notifiers.size()]);
        }
        return nots;
    }
}
