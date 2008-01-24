package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.gef.commands.Command;

/**
 * Command, which sets the value of a property.
 * 
 * @author Sven Wende
 * 
 */
final class SetValueCommand extends Command {

	/**
	 * The new property value.
	 */
	private Object _propertyValue;

	/**
	 * The id of the property.
	 */
	private Object _propertyId;

	/**
	 * The old property value.
	 */
	private Object _undoValue;

	/**
	 * A flag indicating, whether reset is necessary on undo.
	 */
	private boolean _resetOnUndo;

	/**
	 * The property source.
	 */
	private IPropertySource _propertySource;

	/**
	 * Constructor.
	 * 
	 * @param propLabel
	 *            a label for the property, that is beeing set
	 * @param propId
	 *            the id of the property
	 * @param propValue
	 *            the new property value
	 * @param propertySource
	 *            the property source
	 */
	public SetValueCommand(final String propLabel, final Object propId,
			final Object propValue, final IPropertySource propertySource) {
		_propertyId = propId;
		_propertyValue = propValue;
		_propertySource = propertySource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		/*
		 * Fix for Bug# 54250 IPropertySource.isPropertySet(String) returns
		 * false both when there is no default value, and when there is a
		 * default value and the property is set to that value. To correctly
		 * determine if a reset should be done during undo, we compare the
		 * return value of isPropertySet(String) before and after
		 * setPropertyValue(...) is invoked. If they are different (it must have
		 * been false before and true after -- it cannot be the other way
		 * around), then that means we need to reset.
		 */
		boolean wasPropertySet = _propertySource.isPropertySet(_propertyId);
		_undoValue = _propertySource.getPropertyValue(_propertyId);
		if (_undoValue instanceof IPropertySource) {
			_undoValue = ((IPropertySource) _undoValue).getEditableValue();
		}
		if (_propertyValue instanceof IPropertySource) {
			_propertyValue = ((IPropertySource) _propertyValue)
					.getEditableValue();
		}

		_propertySource.setPropertyValue(_propertyId, _propertyValue);

		if (_propertySource instanceof IPropertySource2) {
			_resetOnUndo = !wasPropertySet
					&& ((IPropertySource2) _propertySource)
							.isPropertyResettable(_propertyId);
		} else {
			_resetOnUndo = !wasPropertySet
					&& _propertySource.isPropertySet(_propertyId);
		}

		if (_resetOnUndo) {
			_undoValue = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		execute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		if (_resetOnUndo) {
			_propertySource.resetPropertyValue(_propertyId);
		} else {
			_propertySource.setPropertyValue(_propertyId, _undoValue);
		}
	}

}
