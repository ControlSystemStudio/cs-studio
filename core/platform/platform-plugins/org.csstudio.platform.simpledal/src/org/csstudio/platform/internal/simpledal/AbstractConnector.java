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
package org.csstudio.platform.internal.simpledal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.dal.Timestamp;
import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IConnector;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.SettableState;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for connectors. A connector encapsulates all program logic that is
 * needed to use a certain application layer that accesses process variables.
 *
 * A connector can be used for one-time-action, e.g. getting or setting a
 * process variable�s value as well for permanent-action which means to register
 * listeners for updates of process variables values.
 *
 * For convenience the {@link IProcessVariableValueListener}s are only weakly
 * referenced. The connector tracks for {@link IProcessVariableValueListener}s
 * that have been garbage collected and removes those references from its
 * internal list. This way {@link IProcessVariableValueListener}s don�t have to
 * be removed from the connector explicitly.
 *
 * @author Sven Wende, Xihui Chen
 *
 */
@SuppressWarnings("unchecked")
public abstract class AbstractConnector implements IConnector, IProcessVariableAdressProvider {// TODO jhatje , IProcessVariable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConnector.class);

    public static final int BLOCKING_TIMEOUT = 5000;

    /**
     * True, if the connector has been initialized.
     */
    private boolean initialized = false;

    /**
     * The latest received value.
     */
    private Object _latestValue;

    /**
     * The latest received connection state.
     */
    private ConnectionState _latestConnectionState = ConnectionState.INITIAL;

    /**
     * The process variable pointer for the channel this connector is connected
     * to.
     */
    private IProcessVariableAddress _processVariableAddress;

    /**
     * A list of value listeners to which control system events are forwarded.
     */
    protected List<ListenerReference> _weakListenerReferences;

    /**
     * The latest error.
     */
    private String _latestError;

    /**
     * The value type.
     */
    private ValueType _valueType;

    /**
     * Time in milliseconds until which this connector is not disposed.
     */
    private long _keepAliveUntil = 0;

