package org.csstudio.saverestore;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;

/**
 *
 * <code>DataProvider</code> provides all data that is used by the save and restore application. It loads the beamline
 * sets and snapshots as well as store them and do other actions related to these objects and storage.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface DataProvider {

    /** The name of the data provider extension point definition */
    public static final String EXT_POINT = "org.csstudio.saverestore.dataprovider";

    /**
     * <code>ImportType</code> describes possible import actions. Depending on the value, the beamline sets will be
     * imported, followed by the last snapshot value or by all snapshots for the beamline sets.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public static enum ImportType {
        BEAMLINE_SET, LAST_SNAPSHOT, ALL_SNAPSHOTS
    }

    /**
     * Initialises this data provider so that it can be used. This method is only called after the data provider is
     * selected in the UI in order not to trigger any action that is not required at the time when the data provider is
     * created (e.g. credentials input). This method might be called more than once in the life cycle of the data
     * provider.
     *
     * @throws DataProviderException if the provider could not be initialised
     */
    void initialise() throws DataProviderException;

    /**
     * Adds a completion notifier which is notified every time when a specific action is completed. The notifiers can be
     * used to refresh the data in the UI, whenever a specific action is triggered by another UI part.
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
     * Data provider implementor can decide whether to support branches or not. If branches are supported the clients
     * will value the decision by first selecting the appropriate branch before making further requests to retrieve base
     * levels, beamline sets, or snapshots. In case that branches are not supported, the data provider can ignore any
     * branch parameter given to other methods.
     *
     * @return true if the data provider supports branches or false otherwise
     */
    default boolean areBranchesSupported() {
        return true;
    }

    /**
     * Data provider implementor can decide whether to support base levels or not. If supported clients will query the
     * base levels before querying the beamline sets and snapshots; if not supporter the data provider can safely ignore
     * any base level given as a parameter to other methods.
     * <p>
     * Base level can be for example the top folder, which contains the beamline sets and snapshots for a common setting
     * of the machine. There is no rule, what the base level actually is.
     * </p>
     *
     * @return true if the data provider supports base levels (the first level of selection) or false otherwise
     */
    default boolean areBaseLevelsSupported() {
        return true;
    }

    /**
     * If the data provider is using a repository, which exists locally and centrally it can implement the
     * re-initialisation procedure, which means that the local repository can be reinitialised to have the exactly the
     * same structure and content as the central repository. If re-initialisation is supported, this methods returns
     * true.
     *
     * @return true if re-initialisation of the repository is allowed or false otherwise
     */
    default boolean isReinitSupported() {
        return true;
    }

    /**
     * Returns true if the data provider implements means to import data from one beamline set to the other. The return
     * value indicates if the {@link #importData(BeamlineSet, Branch, Optional, ImportType)} can be invoked or not.
     *
     * @return true if it is possible to import data or false if not
     */
    default boolean isImportSupported() {
        return true;
    }

    /**
     * Returns true if editing and saving new beamline sets is supported or false if not. The return value indicates if
     * {@link #saveBeamlineSet(BeamlineSetData, String)}, {@link #deleteBeamlineSet(BeamlineSet, String)} can be invoked
     * or not.
     *
     * @return true if it is possible to save a new or existing beamline set or false if not
     */
    default boolean isBeamlineSetSavingSupported() {
        return true;
    }

    /**
     * Returns true if it is possible to tag a snapshot or not. The return value indicates if
     * {@link #tagSnapshot(Snapshot, Optional, Optional)} can be invoked or not.
     *
     * @return true if it is possible to tag a snapshot or false otherwise
     */
    default boolean isTaggingSupported() {
        return true;
    }

    /**
     * Returns true if is is possible to search for snapshots or not. The return value indicates if
     * {@link #findSnapshots(String, Branch, List)} can be invoked or not.
     *
     * @return true if it is possible to find a snapshot by specific keywords or false if not
     */
    default boolean isSearchSupported() {
        return true;
    }

    /**
     * Returns true if taking snapshots at the server side is supported or not. The return value indicates if
     * {@link #takeSnapshot(BeamlineSet)} can be invoked or not.
     *
     * @return true if the snapshots are taken on the server side or false if not
     */
    default boolean isTakingSnapshotsSupported() {
        return false;
    }

    /**
     * Returns the list of all available branches. If {@link #areBranchesSupported()} returns false, this method does
     * not need to be implemented and can return null, empty array or anything else.
     *
     * @return the list of branch names
     *
     * @throws DataProviderException if the list of branches cannot be retrieved
     */
    Branch[] getBranches() throws DataProviderException;

    /**
     * Returns the list of base levels that have some beamline sets in the selected branch. The returned list is
     * guaranteed to have base levels with unique storage names, while the presentation names might be completely wrong.
     * It is the responsibility of the client to properly set the storage name. The base level object instance or type
     * that is used in further data retrieval calls, does not need to be an instance that was returned by this method.
     * The client may morph the returned object into another instance or implementation of {@link BaseLevel} and used
     * that instance for all further data retrieval
     *
     *
     * @param branch the branch for which the isotopes are requested
     * @return base levels for which some data exists
     *
     * @throws DataProviderException if the list of branches cannot be retrieved
     */
    BaseLevel[] getBaseLevels(Branch branch) throws DataProviderException;

    /**
     * Returns the beamline sets for the given base level and branch. Only the latest revision of each beamline set
     * should be returned. No data for these should be loaded yet. The base level might provide the branch name as well,
     * however, the branch given as the second parameter should be used for all purposes.
     *
     * @param baseLevel the base level for which the beamline sets should be loaded
     * @param branch the branch from which the data should be loaded
     * @return all available beamline sets
     *
     * @throws DataProviderException if the list of bealine sets cannot be retrieved
     */
    BeamlineSet[] getBeamlineSets(Optional<BaseLevel> baseLevel, Branch branch) throws DataProviderException;

    /**
     * Returns the list of all available snapshot revisions for the given beamline set. The returned snapshots contain
     * information when and by whom they were made including the save comment if supported.
     *
     * @param set the beamline set for which the snapshots are requested
     * @param loadAll if true all snapshots are loaded regardless of the stored parameter maxNumberOfSnapshotsInBatch
     * @param fromThisOneBack if provided only the snapshots that were created strictly before the given one will be
     *            returned, if null all snapshots will be returned
     * @return the list of available snapshots
     * @see SaveRestoreService#getNumberOfSnapshots()
     * @throws DataProviderException if the list of snapshots cannot be retrieved
     */
    Snapshot[] getSnapshots(BeamlineSet set, boolean loadAll, Optional<Snapshot> fromThisOneBack)
        throws DataProviderException;

    /**
     * Returns the list of all snapshots that match the given expression, criteria, and optional time window. If one
     * snapshot matches more than one criterion, it is only included in the returned array once. If snapshots were found
     * they should be returned; if no snapshot was found an empty array should be returned. If the method
     * {@link #isSearchSupported()} returns false that this method may throw an {@link UnsupportedActionException}.
     *
     * @param expression the expression to match
     * @param branch the branch on which to search the snapshots
     * @param criteria the list of search criteria to use
     * @param start the start time of the search window; only snapshots that were created after the start time are
     *            returned
     * @param end the end time of the search window; only snapshots that were created before the end time are returned
     * @return the list of snapshots that match the expression and criteria
     * @throws DataProviderException in case of any search error
     * @throws UnsupportedActionException in case when {@link #isSearchSupported()} returns false
     */
    Snapshot[] findSnapshots(String expression, Branch branch, List<SearchCriterion> criteria, Optional<Date> start,
        Optional<Date> end) throws DataProviderException, UnsupportedActionException;

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
     * Reinitialise the local repository. If the provider contains a local copy of the repository, the local copy is
     * removed and reinitialised to the current state of the central repository. This action might delete all local
     * changes.
     *
     * @return true if re-initialisation was successful or false otherwise
     * @throws DataProviderException if there was an error during re-initialisation
     */
    boolean reinitialise() throws DataProviderException;

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
     * branches are not supported, the call may throw an {@link UnsupportedActionException}.
     *
     * @param originalBranch the name of the branch used as a starting point
     * @param newBranchName the name of the branch to create
     *
     * @return the new branch
     *
     * @throws DataProviderException if there was an error during branch creation
     * @throws UnsupportedActionException if the {@link #areBranchesSupported()} returns false
     */
    Branch createNewBranch(Branch originalBranch, String newBranchName)
        throws DataProviderException, UnsupportedActionException;

    /**
     * Save a new revision of the beamline set. The specifics (e.g. location) of the beamline set are specified by
     * {@link BeamlineSetData#getDescription()}, the content of the file is given by the
     * {@link BeamlineSetData#getDescription()} and {@link BeamlineSetData#getPVList()}. If the storage facility
     * supports revision commenting, it should use the <code>comment</code> parameter and should raise an exception if
     * an invalid comment is provided. If {@link #isBeamlineSetSavingSupported()} returns false, this method may throw
     * an {@link UnsupportedActionException}.
     *
     * @param set the data and set definition to save
     * @param comment the commit comment to use for this revision
     *
     * @return the saved object, which has identical data as the <code>set</code> parameter (can be a different object)
     *         and in addition contains also the comment and other information
     *
     * @throws DataProviderException if there was an error during saving
     * @throws UnsupportedActionException if {@link #isBeamlineSetSavingSupported()} returns false
     */
    BeamlineSetData saveBeamlineSet(BeamlineSetData set, String comment)
        throws DataProviderException, UnsupportedActionException;

    /**
     * Delete the beamline set from the repository. Only the beamline set should be delete, the snapshots may or may not
     * remain in the repository. If {@link #isBeamlineSetSavingSupported()} returns false, this method may throw an
     * {@link UnsupportedActionException}.
     *
     * @param set the set to delete
     * @param comment the comment to go with the action
     *
     * @return true if the set was deleted or false otherwise
     *
     * @throws DataProviderException in case of an error
     * @throws UnsupportedActionException if {@link #isBeamlineSetSavingSupported()} returns false
     */
    boolean deleteBeamlineSet(BeamlineSet set, String comment) throws DataProviderException, UnsupportedActionException;

    /**
     * Save a new version of snapshot. The specifics of the snapshot are specified by <code>snapshot</code> parameter,
     * while the data is specified by the <code>data</code>. If storage facility supports revision comments, the comment
     * should be provided as a parameter.
     *
     * @param data the data to store
     * @param comment the revision comment
     *
     * @return the stored data, which is identical to the <code>data</code> parameter, and in addition it contains the
     *         comment, date etc.
     * @throws DataProviderException if there was an error during saving
     */
    VSnapshot saveSnapshot(VSnapshot data, String comment) throws DataProviderException;

    /**
     * Tag the given snapshot with a specific tag name and message. If the snapshot already has a tag, that tag should
     * be removed and the new one applied. If the provided <code>tagName</code> is empty any existing tag on the
     * snapshot should be deleted. If {@link #isTaggingSupported()} returns false, this method may throw an
     * {@link UnsupportedActionException}.
     *
     * @param snapshot the snapshot to tag
     * @param tagName the name of the tag
     * @param tagMessage the tag message
     *
     * @return the tagged snapshot
     *
     * @throws DataProviderException if there was an error during tagging
     * @throws UnsupportedActionException if {@link #isTaggingSupported()} returns false
     */
    Snapshot tagSnapshot(Snapshot snapshot, Optional<String> tagName, Optional<String> tagMessage)
        throws DataProviderException, UnsupportedActionException;

    /**
     * Imports the data from the given source and imports them into the <code>toBranch</code> and
     * <code>toBaseLevel</code> using the same structures or paths as they are defined in the source. Depending on the
     * <code>type</code> only the beamline sets are imported, the beamline sets and the last snapshot for those beamline
     * sets, or the beamline sets and all snapshots for those beamline sets. If {@link #isImportSupported()} returns
     * false, this method may throw {@link UnsupportedActionException}.
     *
     * @param source the source sets; the given source can be an actual source or it can be a pointer to a folder, where
     *            multiple sets are stored (the name of the beamline set is empty). In the later case all sets within
     *            that folder should be imported
     * @param toBranch the destination branch
     * @param toBaseLevel the destination base level (can be empty if the base level of the sources is also empty)
     * @param type the type of import
     * @return true if successful or false otherwise
     * @throws DataProviderException in case of an error
     * @throws UnsupportedActionException if {@link #isImportSupported()} returns false
     */
    boolean importData(BeamlineSet source, Branch toBranch, Optional<BaseLevel> toBaseLevel, ImportType type)
        throws DataProviderException, UnsupportedActionException;

    /**
     * Some services may require that the snapshot is taken on the server side rather than on the client side. In such
     * case this methods is called by the client and should return the snapshot for the given beamline set at this
     * particular time. Usually this method would return the snapshot, but the snapshot itself is not yet stored into
     * permanently stored. In order to store it, {@link #saveSnapshot(VSnapshot, String)} still has to be called. This
     * method can only be called if {@link #isTaggingSupported()} returns true, otherwise it may throw
     * {@link UnsupportedActionException}.
     *
     * @param beamlineSet the beamline set for which the snapshot is taken
     * @return a snapshot
     * @throws DataProviderException in case of an error
     * @throws UnsupportedActionException if {@link #isTakingSnapshotsSupported()} returns false
     */
    default VSnapshot takeSnapshot(BeamlineSet beamlineSet) throws DataProviderException, UnsupportedActionException {
        throw new UnsupportedActionException("Service cannot take a snapshot.");
    }
}
