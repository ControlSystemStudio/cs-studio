package org.csstudio.opibuilder.visualparts;

import java.util.HashMap;

import org.csstudio.opibuilder.util.MacrosInput;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cellEditor for macros property descriptor.
 * @author Xihui Chen
 *
 */
public class MacrosCellEditor extends AbstractDialogCellEditor {
	
	private MacrosInput macrosInput;

	public MacrosCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
			
		MacrosInputDialog dialog = 
			new MacrosInputDialog(parentShell, macrosInput, dialogTitle);
		if(dialog.open() == Window.OK){
			macrosInput = dialog.getResult();			
		}
	}

	@Override
	protected boolean shouldFireChanges() {
		return macrosInput != null;
	}

	@Override
	protected Object doGetValue() {
		return macrosInput;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof MacrosInput))
			macrosInput = new MacrosInput(new HashMap<String, String>(), true);
		else
			macrosInput = (MacrosInput)value;
			
	}

}
