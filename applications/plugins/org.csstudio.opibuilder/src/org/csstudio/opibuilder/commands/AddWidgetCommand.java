package org.csstudio.opibuilder.commands;

import java.util.Arrays;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;

/**Add widgets to container 
 * @author Xihui Chen
 *
 */
public class AddWidgetCommand extends Command {

	private AbstractContainerModel containerModel;
	
	private List<AbstractWidgetModel> widgets;

	public AddWidgetCommand(AbstractContainerModel containerModel,
			List<AbstractWidgetModel> widgets) {
		this.containerModel = containerModel;
		this.widgets = widgets;
	}
	
	
	public AddWidgetCommand(final AbstractContainerModel containerModel, final AbstractWidgetModel widget){
		this(containerModel, Arrays.asList(widget));
	}
	
	
	@Override
	public void execute() {
		for(AbstractWidgetModel child : widgets)
			containerModel.addChild(child);
	}
	
	@Override
	public void undo() {
		for(AbstractWidgetModel child : widgets)
			containerModel.removeChild(child);
	}
	
	
	
	
	
	
}
