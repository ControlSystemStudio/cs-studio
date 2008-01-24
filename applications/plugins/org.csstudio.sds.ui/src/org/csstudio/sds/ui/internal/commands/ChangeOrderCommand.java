package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.eclipse.gef.commands.Command;

/**
 * An undoable command that can be used to change the order of a widget in the model.
 * 
 * @author Kai Meyer
 * 
 */
public final class ChangeOrderCommand extends Command {
	/**
	 * The new index for the widget model.
	 */
	private int _newIndex;
	/**
	 * The old index of the widget model.
	 */
	private int _oldIndex;
	/**
	 * The widget model, which index has to be changed.
	 */
	private AbstractWidgetModel _widgetModel;
	/**
	 * The parent display model of the widget model.
	 */
	private ContainerModel _container;
	
	/**
	 * Constructor.
	 * @param containerModel
	 * 			The parent display model
	 * @param widgetModel
	 * 			The widget model
	 * @param index
	 * 			The new index
	 */
	public ChangeOrderCommand(final ContainerModel containerModel, final AbstractWidgetModel widgetModel, final int index) {
		assert containerModel != null;
		assert widgetModel != null;
		_newIndex = index;
		_widgetModel = widgetModel;
		_container = containerModel;
		_oldIndex = _container.getIndexOf(_widgetModel);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_container.changeOrder(_widgetModel, _newIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_container.changeOrder(_widgetModel, _oldIndex);
	}
	
}
