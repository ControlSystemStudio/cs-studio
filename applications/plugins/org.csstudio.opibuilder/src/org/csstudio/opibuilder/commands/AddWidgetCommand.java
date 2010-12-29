package org.csstudio.opibuilder.commands;

import java.util.Arrays;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractLayoutModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;

/**Add widgets to container 
 * @author Sven Wende & Stefan Hofer (class of same name in SDS)
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
		for(AbstractWidgetModel child : widgets){
			if(child instanceof AbstractLayoutModel && containerModel
					.getLayoutWidget() != null){
				MessageDialog.openError(null, "Creating widget failed", 
						"There is already a layout widget in the container. " +
						"Please delete it before you can add a new layout widget.");
				return;
			}
			containerModel.addChild(child);
		}
	}
	
	@Override
	public void undo() {
		for(AbstractWidgetModel child : widgets)
			containerModel.removeChild(child);
	}
	
	
	
	
	
	
}
