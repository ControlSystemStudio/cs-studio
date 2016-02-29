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

import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.ResponseEvent;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.ConnectionEvent;
import org.csstudio.dal.context.LinkListener;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.spi.PropertyFactory;
import org.csstudio.platform.internal.simpledal.AbstractConnector;
import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.SettableState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAL Connectors are connected to the control system via the DAL API.
 *
 * All events received from DAL are forwarded to
 * {@link IProcessVariableValueListener}s which abstract from DAL.
 *
 * For convenience the {@link IProcessVariableValueListener}s are only weakly
 * referenced. The connector tracks for {@link IProcessVariableValueListener}s
 * that have been garbage collected and removes those references from its
 * internal list. This way {@link IProcessVariableValueListener}s don�t have
 * to be disposed explicitly.
 *
 * @author Sven Wende
 *
 */
@SuppressWarnings("unchecked")
public final class DalConnector extends AbstractConnector implements DynamicValueListener, LinkListener, ResponseListener,
        PropertyChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(DalConnector.class);

    private static final int CONNECTION_TIMEOUT = 3000;
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
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // a property change event indicates a change in a characteristic value
        Object value = evt.getNewValue();
        String characteristicId = evt.getPropertyName();
        doForwardCharacteristic(value, new Timestamp(), characteristicId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void conditionChange(DynamicValueEvent event) {
        // translate a condition change to certain characteristics listeners
        // might be registered for
        processConditionChange(event.getCondition(), event.getTimestamp());
    }

    @Override
    protected void sendInitialValuesForNewListener(String characteristicId, IProcessVariableValueListener listener) {
        super.sendInitialValuesForNewListener(characteristicId, listener);

        if (_dalProperty != null) {
            processConditionChange(_dalProperty.getCondition(), _dalProperty.getLatestValueUpdateTimestamp());
        }
    }

    private void processConditionChange(DynamicValueCondition condition, Timestamp timestamp) {
        if (condition != null) {
            // ... characteristic "timestamp"
            doForwardCharacteristic(condition.getTimestamp(), timestamp, CharacteristicInfo.C_TIMESTAMP.getName());

            // ... characteristic "status"
            doForwardCharacteristic(EpicsUtil.extratStatus(condition), timestamp, CharacteristicInfo.C_STATUS.getName());

            // ... characteristic "severity"
            doForwardCharacteristic(EpicsUtil.toEPICSFlavorSeverity(condition), timestamp, CharacteristicInfo.C_SEVERITY.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void errorResponse(DynamicValueEvent event) {
        // FIXME: forward condition changes
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void timelagStarts(DynamicValueEvent event) {
        // FIXME: forward condition changes
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void timelagStops(DynamicValueEvent event) {
        // FIXME: forward condition changes
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void timeoutStarts(DynamicValueEvent event) {
        // FIXME: forward condition changes
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void timeoutStops(DynamicValueEvent event) {
        // FIXME: forward condition changes
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChanged(final DynamicValueEvent event) {
        doHandleValueUpdate(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueUpdated(final DynamicValueEvent event) {
        doHandleValueUpdate(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(final ConnectionEvent e) {
        // ... forward the new connection state
        doForwardConnectionStateChange(ConnectionState.translate(e.getState()));

        // ... forward initial values
        updateCharacteristicListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void operational(final ConnectionEvent e) {
        // ... forward the new connection state
        doForwardConnectionStateChange(ConnectionState.translate(e.getState()));

        // ... forward initial values
        updateCharacteristicListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionFailed(ConnectionEvent e) {
        doForwardConnectionStateChange(ConnectionState.translate(e.getState()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionLost(ConnectionEvent e) {
        forwardConnectionEvent(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyed(ConnectionEvent e) {
        forwardConnectionEvent(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected(ConnectionEvent e) {
        forwardConnectionEvent(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resumed(ConnectionEvent e) {
        forwardConnectionEvent(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void suspended(ConnectionEvent e) {
        forwardConnectionEvent(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void responseError(ResponseEvent event) {
        Exception e = event.getResponse().getError();
        doForwardError(e != null ? e.getMessage() : "Unknown error!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     *
     * @return <code>true</code> if property was connected
     */
    public boolean waitTillConnected(long timeout) {
        return EpicsUtil.waitTillConnected(_dalProperty, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGetValueAsynchronously(final IProcessVariableValueListener listener) {

        if (waitTillConnected(CONNECTION_TIMEOUT)) {
            block();

            ResponseListener responseListener = new ResponseListener() {
                public void responseError(ResponseEvent event) {
                    // forward the error
                    Exception error = event.getResponse().getError();
                    String errorMsg = error != null ? error.getMessage() : "Unknown Error!";
                    listener.errorOccured(errorMsg);

                    printDebugInfo("AGET-ERROR : " + error + "  (" + event.getResponse().toString() + ")");
                }

                public void responseReceived(ResponseEvent event) {
                    Object value = event.getResponse().getValue();
                    Timestamp timestamp = event.getResponse().getTimestamp();
                    listener.valueChanged(ConverterUtil.convert(value, getValueType()), timestamp);

                    printDebugInfo("AGET-RETURN: " + getValueType() + " " + value);
                }

            };

            printDebugInfo("GET ASYNC");

            try {
                _dalProperty.getAsynchronous(responseListener);
            } catch (Exception e) {
                listener.errorOccured(e.getLocalizedMessage());
            }
        } else {
            listener.errorOccured("Internal error. No connection available.");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValueSynchronously() throws Exception {

        Object result = null;
        // ... try to read the value
        if (waitTillConnected(CONNECTION_TIMEOUT)) {
            printDebugInfo("GET SYNC");
            result = _dalProperty.getValue();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValueAsynchronously(Object value, final IProcessVariableWriteListener listener) throws Exception {
        if (waitTillConnected(3000)) {
            if (_dalProperty.isSettable()) {
                Object convertedValue;
                try {
                    convertedValue = ConverterUtil.convert(value, getValueType());

                    _dalProperty.setAsynchronous(convertedValue, new ResponseListener() {

                        public void responseReceived(ResponseEvent event) {
                            if (listener != null) {
                                listener.success();
                            }
                            LOG.debug(event.getResponse().toString());
                        }

                        public void responseError(ResponseEvent event) {
                            if (listener != null) {
                                listener.error(event.getResponse().getError());
                            }
                            LOG.error(event.getResponse().getError().toString());
                        }
                    });

                } catch (NumberFormatException nfe) {
                    // Do nothing! Is a invalid value format!
                    LOG.warn("Invalid value format. (" + value + ") is not set to " + getName());
                    return;
                }
            } else {
                throw new Exception("Property " + _dalProperty.getUniqueName() + " is not settable");
            }
        } else {
            throw new Exception("Property not available");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doSetValueSynchronously(Object value) {
        boolean success = false;

        if (waitTillConnected(CONNECTION_TIMEOUT)) {
            if (_dalProperty.isSettable()) {
                try {
                    _dalProperty.setValue(ConverterUtil.convert(value, getValueType()));
                    success = true;
                } catch (NumberFormatException nfe) {
                    LOG.warn("Invalid value format. (" + value + ") is not set to" + getName());
                } catch (DataExchangeException e) {
                    LOG.error(e.toString());
                }
            } else {
                printDebugInfo("Property not settable");
            }
        } else {
            printDebugInfo("Property not available");
        }
        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInit() {
        // get or create a real DAL property
        DynamicValueProperty property = null;

        try {
            RemoteInfo ri = getProcessVariableAddress().toDalRemoteInfo();

            PropertyFactory factory = DALPropertyFactoriesProvider.getInstance().getPropertyFactory(
                    getProcessVariableAddress().getControlSystem());

            switch (getValueType()) {
            case OBJECT:
                property = factory.getProperty(ri);
                break;
            case STRING:
                /*
                 * swende: 2010-03-06: this is a dirty quickfix which is related
                 * to problems with SDS displays that specifiy
                 * "pv[severity], String" as pv address / please remove if it
                 * does not work as expected or when all current SDS files at
                 * DESY have been propertly changed
                 */
                String characteristic = getProcessVariableAddress().getCharacteristic();
                //If connection is made as pv[severity] or just pv, than ignore everything
                //and go to default. In all other cases (e.g. pv[graphMin}, string), create
                //a default property.
                if (characteristic != null && !CharacteristicInfo.C_SEVERITY.getName().equals(characteristic)) {
                    property = factory.getProperty(ri);
                    break;
                }
            default:
                property = factory.getProperty(ri, getValueType().getDalType(), null);
                break;
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
                 * while (e == property.getLatestResponse()) { Thread.sleep(1);
                 * } } catch (DataExchangeException e) { e.printStackTrace(); }
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
            // DAL encapsulates the detection of the current user internally
            // (probably via global system properties)
            if (waitTillConnected(CONNECTION_TIMEOUT)) {
                result = _dalProperty.isSettable() ? SettableState.SETTABLE : SettableState.NOT_SETTABLE;
            }
        } catch (Exception e) {
            LOG.error("We could not check the settable-state of [" + getProcessVariableAddress().toString() + "]",
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

        try {
            if (waitTillConnected(CONNECTION_TIMEOUT)) {
                ResponseListener responseListener = new ResponseListener() {
                    public void responseError(ResponseEvent event) {
                        // forward the error
                        Exception error = event.getResponse().getError();
                        String errorMsg = error != null ? error.getMessage() : "Unknown Error!";
                        listener.errorOccured(errorMsg);

                        printDebugInfo("AGET-ERROR [" + characteristicId + "] : " + error + "  (" + event.getResponse().toString() + ")");
                    }

                    public void responseReceived(ResponseEvent event) {
                        Object value = event.getResponse().getValue();
                        Timestamp timestamp = event.getResponse().getTimestamp();
                        listener.valueChanged(value, timestamp);
                        // listener.valueChanged(ConverterUtil.convert(value,
                        // valueType), timestamp);

                        printDebugInfo("AGET-RETURN: " + valueType + " " + value);

                    }

                };

                printDebugInfo("GET ASYNC [" + characteristicId + "]");

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
        if (waitTillConnected(CONNECTION_TIMEOUT)) {
            if (characteristicId.equals(CharacteristicInfo.C_SEVERITY.getName())) {
                result = EpicsUtil.toEPICSFlavorSeverity(_dalProperty.getCondition());
            } else if (characteristicId.equals(CharacteristicInfo.C_STATUS.getName())) {
                result = EpicsUtil.extratStatus(_dalProperty.getCondition());
            } else if (characteristicId.equals(CharacteristicInfo.C_TIMESTAMP.getName())) {
                result = _dalProperty.getCondition().getTimestamp();
            } else {
                Object tmp = _dalProperty.getCharacteristic(characteristicId);
                result = valueType != null ? ConverterUtil.convert(tmp, valueType) : tmp;
            }
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
        doForwardCharacteristic(event.getTimestamp(), event.getTimestamp(), CharacteristicInfo.C_TIMESTAMP.getName());
    }

}
