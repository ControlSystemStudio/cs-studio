package org.csstudio.opibuilder.commands;

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
		newWidget.setSize(bounds.width, bounds.height);
		redo();
	}
	
	@Override
	public void redo() {
		container.addChild(newWidget);
	}
	
	@Override
	public void undo() {
		container.removeChild(newWidget);
	}
}
