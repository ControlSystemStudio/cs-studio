package org.csstudio.sds.ui.internal.properties.view;

import java.text.MessageFormat;

import org.csstudio.sds.model.DynamicsDescriptor;
import org.eclipse.gef.commands.Command;

/**
 * Command, which sets the dynamics descriptor of a property.
 * 
 * @author Sven Wende
 * 
 */
final class SetDynamicsDescriptorCommand extends Command {

	/**
	 * The new property value.
	 */
	private DynamicsDescriptor _dynamicsDescriptor;

	/**
	 * The id of the property.
	 */
	private Object _propertyId;

	/**
	 * The old property value.
	 */
	private DynamicsDescriptor _undoValue;

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
	 * @param dynamicsDescriptor
	 *            the new dynamics descriptor
	 * @param propertySource
	 *            the property source
	 */
	public SetDynamicsDescriptorCommand(final String propLabel,
			final Object propId, final DynamicsDescriptor dynamicsDescriptor,
			final IPropertySource propertySource) {
		super(MessageFormat.format("Set {0} Property",
				new Object[] { propLabel }).trim());
		_propertyId = propId;
		_dynamicsDescriptor = dynamicsDescriptor;
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
		_undoValue = _propertySource.getDynamicsDescriptor(_propertyId);

		_propertySource.setDynamicsDescriptor(_propertyId, _dynamicsDescriptor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_propertySource.setDynamicsDescriptor(_propertyId, _undoValue);
	}

}
