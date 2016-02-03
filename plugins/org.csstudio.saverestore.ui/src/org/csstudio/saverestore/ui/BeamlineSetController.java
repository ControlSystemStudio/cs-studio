package org.csstudio.saverestore.ui;

import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.ui.fx.util.FXTextAreaInputDialog;
import org.eclipse.ui.IWorkbenchPart;

/**
 *
 * <code>BeamlineSetController</code> is the controller part for the beamline set editor. It provides the logic required
 * by the editor.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BeamlineSetController {

    private final IWorkbenchPart owner;
    private BeamlineSetData selectedData;

    /**
     * Constructs a new controller.
     *
     * @param owner the part that owns this control
     */
    public BeamlineSetController(IWorkbenchPart owner) {
        this.owner = owner;
    }

    /**
     * Stores the beamline set data to the repository. Before triggering the actual store the user is prompt for a short
     * comment describing the changes in the beamline set. This method should never be called on the UI thread.
     *
     * @param data the data to store
     * @return the stored data if successful or an empty object if unsuccessful
     */
    public Optional<BeamlineSetData> save(final BeamlineSetData data) {
        String providerId = data.getDescriptor().getDataProviderId();
        DataProvider provider = SaveRestoreService.getInstance().getDataProvider(providerId).provider;
        if (!provider.isBeamlineSetSavingSupported()) {
            return Optional.empty();
        }
        Optional<String> comment = FXTextAreaInputDialog.get(owner.getSite().getShell(), "Beamline Set Comment",
            "Provide a short comment of the changes to the beamline set " + data.getDescriptor().getDisplayName(), "",
            e -> (e == null || e.trim().length() < 10) ? "Comment should be at least 10 characters long." : null);
        return comment.map(c -> {
            try {
                BeamlineSetData dd = provider.saveBeamlineSet(data, c);
                SaveRestoreService.LOGGER.log(Level.FINE,
                    "Successfully saved the beamline set '" + data.getDescriptor().getFullyQualifiedName() + "'");
                return dd;
            } catch (DataProviderException ex) {
                ActionManager.reportException(ex, owner.getSite().getShell());
                return null;
            }
        });
    }

    /**
     * Set the beamline set data, which the editor currently displays. The object always represents the saved instance;
     * therefore this is either the object which was delivered at the time when the editor was opened, or created when
     * the editor was last saved. This method is not thread safe.
     *
     * @param data the data
     */
    public void setSavedBeamlineSetData(BeamlineSetData data) {
        this.selectedData = data;
    }

    /**
     * Returns the saved data of this editor - either the data provided when editor was opened or data created when
     * editor was saved. This method is not thread safe.
     *
     * @return the saved beamline set data
     */
    public Optional<BeamlineSetData> getSavedBeamlineSetData() {
        return Optional.ofNullable(this.selectedData);
    }
}
