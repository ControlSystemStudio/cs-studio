package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cell editor for OPIColor.
 * @author Xihui Chen
 *
 */
public class OPIColorCellEditor extends AbstractDialogCellEditor {

	private OPIColor opiColor;
	
	
	public OPIColorCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		OPIColorDialog dialog = 
			new OPIColorDialog(parentShell, opiColor, dialogTitle);
		if(dialog.open() == Window.OK)
			opiColor = dialog.getOutput();
	}

	@Override
	protected boolean shouldFireChanges() {
		return opiColor != null;
	}

	@Override
	protected Object doGetValue() {
		return opiColor;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof OPIColor))
			opiColor = new OPIColor("unknown");
		else
			opiColor = (OPIColor)value;
	}

}
