package org.csstudio.sds.model.initializers;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.ParameterDescriptor;

/**
 * Base class for widget model initializers that provides a convinient API for
 * initializing widget model for a certain control system.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public abstract class AbstractWidgetModelInitializer {
	/**
	 * The current model that is beeing initialized. This reference will be
	 * injected at runtime.
	 */
	private AbstractWidgetModel _widgetModel;

	/**
	 * Setter, which injects the widget model at runtime. Should only be called
	 * within this package.
	 * 
	 * @param widgetModel
	 *            the widget model
	 */
	final void setWidgetModel(final AbstractWidgetModel widgetModel) {
		_widgetModel = widgetModel;
	}

	/**
	 * Subclasses should implement the proper widget initialization in this
	 * method. Some Control System wide settings might have been stored in a
	 * global schema, which is shared by all initializers for that schema. A
	 * reference to this schema is assigned.
	 * 
	 * Subclasses can use the following methods for initialization of a widget:
	 * 
	 * {@link #initializeDynamicProperty(String, String)}
	 * {@link #initializeDynamicProperty(String, String[])}
	 * {@link #initializeDynamicProperty(String, String, String)}
	 * {@link #initializeAlias(String, String)}
	 * {@link #initializeStaticProperty(String, Object)}
	 * 
	 * A typical implementation might look like this:
	 * 
	 * <code>
	 * 	protected void initialize(final AbstractControlSystemSchema schema) {
	 * 		initializeStaticProperty(RectangleModel.PROP_FILL, 50.0);
	 * 		initializeDynamicProperty(RectangleModel.PROP_FILL, "$channel$.VAL");
	 *  }
	 * </code>
	 * 
	 * @param schema
	 *            the control system schema
	 */
	protected abstract void initialize(final AbstractControlSystemSchema schema);

	/**
	 * Initializes a alias, which has widget scope.
	 * 
	 * @param alias
	 *            the alias name, e.g. "channel"
	 * @param description
	 *            a alias description
	 * 
	 */
	public final void initializeAlias(final String alias,
			final String description) {
		_widgetModel.addAlias(alias, "");
	}

	/**
	 * Initializes a property with a static value.
	 * 
	 * @param propertyId
	 *            the property id
	 * @param value
	 *            the value
	 */
	public final void initializeStaticProperty(final String propertyId,
			final Object value) {
		_widgetModel.setPropertyValue(propertyId, value);
	}

	/**
	 * Initializes a property with a single input and a single output channel.
	 * 
	 * @param propertyId
	 *            the property id
	 * @param channelName
	 *            the input channel name
	 * @param outputChannelName
	 *            the output channel name
	 */
	public final void initializeDynamicProperty(final String propertyId,
			final String channelName, final String outputChannelName) {
		final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
		dynamicsDescriptor.addInputChannel(new ParameterDescriptor(channelName,
				Object.class));
		if (outputChannelName != null) {
			dynamicsDescriptor.setOutputChannel(new ParameterDescriptor(
					outputChannelName, Object.class));
		}
		_widgetModel.setDynamicsDescriptor(propertyId, dynamicsDescriptor);
	}

	/**
	 * Initializes a property with a single input channel.
	 * 
	 * @param propertyId
	 *            the property id
	 * @param channelName
	 *            the input channel name
	 */
	public final void initializeDynamicProperty(final String propertyId,
			final String channelName) {
		initializeDynamicProperty(propertyId, channelName, null);
	}

	/**
	 * Initializes a property with a several input channels.
	 * 
	 * @param propertyId
	 *            the property id
	 * @param channelNames
	 *            the input channel names
	 */
	public final void initializeDynamicProperty(final String propertyId,
			final String[] channelNames) {
		initializeDynamicProperty(propertyId, channelNames, null);
	}
	/**
	 * Initializes a property with a several input channels and a single output channel.
	 * 
	 * @param propertyId
	 *            the property id
	 * @param channelNames
	 *            the input channel names
	 * @param outputChannelName
	 *            the output channel name
	 */
	public final void initializeDynamicProperty(final String propertyId,
			final String[] channelNames, final String outputChannelName) {
		final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();

		for (String channelName : channelNames) {
			dynamicsDescriptor.addInputChannel(new ParameterDescriptor(
					channelName, Object.class));
		}

		if (outputChannelName != null) {
			dynamicsDescriptor.setOutputChannel(new ParameterDescriptor(
					outputChannelName, Object.class));
		}

		_widgetModel.setDynamicsDescriptor(propertyId, dynamicsDescriptor);
	}

}
