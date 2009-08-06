package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.actions.ActionFactory;

public class CutWidgetsAction extends CopyWidgetsAction {

	private DeleteAction deleteAction;
	
	public CutWidgetsAction(OPIEditor part, DeleteAction deleteAction, PasteWidgetsAction pasteWidgetsAction ) {
		super(part, pasteWidgetsAction);
		this.deleteAction = deleteAction;
		setText("Cut");
		setActionDefinitionId("org.eclipse.ui.edit.cut"); //$NON-NLS-1$
		setId(ActionFactory.CUT.getId());
	}
	
	@Override
	public void run() {
		super.run();
		deleteAction.run();
	}

}
