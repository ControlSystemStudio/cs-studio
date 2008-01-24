package org.csstudio.sds.ui.internal.actions;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.internal.commands.AddWidgetCommand;
import org.csstudio.sds.ui.internal.commands.DeleteElementCommand;
import org.csstudio.sds.ui.internal.commands.SetPropertyCommand;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * The superclass for all grouping actions. 
 * @author Kai Meyer
 *
 */
public abstract class AbstractGroupingAction extends Action {
	
	/**
	 * the {@link CommandStack}.
	 */
	private CommandStack _commandStack;
	
	/**
	 * The {@link ContainerModel}, which is created or removed.
	 */
	private ContainerModel _containerModel;
	
	/**
	 * Constructor.
	 * @param label The displayed text for the action
	 * @param icon The icon for the action
	 * @param cmdStack the {@link CommandStack} for the internal {@link Command}
	 */
	public AbstractGroupingAction(final String label, final ImageDescriptor icon, final CommandStack cmdStack) {
		assert cmdStack!=null : "CommandStack was null";
		this.setText(label);
		if (icon!=null) {
			this.setImageDescriptor(icon);
		}
		_commandStack = cmdStack;
	}
	
	/**
	 * Sets the {@link ContainerModel} which should be created or removed.
	 * @param containerModel The {@link ContainerModel} 
	 */
	protected final void setContainerModel(final ContainerModel containerModel) {
		_containerModel = containerModel;
	}
	
	/**
	 * Return the {@link ContainerModel} which should be created or removed.
	 * @return The {@link ContainerModel}
	 */
	protected final ContainerModel getContainerModel() {
		return _containerModel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void run() {
		Command cmd = this.createCommand();
		_commandStack.execute(cmd);
	}
	
	/**
	 * Creates the {@link Command}, which does the work.
	 * @return The command
	 */
	protected abstract Command createCommand();
	
	/**
	 * Adds {@link Command}s to remove the given widgets from the given {@link ContainerModel}.
	 * @param compoundCmd The {@link CompoundCommand}, where the created {@link Command}s are added
	 * @param widgets A List of widgets, which should be removed
	 */
	protected final void removeWidgets(final CompoundCommand compoundCmd, final List<AbstractWidgetModel> widgets) {
		for (AbstractWidgetModel widgetModel : widgets) {
			DeleteElementCommand deleteCommand = new DeleteElementCommand(widgetModel.getParent(), widgetModel);
			compoundCmd.add(deleteCommand);
		}
	}
	
	/**
	 * Adds {@link Command}s to add the given widgets to the given {@link ContainerModel}.
	 * @param compoundCmd The {@link CompoundCommand}, where the created {@link Command}s are added
	 * @param parent The new parent of the widgets
	 * @param widgets A List of widgets, which should be added
	 */
	protected final void addWidgets(final CompoundCommand compoundCmd, final ContainerModel parent, final List<AbstractWidgetModel> widgets) {
		for (AbstractWidgetModel widgetModel : widgets) {
			AddWidgetCommand addCommand = new AddWidgetCommand(parent, widgetModel);
			compoundCmd.add(addCommand);
			//adapt position
			Point p = this.adaptWidgetPosition(widgetModel);
			SetPropertyCommand propertyCommand = new SetPropertyCommand(widgetModel,AbstractWidgetModel.PROP_POS_X, p.x);
			compoundCmd.add(propertyCommand);
			propertyCommand = new SetPropertyCommand(widgetModel,AbstractWidgetModel.PROP_POS_Y, p.y);
			compoundCmd.add(propertyCommand);
		}
	}
	
	/**
	 * Returns the new position for the {@link AbstractWidgetModel}.
	 * @param widgetModel The {@link AbstractWidgetModel}
	 * @return The new position for the {@link AbstractWidgetModel}
	 */
	protected abstract Point adaptWidgetPosition(final AbstractWidgetModel widgetModel);

}
