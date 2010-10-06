package org.csstudio.opibuilder.widgets.actions;
import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;


/**Add a tab after the current tab.
 * @author Xihui Chen
 *
 */
public class AddTabAfterAction extends AbstractWidgetTargetAction {

	

	public void run(IAction action) {
		Command command = new AddTabCommand(getSelectedTabWidget(), false);
		if(targetPart instanceof OPIEditor){
			execute(command);
		}
	}

	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final TabEditPart getSelectedTabWidget() {
		return (TabEditPart)selection.getFirstElement();
	}
}
