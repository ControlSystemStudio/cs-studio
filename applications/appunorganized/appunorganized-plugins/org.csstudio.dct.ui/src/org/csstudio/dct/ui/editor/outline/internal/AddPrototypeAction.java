package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.internal.Prototype;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Popup menu action for the outline view that creates a new prototype.
 *
 * @author Sven Wende
 *
 */
public final class AddPrototypeAction extends AbstractOutlineAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(List<IElement> selection) {
        assert selection != null;
        assert selection.size() == 1;
        assert selection.get(0) instanceof IFolder;

        CompoundCommand result = null;

        InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), "Prototype Name",
                "Please enter a name for the new prototype:", "New Prototype", new IInputValidator() {
                    @Override
                    public String isValid(String newText) {
                        if (newText == null || newText.trim().length() <= 0) {
                            return "Please enter a valid name";
                        }
                        return null;
                    }
                });

        if (dialog.open() == Dialog.OK) {
            result = new CompoundCommand();
            Prototype prototype = new Prototype(dialog.getValue(), UUID.randomUUID());
            result.add(new AddPrototypeCommand((IFolder) selection.get(0), prototype));
            result.add(new SelectInOutlineCommand(getOutlineView(), prototype));
        }

        return result;
    }
}
