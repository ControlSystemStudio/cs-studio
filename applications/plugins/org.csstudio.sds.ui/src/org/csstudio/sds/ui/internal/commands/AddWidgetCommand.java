package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.eclipse.gef.commands.Command;

/**
 * An undoable command that can be used to add a widget model to a display
 * model.
 * 
 * @author swende
 * 
 */
public final class AddWidgetCommand extends Command {
	/**
	 * The display model.
	 */
	private ContainerModel _container;

	/**
	 * The display model that should be added.
	 */
	private AbstractWidgetModel _widgetModel;
	
	/**
	 * Specifies if the added widget should be selected after the insertion.
	 */
	private boolean _selectWidget = false;

	/**
	 * Constructor.
	 * 
	 * @param container
	 *            the display model
	 * @param widgetModel
	 *            the widget model that should be added to the display model
	 */
	public AddWidgetCommand(final ContainerModel container,
			final AbstractWidgetModel widgetModel) {
		this(container, widgetModel, false);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param container
	 *            the display model
	 * @param widgetModel
	 *            the widget model that should be added to the display model
	 * @param selectWidget
	 * 			  Specifies if the {@link AbstractWidgetModel} should be selected
	 */
	public AddWidgetCommand(final ContainerModel container,
			final AbstractWidgetModel widgetModel, final boolean selectWidget) {
		assert container != null;
		assert widgetModel != null;
		_container = container;
		_widgetModel = widgetModel;
		_selectWidget = selectWidget;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_widgetModel.setLayer(_container.getLayerSupport().getActiveLayer().getId());
		_container.addWidget(_widgetModel, _selectWidget);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_container.removeWidget(_widgetModel);
	}

}
