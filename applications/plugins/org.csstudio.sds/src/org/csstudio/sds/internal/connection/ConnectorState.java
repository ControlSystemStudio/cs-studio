package org.csstudio.sds.internal.connection;

import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.context.ConnectionState;

/**
 * A simple state object for {@link Connector} in POJO style.
 * 
 * @author Sven Wende
 * 
 */
public final class ConnectorState {
	/**
	 * The current connection state.
	 */
	private ConnectionState _currentConnectionState;

	/**
	 * The current dynamic value condition.
	 */
	private DynamicValueCondition _dynamicValueCondition;

	/**
	 * The latest value received.
	 */
	private Object _latestValue;

	/**
	 * The timestamp of the latest value.
	 */
	private long _latestValueTimestamp;

	/**
	 * Constructor.
	 */
	ConnectorState() {
	}

	/**
	 * Returns the current dynamic value condition.
	 * 
	 * @return the current dynamic value condition
	 */
	public DynamicValueCondition getDynamicValueCondition() {
		return _dynamicValueCondition;
	}

	/**
	 * Sets the current dynamic value condition.
	 * 
	 * @param dynamicValueCondition
	 *            the current dynamic value condition
	 */
	public void setDynamicValueCondition(
			final DynamicValueCondition dynamicValueCondition) {
		_dynamicValueCondition = dynamicValueCondition;
	}

	/**
	 * Returns the current connection state.
	 * 
	 * @return the current connection state
	 */
	public ConnectionState getConnectionState() {
		return _currentConnectionState;
	}

	/**
	 * Sets the current connection state.
	 * 
	 * @param currentConnectionState
	 *            the current connection state
	 */
	public void setConnectionState(final ConnectionState currentConnectionState) {
		_currentConnectionState = currentConnectionState;
	}

	/**
	 * Gets the latest received value.
	 * 
	 * @return the latest received value
	 */
	public Object getLatestValue() {
		return _latestValue;
	}

	/**
	 * Sets the latest received value.
	 * 
	 * @param latestValue
	 *            the latest received value
	 */
	public void setLatestValue(final Object latestValue) {
		_latestValue = latestValue;
	}

	/**
	 * Gets the timestamp of the latest received value in ms.
	 * 
	 * @return the timestamp of the latest received value in ms
	 */
	public long getLatestValueTimestamp() {
		return _latestValueTimestamp;
	}

	/**
	 * Sets the timestamp of the latest received value in ms.
	 * 
	 * @param latestValueTimestamp
	 *            the timestamp of the latest received value in ms
	 */
	public void setLatestValueTimestamp(final long latestValueTimestamp) {
		_latestValueTimestamp = latestValueTimestamp;
	}
}
