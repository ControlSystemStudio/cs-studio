package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IInstanceContainer;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.commands.AddInstanceCommand;
import org.csstudio.dct.model.internal.Instance;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

/**
 * Undoable command which adds a new instance to the model.
 * 
 * @author Sven Wende
 * 
 */
public class AddInstanceAction extends AbstractOutlineAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(IElement selection) {
		Command result = null;

		PrototypeSelectionDialog rsd = new PrototypeSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "LOS",
				getProject());

		if (rsd.open() == Window.OK) {
			IPrototype prototype = (IPrototype) rsd.getSelection();

			IInstance instance = new Instance(prototype);
			
			if (selection instanceof IFolder) {
				result = new AddInstanceCommand((IFolder) selection, instance);
			} else if (selection instanceof IContainer) {
				result = new AddInstanceCommand((IContainer) selection, instance);
			}
		}

		return result;
	}

}
