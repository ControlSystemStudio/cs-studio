package org.csstudio.opibuilder.widgets.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.commands.AddWidgetCommand;
import org.csstudio.opibuilder.commands.OrphanChildCommand;
import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;

/**The action will create a group which contains all the selected widgets.
 * @author Xihui Chen
 *
 */
public class CreateGroupAction extends AbstractWidgetTargetAction {

	
	public void run(IAction action) {
		if(!(targetPart instanceof OPIEditor))
			return;
		CompoundCommand compoundCommand = new CompoundCommand("Create Group");
		
		
		
		List<AbstractWidgetModel> originalSelectedWidgets = getSelectedWidgetModels();
		List<AbstractWidgetModel> selectedWidgets = new ArrayList<AbstractWidgetModel>();
		selectedWidgets.addAll(originalSelectedWidgets);		
		
		//remove the selected widgets which are children of another selected widget.
		for(AbstractWidgetModel widget : originalSelectedWidgets){
			if(widget instanceof DisplayModel){
				selectedWidgets.remove(widget);
				continue;
			}
			if(widget instanceof AbstractContainerModel){
				for(AbstractWidgetModel child : originalSelectedWidgets){
					if(((AbstractContainerModel)widget).getChildren().contains(child))
						selectedWidgets.remove(child);
				}
			}
			
		}
		
		int minDepth = Integer.MAX_VALUE;
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, 
			maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		AbstractWidgetModel minDepthWidget = selectedWidgets.get(0);
		
		for(AbstractWidgetModel widget : selectedWidgets){
			
			int leftX = widget.getLocation().x;
			int upY = widget.getLocation().y;
			int rightX = widget.getLocation().x + widget.getSize().width;
			int bottomY = widget.getLocation().y + widget.getSize().height;
			int depth = widget.getNestedDepth();
			if( leftX<minX)
				minX = leftX;
			if( upY < minY)
				minY = upY;
			if(rightX > maxX)
				maxX =rightX;
			if(bottomY > maxY)
				maxY = bottomY;			
			if(minDepth > depth){
				minDepth = depth;
				minDepthWidget = widget;
			}
		
			compoundCommand.add(new OrphanChildCommand(widget.getParent(), widget));
		}
		
		
		
		GroupingContainerModel groupingContainerModel = new GroupingContainerModel();
		// the parent should be the widget with minimum nested depth
		AbstractContainerModel parent = minDepthWidget.getParent();		
		
		int borderWidth = 1;
		
		if(groupingContainerModel.getBorderStyle()== BorderStyle.GROUP_BOX)
			borderWidth = 30;
			
		compoundCommand.add(new WidgetCreateCommand(groupingContainerModel,
				parent, new Rectangle(minX, minY, maxX-minX + borderWidth, maxY-minY + borderWidth), false));
		
		
		for(AbstractWidgetModel widget : selectedWidgets){
			compoundCommand.add(new AddWidgetCommand(groupingContainerModel, widget));
			compoundCommand.add(new SetBoundsCommand(widget, 
					new Rectangle(widget.getLocation().translate(-minX, -minY), widget.getSize())));
		}
		
		execute(compoundCommand);		
	}
	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
	
		List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();
	
		for (Object o : selection.toList()) {
			if (o instanceof AbstractBaseEditPart) {
				selectedWidgetModels.add(((AbstractBaseEditPart) o)
						.getWidgetModel());
			}
		}
		return selectedWidgetModels;
	}

}
