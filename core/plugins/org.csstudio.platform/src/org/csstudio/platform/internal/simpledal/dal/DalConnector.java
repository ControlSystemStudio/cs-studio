/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.platform.internal.simpledal.dal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.csstudio.platform.internal.simpledal.AbstractConnector;
import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.spi.PropertyFactory;

/**
 * DAL Connectors are connected to the control system via the DAL API.
 * 
 * All events received from DAL are forwarded to
 * {@link IProcessVariableValueListener}s which abstract from DAL.
 * 
 * For convenience the {@link IProcessVariableValueListener}s are only weakly
 * referenced. The connector tracks for {@link IProcessVariableValueListener}s
 * that have been garbage collected and removes those references from its
 * internal list. This way {@link IProcessVariableValueListener}s don�t have to
 * be disposed explicitly.
 * 
 * @author Sven Wende
 * 
 */
@SuppressWarnings("unchecked")
public final class DalConnector extends AbstractConnector implements DynamicValueListener, LinkListener, ResponseListener,
		PropertyChangeListener {

	public static final CharacteristicInfo C_TIMESTAMP_INFO = new CharacteristicInfo("timestamp", Timestamp.class,
			new Class[] { DynamicValueProperty.class }, "Meta timestamp characteristic.", null, true);
	public static final CharacteristicInfo C_SEVERITY_INFO = new CharacteristicInfo("severity", String.class,
			new Class[] { DynamicValueProperty.class }, "Meta timestamp characteristic.", null, true);
	public static final CharacteristicInfo C_STATUS_INFO = new CharacteristicInfo("status", String.class,
			new Class[] { DynamicValueProperty.class }, "Meta timestamp characteristic.", null, true);

	{
		CharacteristicInfo.registerCharacteristicInfo(C_SEVERITY_INFO);
		CharacteristicInfo.registerCharacteristicInfo(C_TIMESTAMP_INFO);
		CharacteristicInfo.registerCharacteristicInfo(C_STATUS_INFO);
	}

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
	 * {@inheritDoc}
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// a property change event indicates a change in a characteristic value
		Object value = evt.getNewValue();
		String characteristicId = evt.getPropertyName();
		doForwardCharacteristic(value, new Timestamp(), characteristicId);

		printDebugInfo(characteristicId + " " + (value instanceof String[] ? Arrays.toString((String[]) value) : value.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void conditionChange(DynamicValueEvent event) {
		// translate a condition change to certain characteristics listeners
		// might be registered for
		DynamicValueCondition condition = event.getCondition();

		// ... characteristic "timestamp"
		doForwardCharacteristic(condition.getTimestamp(), event.getTimestamp(), C_TIMESTAMP_INFO.getName());

		// ... characteristic "status"
		doForwardCharacteristic(EpicsUtil.extratStatus(condition), event.getTimestamp(), C_STATUS_INFO.getName());

		// ... characteristic "severity"
		doForwardCharacteristic(EpicsUtil.toEPICSFlavorSeverity(condition), event.getTimestamp(), C_SEVERITY_INFO.getName());

		// ... request initial values, when the condition changes to "normal"
		if (event.getCondition().isNormal()) {
			requestAndForwardInitialValues();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void errorResponse(DynamicValueEvent event) {
		// FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void timelagStarts(DynamicValueEvent event) {
		// FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void timelagStops(DynamicValueEvent event) {
		// FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void timeoutStarts(DynamicValueEvent event) {
		// FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void timeoutStops(DynamicValueEvent event) {
		// FIXME: forward condition changes
	}

	/**
	 * {@inheritDoc}
	 */
	public void valueChanged(final DynamicValueEvent event) {
		doHandleValueUpdate(event);
	}

	/**
	 * {@inheritDoc}
	 */
	public void valueUpdated(final DynamicValueEvent event) {
		doHandleValueUpdate(event);
	}

	/**
	 * {@inheritDoc}
	 */
	public void connected(final ConnectionEvent e) {
		// ... forward the new connection state
		doForwardConnectionStateChange(ConnectionState.translate(e.getState()));

		// ... forward initial values
		requestAndForwardInitialValues();
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
		doForwardError(e != null ? e.getMessage() : "Unknown error!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseReceived(ResponseEvent event) {
		// Igor: if necessary update last value. We expect one event only
		// originating
		// from initial asynchronous get

		doForwardValue(event.getResponse().getValue(), event.getResponse().getTimestamp());

	}

	private void forwardConnectionEvent(ConnectionEvent e) {
		doForwardConnectionStateChange(ConnectionState.translate(e.getState()));
	}

	/**
	 * Waits until DAL property is connected or timeout has elapsed
	 * 
	 * @param timeout
	 *            the timeout to wait
	 * @return <code>true</code> if property was connected
	 */
	public boolean waitTillConnected(long timeout) {
		return EpicsUtil.waitTillConnected(_dalProperty, timeout);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doGetValueAsynchronously(final IProcessVariableValueListener listener) {

		waitTillConnected(3000);

		try {
			block();

			if (_dalProperty != null) {
				ResponseListener responseListener = new ResponseListener() {
					public void responseError(ResponseEvent event) {
						// forward the error
						Exception error = event.getResponse().getError();
						String errorMsg = error != null ? error.getMessage() : "Unknown Error!";
						listener.errorOccured(errorMsg);
					}

					public void responseReceived(ResponseEvent event) {
						Object value = event.getResponse().getValue();
						Timestamp timestamp = event.getResponse().getTimestamp();
						listener.valueChanged(ConverterUtil.convert(value, getValueType()), timestamp);

						printDebugInfo("AGET-RETURN: " + getValueType() + " " + value);
					}

				};

				_dalProperty.getAsynchronous(responseListener);
			} else {
				listener.errorOccured("Internal error. No connection available.");
			}
		} catch (Exception e) {
			listener.errorOccured(e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValueSynchronously() throws Exception {

		waitTillConnected(3000);

		Object result = null;
		// ... try to read the value
		if (_dalProperty != null) {
			result = _dalProperty.getValue();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetValueAsynchronously(Object value) throws Exception {
		waitTillConnected(3000);

		if (_dalProperty != null && _dalProperty.isSettable()) {
			_dalProperty.setAsynchronous(ConverterUtil.convert(value, getValueType()), new ResponseListener() {

				public void responseReceived(ResponseEvent event) {
					CentralLogger.getInstance().debug(null, event.getResponse().toString());
				}

				public void responseError(ResponseEvent event) {
					CentralLogger.getInstance().error(null, event.getResponse().getError());
				}
			});
		} else {
			throw new Exception("Property " + _dalProperty.getUniqueName() + " is not settable");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doSetValueSynchronously(Object value) {
		boolean success = false;

		waitTillConnected(3000);

		if (_dalProperty != null && _dalProperty.isSettable()) {
			try {
				_dalProperty.setValue(ConverterUtil.convert(value, getValueType()));
				success = true;
			} catch (DataExchangeException e) {
				CentralLogger.getInstance().error(null, e);
			}
		} else {
			CentralLogger.getInstance().debug(null, "Property " + _dalProperty.getUniqueName() + " is not settable");
		}
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void init() {
		// get or create a real DAL property
		DynamicValueProperty property = null;
		try {
			RemoteInfo ri = getProcessVariableAddress().toDalRemoteInfo();

			PropertyFactory factory = DALPropertyFactoriesProvider.getInstance().getPropertyFactory(
					getProcessVariableAddress().getControlSystem());

			if (getValueType() == ValueType.OBJECT) {
				property = factory.getProperty(ri);
			} else {
				property = factory.getProperty(ri, getValueType().getDalType(), null);
			}

			if (property != null) {
				setDalProperty(property);
			}
		} catch (Throwable e) {
			forwardError(e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDispose() {
		printDebugInfo("DISPOSE");

		DynamicValueProperty property = _dalProperty;

		setDalProperty(null);

		if (property != null && !property.isDestroyed()) {
			// remove link listener
			property.removeLinkListener(this);

			// remove value listeners
			property.removeDynamicValueListener(this);

			// remove response listeners
			property.removeResponseListener(this);

			// try to dispose the DAL property
			PropertyFactory factory = DALPropertyFactoriesProvider.getInstance().getPropertyFactory(
					getProcessVariableAddress().getControlSystem());

			// if the property is not used anymore by other connectors,
			// destroy it
			if (property.getDynamicValueListeners().length <= 1 && property.getResponseListeners().length <= 0) {

				printDebugInfo("DESTROY");

				factory.getPropertyFamily().destroy(property);

				// <**** Workarround (FIXME: Remove, when DAL is fixed) ***
				// DAL caches a reference to a former ResponseListener
				// via its latestResponse and latestRequest fields on
				// DynamicValuePropertyImpl.class
				// ********************************************************
				/*
				 * try { Object e = property.getLatestResponse();
				 * 
				 * property.getAsynchronous(null);
				 * 
				 * while (e == property.getLatestResponse()) { Thread.sleep(1); } }
				 * catch (DataExchangeException e) { e.printStackTrace(); }
				 * catch (InterruptedException e) { e.printStackTrace(); }
				 */
				// **** Workarround (Remove, when DAL is fixed)************>
				assert !factory.getPropertyFamily().contains(property) : "!getPropertyFactory().getPropertyFamily().contains(property)";
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SettableState doIsSettable() {
		SettableState result = SettableState.UNKNOWN;

		try {
			waitTillConnected(2000);

			// DAL encapsulates the detection of the current user internally
			// (probably via global system properties)
			if (_dalProperty != null && _dalProperty.isConnected()) {
				result = _dalProperty.isSettable() ? SettableState.SETTABLE : SettableState.NOT_SETTABLE;
			}
		} catch (Exception e) {
			CentralLogger.getInstance().error(this, "We could not check the settable-state of [" + getProcessVariableAddress().toString() + "]",
					e);
			result = SettableState.UNKNOWN;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGetCharacteristicAsynchronously(final String characteristicId, final ValueType valueType,
			final IProcessVariableValueListener listener) {

		waitTillConnected(3000);

		try {
			if (_dalProperty != null && _dalProperty.isConnected()) {
				ResponseListener responseListener = new ResponseListener() {
					public void responseError(ResponseEvent event) {
						// forward the error
						Exception error = event.getResponse().getError();
						String errorMsg = error != null ? error.getMessage() : "Unknown Error!";
						listener.errorOccured(errorMsg);
						
						printDebugInfo("AGET-ERROR: " + error);
					}

					public void responseReceived(ResponseEvent event) {
						Object value = event.getResponse().getValue();
						Timestamp timestamp = event.getResponse().getTimestamp();
						listener.valueChanged(ConverterUtil.convert(value, valueType), timestamp);

						printDebugInfo("AGET-RETURN: " + valueType + " " + value);
					}

				};

				_dalProperty.getCharacteristicAsynchronously(characteristicId, responseListener);
			} else {
				listener.errorOccured("Internal error. No connection available.");
			}
		} catch (Exception e) {
			listener.errorOccured(e.getLocalizedMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetCharacteristicSynchronously(String characteristicId, ValueType valueType) throws Exception {

		Object result = null;
		// ... try to read the value
		if (_dalProperty != null) {
			waitTillConnected(3000);

			result = EpicsUtil.getCharacteristic(characteristicId, _dalProperty, getValueType());
		}

		return result;
	}

	/**
	 * Returns the DAL property that is internally used.
	 * 
	 * @return the internally used DAL property
	 */
	protected DynamicValueProperty getDalProperty() {
		return _dalProperty;
	}

	/**
	 * Sets the DAL property, this connector is connected to.
	 * 
	 * @param dalProperty
	 *            the DAL property
	 */
	private void setDalProperty(DynamicValueProperty dalProperty) {
		if (_dalProperty != null) {
			_dalProperty.removeDynamicValueListener(this);
			_dalProperty.removePropertyChangeListener(this);
			_dalProperty.removeLinkListener(this);
		}

		_dalProperty = dalProperty;

		if (_dalProperty != null) {
			_dalProperty.addDynamicValueListener(this);

			_dalProperty.addPropertyChangeListener(this);

			// we add a LinkListener to get informed of connection state changes
			_dalProperty.addLinkListener(this);

			// send initial connection state
			forwardConnectionState(ConnectionState.translate(_dalProperty.getConnectionState()));
		}
	}

	/**
	 * A change of the "normal" value has been reported and needs to be
	 * forwarded.
	 * 
	 * @param event
	 *            the event that reports the value update
	 */
	private void doHandleValueUpdate(DynamicValueEvent event) {
		// ... forward the value
		doForwardValue(event.getValue(), event.getTimestamp());

		// ... forward an additional "timestamp" characteristic
		doForwardCharacteristic(event.getTimestamp(), event.getTimestamp(), C_TIMESTAMP_INFO.getName());
	}

}
