package org.csstudio.saverestore;

import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;

/**
 *
 * <code>CompletionNotifier</code> is a callback that is notified whenever a specific action is completed by
 * the data provider. When a specific UI part will trigger an action in the data provider, the data provider will
 * complete the action and notify the registered listeners about the completion of the event. The listener may
 * take additional actions to refresh the view.
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
    void branchCreated(Branch newBranch);

    /**
     * Called whenever the repository was synchronised and as a consequence of the synchronisation the
     * repository changed. This event could override any other events.
     */
    void synchronised();

    /**
     * Called whenever the beamline set was saved, but only if at the same time no updates due to synchronisation were
     * made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param set the set that was saved
     */
    void beamlineSaved(BeamlineSetData set);

    /**
     * Called whenever the beamline set is successfully deleted, but only if at the same time no updates due to synchronisation were
     * made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param set the set that was deleted
     */
    void beamlineDeleted(BeamlineSet set);

    /**
     * Called whenever the snapshot was saved, but only if at the same time no updates due to synchronisation were
     * made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param snapshot the saved snapshot
     */
    void snapshotSaved(VSnapshot snapshot);

    /**
     * Called whenever the snapshot was tagged, but only if at the same time no updates due to synchronisation were
     * made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param snapshot the snapshot that was tagged
     */
    void snapshotTagged(Snapshot snapshot);
}
