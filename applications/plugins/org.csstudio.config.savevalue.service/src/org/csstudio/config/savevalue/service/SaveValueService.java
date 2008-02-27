package org.csstudio.config.savevalue.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A save value service. Classes implementing this service provide the
 * funcitonality for saving a value of a process variable to an IOC. The
 * service is provided via Java Remote Method Invocation (Java RMI). 
 * 
 * @author Joerg Rathlev
 */
public interface SaveValueService extends Remote {
	
	/**
	 * Saves the given value for the given process variable on the given IOC.
	 * 
	 * @param pvName
	 *            the process variable name.
	 * @param iocName
	 *            the name of the IOC.
	 * @param value
	 *            the value to save.
	 * @throws SaveValueServiceException
	 *             if the service could not save the value.
	 * @throws RemoteException
	 *             if a RMI error occurs.
	 */
	void saveValue(String pvName, String iocName, String value)
		throws SaveValueServiceException, RemoteException;

}
