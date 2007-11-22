package org.csstudio.platform.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * A service to connect to a process variable.
 * 
 * You may use the {@link ProcessVariableConnectionServiceFactory} to create
 * instances of this service.
 * 
 * @author C1 WPS / SW, MZ
 */
public interface IProcessVariableConnectionService {
	/**
	 * Register a listener to be informed on value-changes of Intger-PVs.
	 * 
	 * @param listener
	 *            The listener to be informed about value changes.
	 * @param pv
	 *            the Address of listened PV.
	 * @throws Exception
	 *             In case of a connection error.
	 */
	void registerForLongValues(IProcessVariableValueListener<Integer> listener,
			IProcessVariableAddress pv) throws Exception;

	/**
	 * Register a listener to be informed on value-changes of Double-PVs.
	 * 
	 * @param listener
	 *            The listener to be informed about value changes.
	 * @param pv
	 *            the Address of listened PV.
	 * @throws Exception
	 *             In case of a connection error.
	 */
	void registerForDoubleValues(
			IProcessVariableValueListener<Double> listener,
			IProcessVariableAddress pv) throws Exception;

}
