package org.csstudio.saverestore.ui;

import java.util.Optional;

import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.ui.fx.util.FXTextAreaInputDialog;
import org.eclipse.ui.IWorkbenchPart;

/**
 *
 * <code>BeamlineSetController</code> is the controller part for the beamline set editor. It provides the logic
 * required by the editor.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BeamlineSetController {

    private IWorkbenchPart owner;

    /**
     * Constructs a new controller.
     *
     * @param owner the part that owns this control
     */
    public BeamlineSetController(IWorkbenchPart owner) {
        this.owner = owner;
    }

    /**
     * Stores the beamline set data to the repository. Before triggering the actual store the user is prompt for
     * a short comment describing the changes in the beamline set. This method should never be called on the UI
     * thread.
     *
     * @param data the data to store
     * @return the stored data if successful or an empty object if unsuccessful
     */
    public Optional<BeamlineSetData> save(final BeamlineSetData data) {
        Optional<String> comment = FXTextAreaInputDialog.get(owner.getSite().getShell(), "Beamline Set Comment",
                "Provide a short comment of the changes to the beamline set " + data.getDescriptor().getDisplayName(), "",
                e -> (e == null || e.trim().length() < 10) ? "Comment should be at least 10 characters long." : null);
        return comment.map(c -> {
            try {
                String providerId = data.getDescriptor().getDataProviderId();
                return SaveRestoreService.getInstance().getDataProvider(providerId).provider.saveBeamlineSet(data, c);
            } catch (DataProviderException ex) {
                Selector.reportException(ex, owner.getSite().getShell());
                return null;
            }
        });

    }
}
