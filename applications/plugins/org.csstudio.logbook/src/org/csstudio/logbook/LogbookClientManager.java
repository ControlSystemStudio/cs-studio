package org.csstudio.logbook;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * @author shroffk
 * 
 */
public class LogbookClientManager {

	private LogbookClientManager() {

	}

	public static LogbookClientFactory getLogbookClientFactory() throws Exception {
		final IConfigurationElement[] configs = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(LogbookClientFactory.EXTENSION_ID);
        if (configs.length != 1)
            throw new Exception("Got " + configs.length + " instead of 1 "
                    + LogbookClientFactory.EXTENSION_ID + " implementations");
        return (LogbookClientFactory) configs[0].createExecutableExtension("class");
			
	}

}
