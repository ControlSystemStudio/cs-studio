package org.csstudio.opibuilder.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**The command to add a widget to a container.
 * @author Xihui Chen
 *
 */
public class WidgetCreateCommand extends Command {
	
	private AbstractWidgetModel newWidget;
	
	private final AbstractContainerModel container;
	
	private Rectangle bounds;
	

	public WidgetCreateCommand(AbstractWidgetModel newWidget, AbstractContainerModel
			container, Rectangle bounds) {
		this.newWidget = newWidget;
		this.container  = container;
		this.bounds = bounds;
		setLabel("create widget");
	}
	
	@Override
	public boolean canExecute() {
		return newWidget != null && container != null && bounds != null;
	}
	
	@Override
	public void execute() {
		newWidget.setLocation(bounds.x, bounds.y);
		if (bounds.width > 0 && bounds.height > 0)
		newWidget.setSize(bounds.width, bounds.height);			
		redo();
	}
	
	@Override
	public void redo() {
		Map<String, Integer> typeIDMap = new HashMap<String, Integer>();
		for(AbstractWidgetModel child : container.getChildren()){
			if(typeIDMap.containsKey(child.getTypeID()))
				typeIDMap.put(child.getTypeID(), typeIDMap.get(child.getTypeID())+1);
			else
				typeIDMap.put(child.getTypeID(), 0);
		}
		newWidget.setName(newWidget.getType() + "_" 	//$NON-NLS-1$
				+ (typeIDMap.get(newWidget.getTypeID())==null?
						0 : typeIDMap.get(newWidget.getTypeID()) + 1)); 
		container.addChild(newWidget);
	}
	
	@Override
	public void undo() {
		container.removeChild(newWidget);
	}
}
