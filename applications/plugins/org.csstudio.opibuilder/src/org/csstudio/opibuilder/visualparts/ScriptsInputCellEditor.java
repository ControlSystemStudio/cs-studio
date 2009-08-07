package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.properties.support.ScriptsInput;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ScriptsInputCellEditor extends AbstractDialogCellEditor {
	
	private ScriptsInput scriptsInput;

	public ScriptsInputCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean shouldFireChanges() {
		return scriptsInput != null;
	}

	@Override
	protected Object doGetValue() {
		return scriptsInput;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof ScriptsInput))
			scriptsInput = new ScriptsInput();
		else
			scriptsInput = (ScriptsInput)value;
			
	}

}
