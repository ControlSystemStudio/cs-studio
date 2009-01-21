package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.RemoveFolderCommand;
import org.csstudio.dct.model.commands.RemoveInstanceCommand;
import org.csstudio.dct.model.commands.RemovePrototypeCommand;
import org.csstudio.dct.model.commands.RemoveRecordCommand;
import org.eclipse.gef.commands.Command;

/**
 * Popup menu action for the outline view that removes elements from the model.
 * 
 * @author Sven Wende
 * 
 */
public final class RemoveAction extends AbstractOutlineAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(IElement selection) {
		Command command = null;

		if (selection instanceof IFolder) {
			command = new RemoveFolderCommand((IFolder) selection);
		} else if (selection instanceof IPrototype) {
			command = new RemovePrototypeCommand((IPrototype) selection);
		} else if (selection instanceof IInstance) {
			command = new RemoveInstanceCommand((IInstance) selection);
		} else if (selection instanceof IRecord) {
			command = new RemoveRecordCommand((IRecord) selection);
		}

		return command;
	}

}
