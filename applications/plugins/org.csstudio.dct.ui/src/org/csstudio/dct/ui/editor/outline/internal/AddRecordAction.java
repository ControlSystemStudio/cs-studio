package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;

/**
 * Popup menu action for the outline view that creates new records.
 * 
 * @author Sven Wende
 * 
 */
public class AddRecordAction extends AbstractOutlineAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(IElement selection) {
		Command command = null;

		if (selection instanceof IRecordContainer) {
			// TODO: 25.11.2008: Sven Wende: Recordtyp und Name des neuen Records in Dialog erfragen!
			command = new AddRecordCommand((IRecordContainer) selection, RecordFactory.createRecord("ai", "new record"));
		}

		return command;
	}

}
