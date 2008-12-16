package org.csstudio.dct.ui.editor.outline.internal;

import java.util.UUID;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

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

		RecordTypeSelectionDialog rsd = new RecordTypeSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "LOS",
				getProject().getDatabaseDefinition().getRecordDefinitions());

		if (selection instanceof IRecordContainer) {
			if (rsd.open() == Window.OK) {
				IRecordDefinition rd = rsd.getSelection();
				command = new AddRecordCommand((IRecordContainer) selection, RecordFactory.createRecord(getProject(), rd.getType(),
						"new record", UUID.randomUUID()));
			}
		}
		return command;
	}

}
