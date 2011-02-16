package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.editparts.GroupingContainerEditPart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;

/**The action will auto size the container according to the bounds of its children.
 * @author Xihui Chen
 *
 */
public class PerformAutoSizeAction extends AbstractWidgetTargetAction{


	public void run(IAction action) {
		if(getContainerEditpart().getChildren().size() <=0){
			return;
		}
		CompoundCommand compoundCommand = new CompoundCommand("Perform AutoSize");	
		
		AbstractContainerEditpart containerEditpart = getContainerEditpart();
		AbstractContainerModel containerModel = containerEditpart.getWidgetModel();
		
		
		//temporary unlock children so children will not be resized.
		if(containerEditpart instanceof GroupingContainerEditPart){				
			compoundCommand.add(new SetWidgetPropertyCommand(containerModel, 
					GroupingContainerModel.PROP_LOCK_CHILDREN, false));
		}			
		
		IFigure figure = getContainerFigure();

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, 
		maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		
		for(Object editpart : containerEditpart.getChildren()){
			AbstractWidgetModel widget = ((AbstractBaseEditPart)editpart).getWidgetModel();
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
		

		for(Object editpart : containerEditpart.getChildren()){
			AbstractWidgetModel widget = ((AbstractBaseEditPart)editpart).getWidgetModel();
			compoundCommand.add(new SetBoundsCommand(widget, new Rectangle(
					widget.getLocation().translate(tranlateSize.getNegated()), 
					widget.getSize())));
		}	
		
		//recover lock
		if(containerEditpart instanceof GroupingContainerEditPart){			
			Object oldvalue = containerEditpart.getWidgetModel()
			.getPropertyValue(GroupingContainerModel.PROP_LOCK_CHILDREN);
			compoundCommand.add(new SetWidgetPropertyCommand(containerModel,
					GroupingContainerModel.PROP_LOCK_CHILDREN, oldvalue));
		}
		
		execute(compoundCommand);	
		
	
	}


	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final AbstractContainerEditpart getContainerEditpart() {
		return (AbstractContainerEditpart)selection.getFirstElement();
	}

	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final IFigure getContainerFigure() {
		return ((AbstractContainerEditpart)selection.getFirstElement()).getFigure();
	}
		
		
		
}
