package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.actions.ActionFactory;

public class CutWidgetsAction extends CopyWidgetsAction {

	private static final String ID = "org.csstudio.opibuilder.actions.PasteWidgetsAction";
	private DeleteAction deleteAction;
	
	public CutWidgetsAction(OPIEditor part, DeleteAction deleteAction ) {
		super(part);
		this.deleteAction = deleteAction;
		setId(ID);
		setText("Cut");
		setActionDefinitionId(ActionFactory.CUT.getId());
		setId(ActionFactory.CUT.getId());
	}
	
	@Override
	public void run() {
		super.run();
		deleteAction.run();
	}

}
