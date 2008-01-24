package org.csstudio.sds.ui.internal.commands;

import org.eclipse.gef.commands.Command;
import org.csstudio.sds.model.AbstractWidgetModel;

/**
 * A Command to set a property value of a widget.
 * @author Kai Meyer
 *
 */
public class SetPropertyCommand extends Command {
	
	/**
	 * The {@link AbstractWidgetModel}.
	 */
	private AbstractWidgetModel _widget;
	/**
	 * The name of the property.
	 */
	private String _propertyName;
	/**
	 * The new value for the property.
	 */
	private Object _newValue;
	/**
	 * The old value of the property.
	 */
	private Object _oldValue;
	
	/**
	 * Constructor.
	 * @param widget
	 * 			The widget, which property value should be set
	 * @param propertyName
	 * 			The name of the property
	 * @param newValue
	 * 			The new value for the property
	 */
	public SetPropertyCommand(final AbstractWidgetModel widget, final String propertyName, final Object newValue) {
		_widget = widget;
		_propertyName = propertyName;
		_newValue = newValue;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_oldValue = _widget.getProperty(_propertyName).getPropertyValue();
		_widget.setPropertyValue(_propertyName, _newValue);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_widget.setPropertyValue(_propertyName, _oldValue);
	}

}
