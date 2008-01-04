package org.csstudio.platform.internal.simpledal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;

/**
 * DAL Connectors are connected to the control system via the DAL API.
 * 
 * All events received from DAL are forwarded to
 * {@link IProcessVariableValueListener}큦 which abstract from DAL.
 * 
 * For convinience the {@link IProcessVariableValueListener}큦 are only weakly
 * referenced. The connector tracks for {@link IProcessVariableValueListener}큦
 * that have been garbage collected and removes those references from its
 * internal list. This way {@link IProcessVariableValueListener}큦 must not be
 * disposed explicitly.
 * 
 * @author Sven Wende
 * 
 */
@SuppressWarnings("unchecked")
class DalConnector extends AbstractConnector implements DynamicValueListener,
		LinkListener, ResponseListener {
	/**
	 * The DAL property, this connector is connected to.
	 */
	private DynamicValueProperty _dalProperty;

	/**
	 * Constructor.
	 */
	public DalConnector(IProcessVariableAddress pvAddress, ValueType valueType) {
		super(pvAddress, valueType);
	}

	/**
	 * Sets the DAL property, this connector is connected to.
	 * 
	 * @param dalProperty
	 *            the DAL property
	 */
	public void setDalProperty(DynamicValueProperty dalProperty) {
		_dalProperty = dalProperty;
	}

	/**
	 * Returns the DAL property, this connector is connected to.
	 * 
	 * @return
	 */
	public DynamicValueProperty getDalProperty() {
		return _dalProperty;
	}

	/**
	 * {@inheritDoc}
	 */
	public void conditionChange(DynamicValueEvent event) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void errorResponse(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void timelagStarts(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void timelagStops(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void timeoutStarts(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void timeoutStops(DynamicValueEvent event) {

	}

	/**
	 * {@inheritDoc}
	 */
	public void valueChanged(final DynamicValueEvent event) {
		doForwardValue(event.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public void valueUpdated(final DynamicValueEvent event) {
		doForwardValue(event.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public void connected(final ConnectionEvent e) {
		doForwardConnectionStateChange(ConnectionState.translate(e.getState()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void connectionFailed(ConnectionEvent e) {
		doForwardConnectionStateChange(ConnectionState.translate(e.getState()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void connectionLost(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroyed(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void disconnected(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void resumed(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void suspended(ConnectionEvent e) {
		forwardConnectionEvent(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseError(ResponseEvent event) {
		Exception e = event.getResponse().getError();
		doForwardError(e != null ? e.getMessage() : "Unkown error!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseReceived(ResponseEvent event) {
		IProcessVariableAddress pv = getProcessVariableAddress();
		String idTag = event.getResponse().getIdTag().toString();
		
		// Important: We need to check, that we forward only the right events because all Characteristics  are queried using the same DAL Property instance
		boolean forward = false;

		if (pv.isCharacteristic()) {
			forward = pv.getCharacteristic().equals(idTag);
		} else {
			forward = "value".equals(idTag);
		}

		if (forward) {
			CentralLogger.getInstance().info(
					null,
					"Value received for -> " + getProcessVariableAddress()
							+ " -> " + event.getResponse().getValue());
			doForwardValue(event.getResponse().getValue());
		}
	}

	private void forwardConnectionEvent(ConnectionEvent e) {
		doForwardConnectionStateChange(ConnectionState.translate(e.getState()));
	}
}
