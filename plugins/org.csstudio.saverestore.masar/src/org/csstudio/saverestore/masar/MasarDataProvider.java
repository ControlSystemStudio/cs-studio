package org.csstudio.saverestore.masar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
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

    private final MasarClient mc;
    private List<CompletionNotifier> notifiers;

    /**
     * Constructs a new GitDataProvider.
     */
    public MasarDataProvider() {
        notifiers = new ArrayList<>();
        mc = new MasarClient();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#initialise()
     */
    @Override
    public void initialise() {
        try {
            mc.initialise(Activator.getInstance().getServices());
        } catch (MasarException e) {
            throw new RuntimeException("Could not instantiate masar data provider.", e);
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
            boolean b = mc.initialise(Activator.getInstance().getServices());
            for (CompletionNotifier n : getNotifiers()) {
                n.synchronised();
            }
            return b;
        } catch (MasarException e) {
            throw new DataProviderException("Could not initialise git repository.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getBranches()
     */
    @Override
    public Branch[] getBranches() throws DataProviderException {
        List<Branch> branches = mc.getBranches();
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
            List<BaseLevel> bls = mc.getBaseLevels(branch);
            return bls.toArray(new BaseLevel[bls.size()]);
        } catch (MasarException e) {
            throw new DataProviderException("Could not read the base levels.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getBeamlineSets(java.util.Optional,
     * org.csstudio.saverestore.data.Branch)
     */
    @Override
    public BeamlineSet[] getBeamlineSets(Optional<BaseLevel> baseLevel, Branch branch) throws DataProviderException {
        try {
            List<BeamlineSet> sets = mc.getBeamlineSets(baseLevel, branch);
            return sets.toArray(new BeamlineSet[sets.size()]);
        } catch (MasarException e) {
            throw new DataProviderException("Error loading the beamline set list.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getSnapshots(org.csstudio.saverestore.data.BeamlineSet, boolean,
     * java.util.Optional)
     */
    @Override
    public Snapshot[] getSnapshots(BeamlineSet set, boolean all, Optional<Snapshot> fromThisOneBack)
        throws DataProviderException {
        try {
            List<Snapshot> snapshots = mc.getSnapshots(set);
            return snapshots.toArray(new Snapshot[snapshots.size()]);
        } catch (MasarException | ParseException e) {
            throw new DataProviderException("Error retrieving the snapshots list for '" + set.getPathAsString() + "'.",
                e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#getBeamlineSetContent(org.csstudio.saverestore.BeamlineSet)
     */
    @Override
    public BeamlineSetData getBeamlineSetContent(BeamlineSet set) throws DataProviderException {
        try {
            return mc.loadBeamlineSetData(set);
        } catch (MasarException | ParseException e) {
            throw new DataProviderException("Error loading contents of a beamline set.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#createNewBranch(org.csstudio.saverestore.Branch, java.lang.String)
     */
    @Override
    public String createNewBranch(Branch originalBranch, String newBranchName) throws DataProviderException {
        // TODO define a new service and check if it exists
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#takeSnapshot(org.csstudio.saverestore.data.BeamlineSet)
     */
    @Override
    public VSnapshot takeSnapshot(BeamlineSet beamlineSet) throws DataProviderException, UnsupportedActionException {
        try {
            return mc.takeSnapshot(beamlineSet);
        } catch (MasarException e) {
            throw new DataProviderException("Error taking a snapshot for '" + beamlineSet.getPathAsString() + "'.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#saveBeamlineSet(org.csstudio.saverestore.BeamlineSetData,
     * java.lang.String)
     */
    @Override
    public BeamlineSetData saveBeamlineSet(BeamlineSetData set, String comment) throws DataProviderException {
        throw new UnsupportedActionException("MASAR does not support beamline set editing.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#deleteBeamlineSet(org.csstudio.saverestore.data.BeamlineSet,
     * java.lang.String)
     */
    @Override
    public boolean deleteBeamlineSet(BeamlineSet set, String comment) throws DataProviderException {
        throw new UnsupportedActionException("MASAR does not support beamline set editing.");
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
        } catch (MasarException e) {
            throw new DataProviderException(
                "Error saving snapshot set for '" + data.getBeamlineSet().getPathAsString() + "'.", e);
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
        } catch (MasarException e) {
            throw new DataProviderException("Error loading the snapshot content for snapshot '"
                + snapshot.getBeamlineSet().getPathAsString() + "[" + snapshot.getDate() + "]'.", e);
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
            if (criteria.contains(SearchCriterion.COMMENT) && criteria.contains(SearchCriterion.USER)) {
                // we want OR between user and comments matches, but the MasarClient does AND
                list.addAll(mc.findSnapshots(branch, expression, true, false, start, end));
                int size = list.size();
                list.addAll(mc.findSnapshots(branch, expression, false, true, start, end));
                sort = size > 0 && list.size() != size;
            } else if (criteria.contains(SearchCriterion.COMMENT)) {
                list.addAll(mc.findSnapshots(branch, expression, false, true, start, end));
            } else if (criteria.contains(SearchCriterion.USER)) {
                list.addAll(mc.findSnapshots(branch, expression, true, false, start, end));
            } else if (start.isPresent() || end.isPresent()) {
                list.addAll(mc.findSnapshots(branch, expression, false, false, start, end));
            }
        } catch (MasarException | ParseException e) {
            throw new DataProviderException("Error searching for snapshots that match the expression '" + expression
                + "' using criteria '" + criteria.toString() + ".", e);
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
     * @see org.csstudio.saverestore.DataProvider#importData(org.csstudio.saverestore.data.BeamlineSet,
     * org.csstudio.saverestore.data.Branch, java.util.Optional, org.csstudio.saverestore.DataProvider.ImportType)
     */
    @Override
    public boolean importData(BeamlineSet source, Branch toBranch, Optional<BaseLevel> toBaseLevel, ImportType type)
        throws DataProviderException {
        throw new UnsupportedActionException("MASAR does not provide facilities to import beamline sets.");
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
     * @see org.csstudio.saverestore.DataProvider#isImportSupported()
     */
    @Override
    public boolean isImportSupported() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.DataProvider#isBeamlineSetSavingSupported()
     */
    @Override
    public boolean isBeamlineSetSavingSupported() {
        return false;
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
        CompletionNotifier[] nots = null;
        synchronized (notifiers) {
            nots = notifiers.toArray(new CompletionNotifier[notifiers.size()]);
        }
        return nots;
    }
}
