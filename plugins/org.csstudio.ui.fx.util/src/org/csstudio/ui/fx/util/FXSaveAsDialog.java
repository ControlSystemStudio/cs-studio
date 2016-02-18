package org.csstudio.ui.fx.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;

import javafx.scene.Scene;
import javafx.scene.control.Button;

/**
 *
 * <code>FXSaveAsDialog</code> is an extension of the {@link SaveAsDialog}, which provides JavaFX buttons.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FXSaveAsDialog extends SaveAsDialog {

    private Button okButton;

    /**
     * Constructs a new dialog.
     *
     * @param parentShell the parent shell
     */
    public FXSaveAsDialog(Shell parentShell) {
        super(parentShell);
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(""));
        setOriginalFile(file);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.dialogs.SaveAsDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns = 1;
        FXUtilities.createFXBridge(parent,this::createFxButtonBar);
    }

    private Scene createFxButtonBar(Composite parent) {
        okButton = FXUtilities.createButtonBarWithOKandCancel(e -> buttonPressed(e));
        return okButton.getScene();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.dialogs.SaveAsDialog#setDialogComplete(boolean)
     */
    @Override
    protected void setDialogComplete(boolean value) {
        okButton.setDisable(!value);
    }
}

