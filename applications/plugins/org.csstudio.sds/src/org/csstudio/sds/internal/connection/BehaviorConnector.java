package org.csstudio.sds.internal.connection;

import org.csstudio.dal.DalPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;

/**
 * Connector that forwards channel events to a behavior strategy class.
 * 
 * @author Sven Wende
 * 
 */
public final class BehaviorConnector implements ChannelListener, org.csstudio.sds.model.IPropertyChangeListener {
	private AbstractWidgetModel widget;
	private ConnectionParameters connectionParameters;
	private AbstractBehavior<AbstractWidgetModel> behavior;

	/**
	 * Constructs a connector.
	 * 
	 * @param connectionParameters
	 *            the connection parameters (needed for the DAL connection)
	 * @param behavior
	 *            the behavior (consumer for DAL events)
	 */
	public BehaviorConnector(AbstractWidgetModel widget, ConnectionParameters connectionParameters, AbstractBehavior<AbstractWidgetModel> behavior) {
		assert widget != null;
		assert connectionParameters != null;
		assert behavior != null;
		this.widget = widget;
		this.connectionParameters = connectionParameters;
		this.behavior = behavior;
	}

	/**
	 *{@inheritDoc}
	 */
	public void channelDataUpdate(AnyDataChannel channel) {
		behavior.processChannelDataUpdate(widget, channel);
	}

	/**
	 *{@inheritDoc}
	 */
	public void channelStateUpdate(AnyDataChannel channel) {
		behavior.processChannelStateUpdate(widget, channel);
	}

	public ConnectionParameters getConnectionParameters() {
		return connectionParameters;
	}

	/**
	 *{@inheritDoc}
	 */
	public void dynamicsDescriptorChanged(DynamicsDescriptor dynamicsDescriptor) {
		// ignore
	}

	/**
	 *{@inheritDoc}
	 */
	public void propertyManualValueChanged(String propertyId, Object manualValue) {
		Object value2set = behavior.convertOutgoingValue(widget, propertyId, manualValue);

		if (value2set != null) {
			try {
				CentralLogger.getInstance().info(null, "About to apply: "+value2set + " to " + connectionParameters.toString());
				DalPlugin.getDefault().getSimpleDALBroker().setValueAsync(this.connectionParameters, value2set, null);
			} catch (Exception e) {
				CentralLogger.getInstance().error(this, e.getMessage());
			}
		}
	}

	/**
	 *{@inheritDoc}
	 */
	public void propertyValueChanged(Object oldValue, Object newValue) {
		// ignore
	}
	
}