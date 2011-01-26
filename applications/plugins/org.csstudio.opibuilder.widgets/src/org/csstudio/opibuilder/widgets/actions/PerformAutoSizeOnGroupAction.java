package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.editparts.GroupingContainerEditPart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;

/**The action will auto size the group according its children.
 * @author Xihui Chen
 *
 */
public class PerformAutoSizeOnGroupAction extends AbstractWidgetTargetAction{


	public void run(IAction action) {
		if(getContainerModel().getChildren().size() <=0){
			return;
		}
		CompoundCommand compoundCommand = new CompoundCommand("Perform AutoSize");	
		
		GroupingContainerModel containerModel = getContainerModel();
		IFigure figure = getContainerFigure();

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, 
		maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		
		for(AbstractWidgetModel widget : containerModel.getChildren()){
			int leftX = widget.getLocation().x;
			int upY = widget.getLocation().y;
			int rightX = widget.getLocation().x + widget.getSize().width;
			int bottomY = widget.getLocation().y + widget.getSize().height;
			if( leftX<minX)
				minX = leftX;
			if( upY < minY)
				minY = upY;
			if(rightX > maxX)
				maxX =rightX;
			if(bottomY > maxY)
				maxY = bottomY;	
			
	
		}
		Point tranlateSize = new Point(minX,minY);
		
		compoundCommand.add(new SetBoundsCommand(containerModel, 
				new Rectangle(containerModel.getLocation().translate(tranlateSize), new Dimension(
						maxX - minX + figure.getInsets().left + figure.getInsets().right,
						maxY - minY + figure.getInsets().top + figure.getInsets().bottom))));
		

		for(AbstractWidgetModel widget : containerModel.getChildren()){
			compoundCommand.add(new SetBoundsCommand(widget, new Rectangle(
					widget.getLocation().translate(tranlateSize.getNegated()), 
					widget.getSize())));
		}
		
			execute(compoundCommand);
		
	}


	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final GroupingContainerModel getContainerModel() {
		return ((GroupingContainerEditPart)selection.getFirstElement()).getWidgetModel();
	}

	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final IFigure getContainerFigure() {
		return ((GroupingContainerEditPart)selection.getFirstElement()).getFigure();
	}
		
		
		
}
