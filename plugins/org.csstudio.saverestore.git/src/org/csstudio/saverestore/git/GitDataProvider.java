package org.csstudio.saverestore.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.BaseLevel;
import org.csstudio.saverestore.BeamlineSet;
import org.csstudio.saverestore.BeamlineSetData;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.Snapshot;
import org.csstudio.saverestore.VSnapshot;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitDataProvider implements DataProvider {

    private final GitResourceManager grm;

    /**
     * Constructs a new GitDataProvider.
     */
    public GitDataProvider() {
        URI rem = URI.create("https://github.com/jbobnar/ssrdemo.git");
        File dest = new File("G:/temp/ssr");
        try {
            grm = new GitResourceManager(rem, dest);
        } catch (GitAPIException e) {
            throw new RuntimeException("Could not instantiate git data provider.",e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#getBranches()
     */
    @Override
    public String[] getBranches() throws DataProviderException {
        try {
            List<String> branches = grm.getBranches();
            return branches.toArray(new String[branches.size()]);
        } catch (GitAPIException e) {
            throw new DataProviderException("Error loading the branches list", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#getBaseLevels(java.lang.String)
     */
    @Override
    public BaseLevel[] getBaseLevels(String branch) throws DataProviderException {
        assertOnBranch(branch);
        List<BaseLevel> bls = grm.getBaseLevels();
        return bls.toArray(new BaseLevel[bls.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#getBeamlineSets(org.csstudio.saverestore.BaseLevel, java.lang.String)
     */
    @Override
    public BeamlineSet[] getBeamlineSets(BaseLevel baseLevel, String branch) throws DataProviderException {
        assertOnBranch(branch);
        try {
            List<BeamlineSet> sets = grm.getBeamlineSets(Optional.ofNullable(baseLevel));
            return sets.toArray(new BeamlineSet[sets.size()]);
        } catch (IOException e) {
            throw new DataProviderException("Error loading the beamline set list", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#getSnapshots(org.csstudio.saverestore.BeamlineSet)
     */
    @Override
    public Snapshot[] getSnapshots(BeamlineSet set) throws DataProviderException {
        assertOnBranch(set.getBranch());
        try {
            List<Snapshot> snapshots = grm.getSnapshots(set);
            return snapshots.toArray(new Snapshot[snapshots.size()]);
        } catch (IOException | GitAPIException e) {
            throw new DataProviderException("Error retrieving the snapshots list", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#getBeamlineSetContent(org.csstudio.saverestore.BeamlineSet)
     */
    @Override
    public BeamlineSetData getBeamlineSetContent(BeamlineSet set) throws DataProviderException {
        assertOnBranch(set.getBranch());
        try {
            return grm.loadBeamlineSetData(set, Optional.empty());
        } catch (IOException e) {
            throw new DataProviderException("Error loading the beamline set data for " + set, e);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#createNewBranch(java.lang.String, java.lang.String)
     */
    @Override
    public void createNewBranch(String originalBranch, String newBranchName) throws DataProviderException {
        assertOnBranch(originalBranch);
        try {
            grm.createBranch(newBranchName);
        } catch (GitAPIException e) {
            throw new DataProviderException("Error creating branch " + newBranchName,e);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#saveBeamlineSet(org.csstudio.saverestore.BeamlineSetData, java.lang.String)
     */
    @Override
    public void saveBeamlineSet(BeamlineSetData set, String comment) throws DataProviderException {
        assertOnBranch(set.getDescriptor().getBranch());
        MetaInfo meta = new MetaInfo(comment,null,null,null);
        try {
            grm.saveBeamlineSet(set, meta);
        } catch (IOException | GitAPIException e) {
            throw new DataProviderException("Error saving beamline set",e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#saveSnapshot(org.csstudio.saverestore.VSnapshot, java.lang.String)
     */
    @Override
    public void saveSnapshot(VSnapshot data, String comment) throws DataProviderException {
        assertOnBranch(data.getBeamlineSet().getBranch());
        MetaInfo meta = new MetaInfo(comment,null,null,null);
        try {
            grm.saveSnapshot(data, meta);
        } catch (IOException | GitAPIException e) {
            throw new DataProviderException("Error saving snapshot set",e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#tagSnapshot(org.csstudio.saverestore.Snapshot, java.lang.String, java.lang.String)
     */
    @Override
    public void tagSnapshot(Snapshot snapshot, String tagName, String tagMessage) {
        System.out.println("Snapshot tagged");
        //TODO
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#getSnapshotContent(org.csstudio.saverestore.Snapshot)
     */
    @Override
    public VSnapshot getSnapshotContent(Snapshot snapshot) throws DataProviderException {
        assertOnBranch(snapshot.getBeamlineSet().getBranch());
        try {
            return grm.loadSnapshotData(snapshot);
        } catch (ParseException | IOException e) {
            throw new DataProviderException("Error loading the snapshot content",e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#areBranchesSupported()
     */
    @Override
    public boolean areBranchesSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#areBaseLevelsSupported()
     */
    @Override
    public boolean areBaseLevelsSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.DataProvider#synchronise()
     */
    @Override
    public void synchronise() throws DataProviderException {
        try {
            grm.synchronise();
        } catch (GitAPIException e) {
            throw new DataProviderException("Error synchronising local repository with remote.",e);
        }
    }

    private void assertOnBranch(String branch) throws DataProviderException {
        try {
            grm.setBranch(branch);
        } catch (GitAPIException | IOException e) {
            throw new DataProviderException("Error during branch '" + branch + "' checkout",e);
        }
    }

}
