package org.csstudio.dct.ui.editor.outline.internal;

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
	protected Command createCommand(IElement selection) {
		Command result = null;

		PrototypeSelectionDialog rsd = new PrototypeSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Available Prototypes:", getProject(), selection instanceof IContainer ? (IContainer) selection : null);

		if (rsd.open() == Window.OK) {
			IPrototype prototype = (IPrototype) rsd.getSelection();

			IInstance instance = new Instance(prototype, UUID.randomUUID());

			if (selection instanceof IFolder) {
				result = new AddInstanceCommand((IFolder) selection, instance);
			} else if (selection instanceof IContainer) {
				if (ModelValidationUtil.causesTransitiveLoop((IContainer) selection, prototype)) {
					MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
							"An instance of the selected prototype cannot be inserted because this would cause a transitive relationship.");
				} else {
					result = new AddInstanceCommand((IContainer) selection, instance);
				}
			}
		}

		return result;
	}

}
