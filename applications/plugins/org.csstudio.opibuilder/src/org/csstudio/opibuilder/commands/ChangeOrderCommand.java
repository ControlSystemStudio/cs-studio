package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.commands.Command;

/**The command to change the order of a child.
 * @author Xihui Chen
 *
 */
public class ChangeOrderCommand extends Command {

	private int newIndex;
	
	private int oldIndex;
	
	private AbstractContainerModel container;
	
	private AbstractWidgetModel widget;

	public ChangeOrderCommand(int newIndex, AbstractContainerModel container,
			AbstractWidgetModel widget) {
		Assert.isNotNull(container);
		Assert.isNotNull(widget);
		this.newIndex = newIndex;
		this.container = container;
		this.widget = widget;		
	}
	
	
	@Override
	public void execute() {
		oldIndex = container.getIndexOf(widget);
		container.changeChildOrder(widget, newIndex);
	}
	
	
	@Override
	public void undo() {
		container.changeChildOrder(widget, oldIndex);
	}
	
	
	
	
}
