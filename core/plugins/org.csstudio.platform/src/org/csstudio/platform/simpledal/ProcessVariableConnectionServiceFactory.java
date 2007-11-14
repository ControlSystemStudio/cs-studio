package org.csstudio.platform.simpledal;

import org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService;

/**
 * Factory of {@link IProcessVariableConnectionService ProcessVariableConnectionService}s,
 * 
 * @author C1 WPS / KM, MZ
 */
public class ProcessVariableConnectionServiceFactory {

	/**
	 * Creates a new, empty {@link IProcessVariableConnectionService}.
	 */
	public static IProcessVariableConnectionService createNewServiceInstance() {
		return new ProcessVariableConnectionService();
	}
}
