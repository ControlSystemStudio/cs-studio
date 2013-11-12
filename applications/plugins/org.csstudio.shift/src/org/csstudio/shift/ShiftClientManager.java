package org.csstudio.shift;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class ShiftClientManager {
	
	private ShiftClientManager() {

	}

	/**
	 * 
	 * @return the registered ShiftClientFactory
	 * @throws Exception
	 */
	public static ShiftClientFactory getShiftClientFactory() throws Exception {

		final IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(ShiftClientFactory.EXTENSION_ID);
		
        if (configs.length != 1) {
            throw new Exception("Got " + configs.length + " instead of 1 " + ShiftClientFactory.EXTENSION_ID + " implementations");
        }
        return (ShiftClientFactory) configs[0].createExecutableExtension("class");			
	}
}
