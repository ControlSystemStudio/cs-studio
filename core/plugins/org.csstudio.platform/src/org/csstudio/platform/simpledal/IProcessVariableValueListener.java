package org.csstudio.platform.simpledal;

/**
 * A listener to be informed on changes on PVs or the connection state to a PV.
 * 
 * @author C1 WPS / SW, MZ
 * 
 * @param <E>
 *            The Java-type of received values.
 *            
 * TODO Introduce an use adapter-class!
 */
public interface IProcessVariableValueListener<E> {
	/**
	 * This method will be informed on a change of a PV value. Change also means
	 * the first received value.
	 * 
	 * @param value
	 *            The new value, not null.
	 */
	void valueChanged(E value);

	/**
	 * This method will be informed on changes of the state of the connection to
	 * a PV.
	 * 
	 * @param connectionState
	 *            The new connection state.
	 */
	void connectionStateChanged(ConnectionState connectionState);
}
