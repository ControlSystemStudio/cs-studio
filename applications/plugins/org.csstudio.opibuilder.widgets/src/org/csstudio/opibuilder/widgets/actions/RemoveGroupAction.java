package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.commands.AddWidgetCommand;
import org.csstudio.opibuilder.commands.OrphanChildCommand;
import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.commands.WidgetDeleteCommand;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.editparts.GroupingContainerEditPart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;

/**The action will remove a group and move all the selected widgets to the group's parent.
 * @author Xihui Chen
 *
 */
public class RemoveGroupAction extends AbstractWidgetTargetAction{


	public void run(IAction action) {
		CompoundCommand compoundCommand = new CompoundCommand("Remove Group");	
		
		GroupingContainerModel containerModel = getSelectedContainer();
		
		Point leftCorner = containerModel.getLocation();
		for(AbstractWidgetModel widget : containerModel.getChildren()){			
			compoundCommand.add(new OrphanChildCommand(containerModel, widget));
			compoundCommand.add(new AddWidgetCommand(containerModel.getParent(), widget));
			compoundCommand.add(new SetBoundsCommand(widget, 
					new Rectangle(widget.getLocation(), widget.getSize()).translate(leftCorner)));		
		}		
		compoundCommand.add(new WidgetDeleteCommand(containerModel.getParent(), containerModel));
		if(targetPart instanceof OPIEditor){
			execute(compoundCommand);
		}
	}

	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final GroupingContainerModel getSelectedContainer() {
		return ((GroupingContainerEditPart)selection.getFirstElement()).getWidgetModel();
	}

}
