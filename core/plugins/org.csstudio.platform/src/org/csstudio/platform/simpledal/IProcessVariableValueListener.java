package org.csstudio.platform.simpledal;

/**
 * Listener, which can be connected to control system channels using an
 * {@link IProcessVariableConnectionService}.
 * 
 * @author Sven Wende
 * 
 * @param <E>
 *            the type of channel values expected (String, Double, etc.)
 */
public interface IProcessVariableValueListener<E> {
	/**
	 * Announces a value change on the channel.
	 * 
	 * @param value
	 *            the latest value
	 */
	void valueChanged(E value);

	/**
	 * Announces a connection state change.
	 * 
	 * @param connectionState
	 *            the current connection state
	 */
	void connectionStateChanged(ConnectionState connectionState);
}
