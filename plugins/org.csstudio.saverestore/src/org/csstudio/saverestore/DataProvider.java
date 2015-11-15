package org.csstudio.saverestore;

/**
 *
 * <code>DataProvider</code> provides all data that is used by the save and restore application. It loads the
 * beamline sets and snapshots as well as store them and do other actions related to these objects and storage.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface DataProvider {

    /**
     * Adds a completion notifier which is notified every time when a specific action is completed. The notifiers
     * can be used to refresh the data in the UI, whenever a specific action is triggered by another UI part.
     *
     * @param notifier the notifier
     */
    void addCompletionNotifier(CompletionNotifier notifier);

    /**
     * Remove the completion notifier.
     *
     * @param notifier the notifier to remove
     */
    void removeCompletionNotifier(CompletionNotifier notifier);

    /**
     * Returns the list of all available branches. If {@link #areBranchesSupported()} returns false, this method does
     * not need to be implemented.
     *
     * @return the list of branch names
     *
     * @throws DataProviderException if the list of branches cannot be retrieved
     */
    String[] getBranches() throws DataProviderException;

    /**
     * Returns the list of base levels that have some beamline sets in the selected branch. The returned list is
     * guaranteed to have base levels with unique storage names, while the presentation names might be completely
     * wrong. It is the responsibility of the client to properly set the storage name. The base level object
     * instance or type that is used in further data retrieval calls, does not need to be an instance that was
     * returned by this method. The client may morph the returned object into another instance or implementation
     * of {@link BaseLevel} and used that instance for all further data retrieval
     *
     *
     * @param branch the branch for which the isotopes are requested
     * @return base levels for which some data exists
     *
     * @throws DataProviderException if the list of branches cannot be retrieved
     */
    BaseLevel[] getBaseLevels(String branch) throws DataProviderException;

    /**
     * Returns the beamline sets for the given base level and branch. Only the latest revision of each beamline set
     * should be returned. No data for these should be loaded yet.
     *
     * @param baseLevel the base level for which the beamline sets should be loaded
     * @param branch the branch from which the data should be loaded
     * @return all available beamline sets
     *
     * @throws DataProviderException if the list of bealine sets cannot be retrieved
     */
    BeamlineSet[] getBeamlineSets(BaseLevel baseLevel, String branch) throws DataProviderException;

    /**
     * Returns the list of all available snapshot revisions for the given beamline set. The returned snapshots contain
     * information when and by whom they were made including the save comment if supported.
     *
     * @param set the beamline set for which the snapshots are requested
     * @return the list of available snapshots
     *
     * @throws DataProviderException if the list of snapshots cannot be retrieved
     */
    Snapshot[] getSnapshots(BeamlineSet set) throws DataProviderException;

    /**
     * Returns the content of one specific snapshot revision.
     *
     * @param snapshot the snapshot revision descriptor
     * @return the snapshot data
     *
     * @throws DataProviderException if there is an error during content reading
     */
    VSnapshot getSnapshotContent(Snapshot snapshot) throws DataProviderException;

    /**
     * Returns the content of one specific beamline set.
     *
     * @param set the beamline set for which the content is requested
     * @return the data
     *
     * @throws DataProviderException if there is an error during content reading
     */
    BeamlineSetData getBeamlineSetContent(BeamlineSet set) throws DataProviderException;

    /**
     * @return true if the data provider supports branches or false if the structure is flat and the branch parameter
     *          of other methods is ignored
     */
    boolean areBranchesSupported();

    /**
     * @return true if the data provider supports base levels (the first level of selection) or false otherwise
     */
    boolean areBaseLevelsSupported();

    /**
     * Synchronise the local repository with the remote.
     *
     * @return true if any changes were pulled from the remote repository or false otherwise
     *
     * @throws DataProviderException if there is an error during synchronisation
     */
    boolean synchronise() throws DataProviderException;

    /**
     * Create a new branch with the given name. The content of the new branch is identical to the original one. If the
     * branches are not supported, the call can be ignored.
     *
     * @param originalBranch the name of the branch used as a starting point
     * @param newBranchName the name of the branch to create
     *
     * @return the name of the new branch
     *
     * @throws DataProviderException if there was an error during branch creation
     */
    String createNewBranch(String originalBranch, String newBranchName) throws DataProviderException;

    /**
     * Save a new revision of the beamline set. The specifics (e.g. location) of the beamline set are specified by
     * {@link BeamlineSetData#getDescription()}, the content of the file is given by the
     * {@link BeamlineSetData#getDescription()} and {@link BeamlineSetData#getPVList()}. If the storage facility
     * supports revision commenting, it should use the <code>comment</code> parameter and should raise an exception
     * if an invalid comment is provided.
     *
     * @param set the data and set definition to save
     * @param comment the commit comment to use for this revision
     *
     * @return the saved object, which has identical data as the <code>set</code> parameter (can be a different
     *          object) and in addition contains also the comment and other information
     *
     * @throws DataProviderException if there was an error during saving
     */
    BeamlineSetData saveBeamlineSet(BeamlineSetData set, String comment) throws DataProviderException;

    /**
     * Save a new version of snapshot. The specifics of the snapshot are specified by <code>snapshot</code>
     * parameter, while the data is specified by the <code>data</code>. If storage facility supports revision comments,
     * the comment should be provided as a parameter.
     *
     * @param data the data to store
     * @param comment the revision comment
     *
     * @return the stored data, which is identical to the <code>data</code> parameter, and in addition it contains
     *          the comment, date etc.
     * @throws DataProviderException if there was an error during saving
     */
    VSnapshot saveSnapshot(VSnapshot data, String comment) throws DataProviderException;

    /**
     * Tag the given snapshot with a specific tag name and message.
     *
     * @param snapshot the snapshot to tag
     * @param tagName the name of the tag
     * @param tagMessage the tag message
     *
     * @return the tagged snapshot
     *
     * @throws DataProviderException if there was an error during tagging
     */
    Snapshot tagSnapshot(Snapshot snapshot, String tagName, String tagMessage) throws DataProviderException;
}
