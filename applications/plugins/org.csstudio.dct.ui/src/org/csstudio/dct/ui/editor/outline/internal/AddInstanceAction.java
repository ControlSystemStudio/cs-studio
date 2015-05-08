package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.commands.AddInstanceCommand;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.util.ModelValidationUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

/**
 * Action that adds an instance.
 *
 * @author Sven Wende
 *
 */
public final class AddInstanceAction extends AbstractOutlineAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(List<IElement> selection) {
        assert selection != null;
        assert selection.size() == 1;
        assert selection.get(0) instanceof IFolder || selection.get(0) instanceof IContainer;

        IElement container = selection.get(0);

        CompoundCommand result = null;

        InstanceDialog rsd = new InstanceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getProject(),
                container instanceof IContainer ? (IContainer) container : null);

        if (rsd.open() == Window.OK) {
            result = new CompoundCommand("Add Instance");

            IPrototype prototype = (IPrototype) rsd.getSelection();

            IInstance instance = new Instance(prototype, UUID.randomUUID());

            if (container instanceof IFolder) {
                result.add(new AddInstanceCommand((IFolder) container, instance));
            } else if (container instanceof IContainer) {
                if (ModelValidationUtil.causesTransitiveLoop((IContainer) container, prototype)) {
                    MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
                            "An instance of the selected prototype cannot be inserted because this would cause a transitive relationship.");
                } else {
                    result.add(new AddInstanceCommand((IContainer) container, instance));
                }
            }

            result.add(new SelectInOutlineCommand(getOutlineView(), instance));

        }

        return result;
    }
}
