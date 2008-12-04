package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.internal.Prototype;
import org.eclipse.gef.commands.Command;

/**
 * Popup menu action for the outline view that creates new prototypes.
 * 
 * @author Sven Wende
 * 
 */
public class AddPrototypeAction extends AbstractOutlineAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(IElement selection) {
		Command result = null;

		if (selection instanceof IFolder) {
			result = new AddPrototypeCommand((IFolder) selection, new Prototype("neu"));
		}

		return result;
	}
}