    /**
     * Constructor.
     *
     * @param pvAddress
     *            the address of the process variable this connector is for
     * @param valueType
     *            the type of values expected
     */
    public AbstractConnector(IProcessVariableAddress pvAddress, ValueType valueType) {
        assert pvAddress != null;
        assert valueType != null;
        _processVariableAddress = pvAddress;
        _valueType = valueType;
        _weakListenerReferences = Collections.synchronizedList(new ArrayList<ListenerReference>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void init() {
        if (!initialized) {
            try {
                doInit();
            } catch (Exception e) {
                LOG.error(e.toString());
            }
            initialized = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getListenerCount() {
        return _weakListenerReferences.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ConnectionState getLatestConnectionState() {
        return _latestConnectionState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object getLatestValue() {
        return _latestValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getLatestError() {
        return _latestError;
    }

    /**
     * Forwards the specified connection state to all registered listeners.
     *
     * @param connectionState
     *            the connection state
     */
    protected final void forwardConnectionState(ConnectionState connectionState) {
        doForwardConnectionStateChange(connectionState);
    }

    /**
     * Forwards the specified value to all registered listeners.
     *
     * @param value
     *            the value
     */
    protected final void forwardValue(Object value) {
        doForwardValue(value, new Timestamp());
    }

    /**
     * Forwards the specified value to all registered listeners.
     *
     * @param value
     *            the value
     */
    protected final void forwardError(String error) {
        doForwardError(error);
        _latestError = error;
    }

    /**
     * Adds a value listener. When a characteristic is provided, the specified
     * listener is only informed of changes in that characteristic.
     *
     * @param listener
     *            a value listener (has to be !=null)
     *
     * @param characteristicId
     *            the id of a characteristic (can be null)
     */
    public final void addProcessVariableValueListener(String characteristicId, IProcessVariableValueListener listener) {

        assert listener != null;

        _weakListenerReferences.add(new ListenerReference(characteristicId, listener));

        sendInitialValuesForNewListener(characteristicId, listener);
    }

    protected void sendInitialValuesForNewListener(String characteristicId, IProcessVariableValueListener listener) {
        // send initial connection state
        if (_latestConnectionState != null) {
            listener.connectionStateChanged(_latestConnectionState);
        }

        // send initial value
        if (_latestValue != null && characteristicId == null) {
            listener.valueChanged(_latestValue, null);
        }

        // send latest error
        if (_latestError != null) {
            listener.errorOccured(_latestError);
        }

        // send characteristics
        if (characteristicId != null) {
            getCharacteristicAsynchronously(characteristicId, getValueType(), listener);
        }
    }

    /**
     * Removes the specified value listener. This is optional - a connector does
     * reference its listeners weak by design. This way connectors that are no
     * longer referenced outside of the connector will get garbage collected
     * anyway.
     *
     * @param listener
     *            the value listener to be removed from the connector
     */
    public final boolean removeProcessVariableValueListener(IProcessVariableValueListener listener) {
        ListenerReference[] listeners = getWeakReferenceListeners();
        ListenerReference toRemove = null;

        for (ListenerReference ref : listeners) {
            IProcessVariableValueListener lr = ref.getListener();
            if (lr != null && listener == lr) {
                toRemove = ref;
                _weakListenerReferences.remove(toRemove);
                return true;
            }
        }
        return false;
    }

    /**
     * Determines, whether this connector can get disposed. This is, when no
     * value listeners for the connector live in the JVM anymore.
     *
     * @return true, if this connector can be disposed, false otherwise
     */
    public final boolean isDisposable() {
        // perform a cleanup first
        // cleanupWeakReferences();

        // this connector can be disposed it there are not weak references left
        return _weakListenerReferences.isEmpty() && !isBlocked();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IProcessVariableAddress getProcessVariableAddress() {
        return _processVariableAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ValueType getValueType() {
        return _valueType;
    }

    /**
     * Determines whether it is possible to write values to the underlying
     * process variable.
     *
     * @return a state that defines whether write access is possible in a
     *         yes/no/unknown manner
     */
    public final SettableState isSettable() {
        SettableState state = null;
        try {
            state = doIsSettable();
        } catch (Exception e) {
            LOG.error(e.toString());
        }

        return state != null ? state : SettableState.UNKNOWN;
    }

    /**
     * Disposes the connector.
     */
    public final void dispose() {
        try {
            doDispose();
        } catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void forceDispose() {
        _weakListenerReferences.clear();
        dispose();
    }

    /**
     * Queries the current value using a synchronous call that will block the
     * current thread.
     *
     * @param <E>
     *            return type
     *
     * @return the current value or null
     * @throws Exception
     */
    public final <E> E getValueSynchronously() throws Exception {

        Object value = doGetValueSynchronously();

        E result = value != null ? (E) ConverterUtil.convert(value, getValueType()) : null;

        return result;
    }

    /**
     * Queries the current value using an asynchronous call. The result will be
     * reported by calling the valueChanged() method on the specified listener.
     *
     * @param <E>
     *            return type
     */
    public final void getValueAsynchronously(final IProcessVariableValueListener listener) {
        try {
            doGetValueAsynchronously(listener);
        } catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    /**
     * Queries the specified characteristic using a synchronous call.The result
     * will be reported by calling the valueChanged() method on the specified
     * listener.
     *
     * @param <E>
     *            return type
     * @param characteristicId
     *            the characteristic id
     * @param valueType
     *            the type of the expected value
     *
     * @return the current value or null
     * @throws Exception
     */
    public final <E> E getCharacteristicSynchronously(String characteristicId, final ValueType valueType) throws Exception {

        Object value = doGetCharacteristicSynchronously(characteristicId, valueType);

        E result = value != null ? (E) value : null;

        return result;
    }

    /**
     * Queries the specified characteristic using an asynchronous call that will
     * block the current thread.
     *
     * @param <E>
     *            return type
     * @param characteristicId
     *            the characteristic id
     * @param valueType
     *            the type of the expected value
     *
     * @return the current value or null
     */
    public final void getCharacteristicAsynchronously(final String characteristicId, final ValueType valueType,
            final IProcessVariableValueListener listener) {
        try {
            doGetCharacteristicAsynchronously(characteristicId, valueType, listener);
        } catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    /**
     * Sets the specified value using an asynchronous call.
     *
     * @param value
     *            the value to be set
     * @param listener
     *            an optional call-back listener
     */
    public final void setValueAsynchronously(Object value, final IProcessVariableWriteListener listener) {
        try {
            doSetValueAsynchronously(value, listener);
        } catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    /**
     *
     * @param value
     * @return
     */
    public final boolean setValueSynchronously(Object value) throws Exception {
        boolean result = false;
        result = doSetValueSynchronously(value);
        return result;
    }

    /**
     * Template method. Subclasses should determine whether write access to the
     * underlying process variable is possible.
     *
     * @return a state that defines whether write access is possible in a
     *         yes/no/unknown manner
     * @throws Exception
     *             an arbitrary exception
     */
    protected abstract SettableState doIsSettable() throws Exception;

    /**
     * Template method. Subclasses should initialize the physical connection
     * here.
     *
     * @throws Exception
     *             an arbitrary exception
     *
     */
    protected abstract void doInit() throws Exception;

    /**
     * Template method. Subclasses should shut down all physical connections
     * here.
     *
     * @throws Exception
     *             an arbitrary exception
     *
     */
    protected abstract void doDispose() throws Exception;

    /**
     * Template method. Subclasses should implement asynchronous access logic
     * for characteristics here.
     *
     * @param characteristicId
     *            the characteristic id
     * @param valueType
     *            the expected value type
     * @param listener
     *            the listener has to be informed *
     * @throws Exception
     *             an arbitrary exception
     */
    protected abstract void doGetCharacteristicAsynchronously(String characteristicId, ValueType valueType,
            IProcessVariableValueListener listener) throws Exception;

    /**
     * Template method. Subclasses should implement synchronous access logic for
     * characteristics here.
     *
     * @param characteristicId
     *            the characteristic id
     * @param valueType
     *            the expected value type
     *
     * @return the characteristic value or null
     *
     * @throws Exception
     *             an arbitrary exception
     */
    protected abstract Object doGetCharacteristicSynchronously(String characteristicId, ValueType valueType) throws Exception;

    /**
     * Template method. Subclasses should implement synchronous access logic for
     * the value here.
     *
     * @return the current value or null
     *
     * @throws Exception
     *             an arbitrary exception
     */
    protected abstract Object doGetValueSynchronously() throws Exception;

    /**
     * Template method. Subclasses should implement asynchronous access logic
     * for the value here.
     *
     * @param listener
     *            the listener that has to be informed in a call-back style *
     * @throws Exception
     *             an arbitrary exception
     */
    protected abstract void doGetValueAsynchronously(final IProcessVariableValueListener listener) throws Exception;;

    /**
     * Template method. Subclasses should implement synchronous logic for
     * setting a value here.
     *
     * @param value
     *            the value to be set
     *
     * @return true on success, false otherwise
     * @throws Exception
     *             an arbitrary exception
     */
    protected abstract boolean doSetValueSynchronously(Object value) throws Exception;

    /**
     * Template method. Subclasses should implement asynchronous logic for
     * setting a value here.
     *
     * @param value
     *            the value to be set
     * @param listener
     *            an optional call-back listener
     *
     * @throws Exception
     *             an arbitrary exception
     */
    protected abstract void doSetValueAsynchronously(Object value, final IProcessVariableWriteListener listener) throws Exception;

    /**
     * Forward the specified connection event to the value listeners.
     *
     * @param event
     *            the DAL connection event
     */
    protected final void doForwardConnectionStateChange(final ConnectionState connectionState) {
        if (connectionState != null) {
            // remember the latest state
            _latestConnectionState = connectionState;

            execute(new IInternalRunnable() {
                public void doRun(IProcessVariableValueListener valueListener, String characteristicId) {
                    valueListener.connectionStateChanged(connectionState);
                }

                public void doRun(IProcessVariableValueListener valueListener) {
                    valueListener.connectionStateChanged(connectionState);
                }
            });
        }
    }

    /**
     * Forward the current value with its time stamp.
     *
     * @param value
     *            the value
     * @param timestamp
     *            the time stamp of the latest event
     */
    protected final void doForwardValue(final Object value, final Timestamp timestamp) {
        if (value != null) {

            execute(new IInternalRunnable() {
                public void doRun(IProcessVariableValueListener valueListener, String characteristicId) {
                    // nothing to do - this is for "normal" value listeners only

                }

                public void doRun(IProcessVariableValueListener valueListener) {
                    try {
                        valueListener.valueChanged(ConverterUtil.convert(value, _valueType), timestamp);
                        // memorize the latest value
                        _latestValue = value;
                    } catch (NumberFormatException nfe) {
                        // Do nothing! Is a invalid value format!
                        LOG.warn("Invalid value format. (" + value + ") is not set to " + getName() + ".");
                    }
                }
            });
        }
    }

    /**
     * Forwards the specified characteristic value to all listeners registered
     * for that characteristic.
     *
     * @param characteristicValue
     *            the characteristic value
     * @param timestamp
     *            the timestamp
     * @param characteristicId
     *            the characteristic id
     */
    protected final void doForwardCharacteristic(final Object characteristicValue, final Timestamp timestamp, final String characteristicId) {
        if (characteristicValue != null && characteristicId != null) {

            // forward the value
            execute(new IInternalRunnable() {
                public void doRun(IProcessVariableValueListener valueListener, String cId) {
                    // forward the value only, if the current listener is
                    // registered for the same characteristic id
                    if (cId != null && cId.equals(characteristicId)) {
                        valueListener.valueChanged(characteristicValue, timestamp);
                    }
                }

                public void doRun(IProcessVariableValueListener valueListener) {
                    // do not forward the value because these listeners are not
                    // registered for a characteristic

                }
            });
        }
    }

    /**
     * Forward the current value.
     *
     * @param event
     *            the DAL connection event
     */
    protected final void doForwardError(final String error) {
        execute(new IInternalRunnable() {
            public void doRun(IProcessVariableValueListener valueListener, String characteristicId) {
                valueListener.errorOccured(error);
            }

            public void doRun(IProcessVariableValueListener valueListener) {
                valueListener.errorOccured(error);
            }
        });
    }

    /**
     * Updates all listeners that are connected to a characteristic.
     *
     * Characteristics are requested asynchronously.
     *
     * FIXME: This is a only workaround. DAL should deliver propertyChange()
     * Events for characteristics whenever a DalProperty switches to "connected"
     * state. The same already works for valueChanged() updates.
     */
    protected void updateCharacteristicListeners() {
        ListenerReference[] listeners = getWeakReferenceListeners();
        for (ListenerReference ref : listeners) {
            IProcessVariableValueListener listener = ref.getListener();

            if (listener != null && ref.getCharacteristicId() != null) {
                getCharacteristicAsynchronously(ref.getCharacteristicId(), getValueType(), listener);
            }
        }

    }

    /**
     * Logs a debug message that is prefixed with common connector information
     * (e.g. the name of the process variable).
     *
     * @param message
     *            the message
     */
    protected void printDebugInfo(String message) {
        StringBuffer sb = new StringBuffer();
        sb.append(getProcessVariableAddress().toString());
        sb.append(": ");
        sb.append(message);

        LOG.debug(sb.toString());
    }

    /**
     * Executes the specified runnable for all existing value listeners.
     *
     * Only for valid listeners that still live in the JVM the hook method
     * {@link IInternalRunnable#doRun(IProcessVariableValueListener)} is called.
     *
     * @param runnable
     *            the runnable
     */
    private void execute(IInternalRunnable runnable) {
        ListenerReference[] listeners = getWeakReferenceListeners();
        for (ListenerReference wr : listeners) {
            IProcessVariableValueListener listener = wr.getListener();

            if (listener != null) {
                // split the calls for listeners that are registered for a
                // characteristic and those which are registered for a
                // "normal" value
                if (wr.getCharacteristicId() != null) {
                    runnable.doRun(listener, wr.getCharacteristicId());
                } else {
                    runnable.doRun(listener);
                }
            }
        }

    }

    /**
     * Removes weak references for value listeners that have been garbage
     * collected.
     */
    void cleanupWeakReferences() {
        ListenerReference[] listeners = getWeakReferenceListeners();
        List<ListenerReference> deletionCandidates = new ArrayList<ListenerReference>();
        for (ListenerReference ref : listeners) {
            if (ref.getListener() == null) {
                deletionCandidates.add(ref);
            }
        }
        synchronized (_weakListenerReferences) {
            for (ListenerReference wr : deletionCandidates) {
                _weakListenerReferences.remove(wr);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _processVariableAddress.getProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IProcessVariableAddress> getProcessVariableAdresses() {
        return Collections.singletonList(_processVariableAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IProcessVariableAddress getPVAdress() {
        return _processVariableAddress;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return _processVariableAddress.toString();
    }

//    /**
//     * {@inheritDoc}
//     */
//    public String getTypeId() {
//        return IProcessVariable.TYPE_ID;
//    }

    public void block() {
        _keepAliveUntil = System.currentTimeMillis() + BLOCKING_TIMEOUT;
    }

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapterType) {
        return Platform.getAdapterManager().getAdapter(this, adapterType);
    }

    private boolean isBlocked() {
        long diff = _keepAliveUntil - System.currentTimeMillis();
        return diff > 0;
    }

    private ListenerReference[] getWeakReferenceListeners() {
        ListenerReference[] ret = null;
        synchronized (_weakListenerReferences) {
            ret = _weakListenerReferences.toArray(new ListenerReference[_weakListenerReferences.size()]);
        }
        return ret;
    }

    /**
     * Runnable which is used to forward events to process variable value
     * listeners.
     *
     * @author Sven Wende
     *
     */
    interface IInternalRunnable {
        /**
         * Hook which is called only for valid value listeners, which still live
         * in the JVM and have not already been garbage collected AND that is
         * registered for a specific characteristic id.
         *
         * @param valueListener
         *            a value listener instance (it is ensured, that this is not
         *            null)
         * @param characteristicId
         *            the characteristic id (it is ensured, that this is not
         *            null)
         */
        void doRun(IProcessVariableValueListener valueListener, String characteristicId);

        /**
         * Hook which is called only for valid value listeners, which still live
         * in the JVM and have not already been garbage collected.
         *
         * @param valueListener
         *            a value listener instance (we ensure, that this is not
         *            null)
         */
        void doRun(IProcessVariableValueListener valueListener);
    }

    /**
     * Keeps a weak reference to a listener. Because of its weakness this
     * reference does not prevent the listener from getting garbage collected
     * when its not references elsewhere.
     *
     * A listener could have been registered for a specific characteristic id,
     * which is a special case, as several characteristics can be delivered via
     * the same connection and can be reported as a reaction to certain system
     * events.
     *
     * @author Sven Wende
     */
    static class ListenerReference {

        private WeakReference<IProcessVariableValueListener> _listener;
        private String _characteristicId = null;

        public ListenerReference(String characteristic, IProcessVariableValueListener<?> listener) {
            _characteristicId = characteristic;
            _listener = new WeakReference<IProcessVariableValueListener>(listener);
        }

        public boolean isCharacteristic() {
            return _characteristicId != null;
        }

        public IProcessVariableValueListener<?> getListener() {
            return _listener.get();
        }

        public String getCharacteristicId() {
            return _characteristicId;
        }
    }
}
