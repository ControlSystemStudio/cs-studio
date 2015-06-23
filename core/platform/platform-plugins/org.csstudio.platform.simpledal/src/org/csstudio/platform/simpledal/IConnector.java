package org.csstudio.platform.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;

/**
 * Represents a connector for a process variable.
 *
 * @author Sven Wende
 *
 */
public interface IConnector {

    /**
     * Initializes the connector.
     */
    void init();

    /**
     * Returns the number of listeners that are currently attached to this
     * connector.
     *
     * @return
     */
    int getListenerCount();

    /**
     * Returns the latest (cached) connection state.
     *
     * @return the latest connection state
     */
    ConnectionState getLatestConnectionState();

    /**
     * Returns the latest (cached) value.
     *
     * @return the latest value
     */
    Object getLatestValue();

    /**
     * Returns the latest (cached) error.
     *
     * @return the latest error
     */
    String getLatestError();

    /**
     * Returns the process variable address.
     *
     * @return the process variable address
     */
    IProcessVariableAddress getProcessVariableAddress();

    /**
     * Returns the value type.
     *
     * @return the value type
     */
    ValueType getValueType();

    /**
     * Returns true, if the connector is disposable.
     *
     * @return true, if the connector is disposable
     */
    boolean isDisposable();

    /**
     * Disposes this connector regardless of its internal state. This could not be undone and must be
     * called very carefully.
     */
    void forceDispose();
}