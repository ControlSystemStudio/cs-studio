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
package org.csstudio.saverestore;

import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;

/**
 *
 * <code>CompletionNotifier</code> is a callback that is notified whenever a specific action is completed by the data
 * provider. When a specific UI part will trigger an action in the data provider, the data provider will complete the
 * action and notify the registered listeners about the completion of the event. The listener may take additional
 * actions to refresh the view.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface CompletionNotifier {

    /**
     * Called whenever a new branch was created.
     *
     * @param newBranch the new branch
     */
    default void branchCreated(Branch newBranch) {
    }

    /**
     * Called whenever the repository was synchronised and as a consequence of the synchronisation the repository
     * changed. This event could override any other events.
     */
    default void synchronised() {
    }

    /**
     * Called whenever the save set was saved, but only if at the same time no updates due to synchronisation were made.
     * If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param set the set that was saved
     */
    default void saveSetSaved(SaveSetData set) {
    }

    /**
     * Called whenever the save set is successfully deleted, but only if at the same time no updates due to
     * synchronisation were made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param set the set that was deleted
     */
    default void saveSetDeleted(SaveSet set) {
    }

    /**
     * Called whenever the snapshot was saved, but only if at the same time no updates due to synchronisation were made.
     * If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param snapshot the saved snapshot
     */
    default void snapshotSaved(VSnapshot snapshot) {
    }

    /**
     * Called whenever the snapshot was tagged, but only if at the same time no updates due to synchronisation were
     * made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param snapshot the snapshot that was tagged
     */
    default void snapshotTagged(Snapshot snapshot) {
    }

    /**
     * Called whenever the data was imported, but only if at the same time no updates due to synchronisation were made.
     * If the repository was also updated during the import action (e.g. new data fetched from central service) only
     * {@link #synchronised()} is called.
     *
     * @param source the source of data
     * @param toBranch the destination branch
     * @param toBase the destination base level
     */
    default void dataImported(SaveSet source, Branch toBranch, Optional<BaseLevel> toBase) {
    }
}
