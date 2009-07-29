package org.csstudio.opibuilder.commands;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;

/**
 * A Command to set a property value of a widget. Use command can help to realize redo/undo.
 * @author Xihui Chen
 *
 */
public class SetWidgetPropertyCommand extends Command {
	
	/**
	 * The {@link AbstractWidgetModel}.
	 */
	private AbstractWidgetModel widgetModel;
	/**
	 * The name of the property.
	 */
	private String prop_id;
	/**
	 * The new value for the property.
	 */
	private Object newValue;
	/**
	 * The old value of the property.
	 */
	private Object oldValue;
	
	/**
	 * Constructor.
	 * @param widget
	 * 			The widget, whose property value should be set
	 * @param prop_id
	 * 			The id of the property
	 * @param newValue
	 * 			The new value for the property
	 */
	public SetWidgetPropertyCommand(final AbstractWidgetModel widget, final String prop_id, final Object newValue) {
		this.widgetModel = widget;
		this.prop_id = prop_id;
		this.newValue = newValue;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		oldValue = widgetModel.getPropertyValue(prop_id);
		widgetModel.setPropertyValue(prop_id, newValue);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		widgetModel.setPropertyValue(prop_id, oldValue);
	}

}
