package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ActionsCellEditor extends AbstractDialogCellEditor {

	private ActionsInput actionsInput;
	
	
	public ActionsCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		ActionsInputDialog dialog = 
			new ActionsInputDialog(parentShell, actionsInput, dialogTitle);
		if(dialog.open() == Window.OK)
			actionsInput = dialog.getOutput();
	}

	@Override
	protected boolean shouldFireChanges() {
		return actionsInput != null;
	}

	@Override
	protected Object doGetValue() {
		return actionsInput;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof ActionsInput))
			actionsInput = new ActionsInput();
		else
			actionsInput = (ActionsInput)value;
	}

}
