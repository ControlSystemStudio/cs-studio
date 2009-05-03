package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;
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
	protected Command createCommand(List<IElement> selection) {
		assert selection != null;
		assert selection.size() == 1;

		IElement element = selection.get(0);
		
		Command command = null;

		if (element instanceof IFolder) {
			command = new RemoveFolderCommand((IFolder) element);
		} else if (element instanceof IPrototype) {
			command = new RemovePrototypeCommand((IPrototype) element);
		} else if (element instanceof IInstance) {
			command = new RemoveInstanceCommand((IInstance) element);
		} else if (element instanceof IRecord) {
			command = new RemoveRecordCommand((IRecord) element);
		}

		return command;
	}

}
