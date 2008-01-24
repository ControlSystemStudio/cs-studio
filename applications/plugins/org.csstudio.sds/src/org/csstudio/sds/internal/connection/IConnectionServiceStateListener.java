package org.csstudio.sds.internal.connection;

/**
 * A listener, which can be added to a {@link ConnectionService} to listen for
 * state changes.
 * 
 * See
 * {@link ConnectionService#addConnectionServiceStateListener(IConnectionServiceStateListener)}.
 * 
 * 
 * @author Sven Wende
 * 
 */
public interface IConnectionServiceStateListener {
	/**
	 * This method is called, when the state of the connection service has
	 * changed. This is usually the case, when process variables get
	 * connected or disconnected.
	 * 
	 * @param service the connection service
	 */
	void connectionServiceStateChanged(final ConnectionService service);
}
