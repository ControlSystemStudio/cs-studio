package org.csstudio.sds.internal.connection;

/**
 * A listener, which can be added to a {@link Connector} to listen for state
 * changes.
 * 
 * See {@link Connector#addConnectorStateListener(IConnectorStateListener)}.
 * 
 * 
 * @author Sven Wende
 * 
 */
public interface IConnectorStateListener {
	/**
	 * This method is called, when the state of the connector has changed. This
	 * is usually the case, when new values are delivered by the underlying
	 * control system or the connection state of a channel changes.
	 * 
	 * @param connector
	 *            the connector
	 */
	void connectorStateChanged(final Connector connector);
}
