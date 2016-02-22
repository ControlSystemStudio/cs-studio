package org.csstudio.saverestore.ui;

import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.ui.fx.util.FXTextAreaInputDialog;
import org.eclipse.ui.IWorkbenchPart;

/**
 *
 * <code>SaveSetController</code> is the controller part for the save set editor. It provides the logic required
 * by the editor.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SaveSetController {

    private final IWorkbenchPart owner;
    private SaveSetData selectedData;

    /**
     * Constructs a new controller.
     *
     * @param owner the part that owns this control
     */
    public SaveSetController(IWorkbenchPart owner) {
        this.owner = owner;
    }

    /**
     * Stores the save set data to the repository. Before triggering the actual store the user is prompt for a short
     * comment describing the changes in the save set. This method should never be called on the UI thread.
     *
     * @param data the data to store
     * @return the stored data if successful or an empty object if unsuccessful
     */
    public Optional<SaveSetData> save(final SaveSetData data) {
        String providerId = data.getDescriptor().getDataProviderId();
        DataProvider provider = SaveRestoreService.getInstance().getDataProvider(providerId).getProvider();
        if (!provider.isSaveSetSavingSupported()) {
            return Optional.empty();
        }
        Optional<String> comment = FXTextAreaInputDialog.get(owner.getSite().getShell(), "Save Set Comment",
            "Provide a short comment of the changes to the save set " + data.getDescriptor().getDisplayName(), "",
            e -> (e == null || e.trim().length() < 10) ? "Comment should be at least 10 characters long." : null);
        return comment.map(c -> {
            try {
                SaveSetData dd = provider.saveSaveSet(data, c);
                SaveRestoreService.LOGGER.log(Level.FINE, "Successfully saved the save set {0}.",
                    new Object[] { data.getDescriptor().getFullyQualifiedName() });
                return dd;
            } catch (DataProviderException ex) {
                ActionManager.reportException(ex, owner.getSite().getShell());
                return null;
            }
        });
    }

    /**
     * Set the save set data, which the editor currently displays. The object always represents the saved instance;
     * therefore this is either the object which was delivered at the time when the editor was opened, or created when
     * the editor was last saved. This method is not thread safe.
     *
     * @param data the data
     */
    public void setSavedSaveSetData(SaveSetData data) {
        this.selectedData = data;
    }

    /**
     * Returns the saved data of this editor - either the data provided when editor was opened or data created when
     * editor was saved. This method is not thread safe.
     *
     * @return the saved save set data
     */
    public Optional<SaveSetData> getSavedSaveSetData() {
        return Optional.ofNullable(this.selectedData);
    }
}
