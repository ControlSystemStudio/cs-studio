package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.editparts.AbstractLayoutEditpart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * The common code for {@link LayoutWidgetsAction} and {@link LayoutWidgetsHandler}.
 * @author Xihui Chen
 *
 */
public class LayoutWidgetsImp {

	
	public  static void run(AbstractLayoutEditpart layoutWidget, CommandStack commandStack) {

		AbstractContainerModel container = layoutWidget.getWidgetModel().getParent();
		
		List<AbstractWidgetModel> modelChildren = new ArrayList<AbstractWidgetModel>();
		modelChildren.addAll(container.getChildren());
		modelChildren.remove(layoutWidget.getWidgetModel());
		
		if(modelChildren.size() ==0)
			return;
		
		List<Rectangle> newBounds = 
			layoutWidget.getNewBounds(modelChildren, container.getBounds());
		
		CompoundCommand compoundCommand = new CompoundCommand("Layout Widgets");
		
		int i=0;
		for(AbstractWidgetModel model : modelChildren){
			compoundCommand.add(new SetBoundsCommand(model, newBounds.get(i)));
			i++;
		}
		
		commandStack.execute(compoundCommand);
		
	}
	
	
}
