package org.csstudio.nams.service.history;

import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.extensionPoint.HistoryServiceFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class HistoryActivator extends Plugin {

	private static final String NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT = "implementation";
	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.service.history";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		String extensionPointId = HistoryServiceFactory.class.getName();

		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(extensionPointId);

		if (elements.length != 1) {
			throw new RuntimeException(
					"One and only one extension for extension point \""
							+ extensionPointId
							+ "\" should be present in current runtime configuration!");
		} else {
			Object executableExtension = elements[0]
					.createExecutableExtension(NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT);
			if (!(executableExtension instanceof HistoryServiceFactory)) {
				throw new RuntimeException("Only a extension of type "
						+ HistoryServiceFactory.class.getName()
						+ " for extension point \"" + extensionPointId
						+ "\" is valid!");
			}
			HistoryServiceFactory factory = (HistoryServiceFactory) executableExtension;
			context.registerService(HistoryService.class.getName(), factory
					.createService(), null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}
}
