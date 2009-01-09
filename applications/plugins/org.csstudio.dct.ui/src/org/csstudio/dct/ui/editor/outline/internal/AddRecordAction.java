package org.csstudio.dct.ui.editor.outline.internal;

import java.util.UUID;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IContainer;
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

		RecordTypeSelectionDialog rsd = new RecordTypeSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Please select the record type:",
				getProject().getDatabaseDefinition().getRecordDefinitions());

		if (selection instanceof IRecordContainer) {
			if (rsd.open() == Window.OK) {
				IRecordDefinition rd = rsd.getSelection();

				if (rd != null) {
					command = new AddRecordCommand((IContainer) selection, RecordFactory.createRecord(getProject(), rd.getType(),
							"new record", UUID.randomUUID()));
				}
			}
		}
		return command;
	}

}
