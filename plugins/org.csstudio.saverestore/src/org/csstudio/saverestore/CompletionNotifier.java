package org.csstudio.saverestore;

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

    void branchCreated(String newBranch);

    void synchronised();

    void beamlineSaved(BeamlineSetData set);

    void snapshotSaved(VSnapshot snapshot);

    void snapshotTagged(Snapshot snapshot);
}
