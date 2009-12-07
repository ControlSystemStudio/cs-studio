package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cell editor for scripts input.
 * @author Xihui Chen
 *
 */
public class ScriptsInputCellEditor extends AbstractDialogCellEditor {
	
	private ScriptsInput scriptsInput;
	
	private AbstractWidgetModel widgetModel;

	public ScriptsInputCellEditor(Composite parent,final AbstractWidgetModel widgetModel, String title) {
		super(parent, title);
		this.widgetModel = widgetModel;
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		ScriptsInputDialog dialog = 
			new ScriptsInputDialog(parentShell, scriptsInput,
					widgetModel.getRootDisplayModel().getOpiFilePath().removeLastSegments(1), dialogTitle);
		if(dialog.open() == Window.OK){
			scriptsInput = new ScriptsInput(dialog.getScriptDataList());
		}

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
