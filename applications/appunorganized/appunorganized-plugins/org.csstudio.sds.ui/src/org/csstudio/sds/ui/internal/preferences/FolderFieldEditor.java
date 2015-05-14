/**
 *
 */
package org.csstudio.sds.ui.internal.preferences;

import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

public class FolderFieldEditor extends PathEditor {

    /**
     * Creates a path field editor.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public FolderFieldEditor(String name, String labelText, Composite parent) {
        init(name, labelText);
        createControl(parent);
    }

    @Override
    protected String getNewInputObject() {
        ResourceSelectionDialog resourceSelectionDialog = new ResourceSelectionDialog(getShell(), "Select a folder which contains rules", null);
        if (resourceSelectionDialog.open() == Window.OK) {
            IPath selectedResource = resourceSelectionDialog.getSelectedResource();
            return selectedResource.toString();
        }
        return null;
    }

}