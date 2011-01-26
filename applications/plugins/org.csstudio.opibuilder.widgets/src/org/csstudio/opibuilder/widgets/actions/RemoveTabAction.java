package org.csstudio.opibuilder.widgets.actions;
import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;


/**Remove the current tab.
 * @author Xihui Chen
 *
 */
public class RemoveTabAction extends AbstractWidgetTargetAction {

	

	public void run(IAction action) {
		Command command = new RemoveTabCommand(getSelectedTabWidget());
			execute(command);
		
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
