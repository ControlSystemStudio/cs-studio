package org.csstudio.opibuilder.widgets.properties;

import org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cell editor for {@link ColorMap}
 * @author Xihui Chen
 *
 */
public class ColorMapCellEditor extends AbstractDialogCellEditor {

	private ColorMap colorMap;
	
	public ColorMapCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		ColorMapEditDialog dialog = 
			new ColorMapEditDialog(parentShell, colorMap, dialogTitle);
		if(dialog.open() == Window.OK)
			colorMap = dialog.getOutput();
	}

	@Override
	protected boolean shouldFireChanges() {
		return colorMap != null;
	}

	@Override
	protected Object doGetValue() {
		return colorMap;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof ColorMap))
			colorMap = new ColorMap(PredefinedColorMap.GrayScale, true, true);
		else
			colorMap = (ColorMap)value;
	}

}
