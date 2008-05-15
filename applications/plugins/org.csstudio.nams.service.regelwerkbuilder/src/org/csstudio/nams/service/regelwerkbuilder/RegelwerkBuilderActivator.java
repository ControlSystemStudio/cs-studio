package org.csstudio.nams.service.regelwerkbuilder;

import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.csstudio.nams.service.regelwerkbuilder.extensionPoint.RegelwerkBuilderServiceFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RegelwerkBuilderActivator extends Plugin {

	private static final String NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT = "implementation";
	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.service.regelwerkbuilder";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		String extensionPointId = RegelwerkBuilderServiceFactory.class
				.getName();

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
			if (!(executableExtension instanceof RegelwerkBuilderServiceFactory)) {
				throw new RuntimeException("Only a extension of type "
						+ RegelwerkBuilderServiceFactory.class.getName()
						+ " for extension point \"" + extensionPointId
						+ "\" is valid!");
			}
			RegelwerkBuilderServiceFactory factory = (RegelwerkBuilderServiceFactory) executableExtension;
			context.registerService(RegelwerkBuilderService.class.getName(),
					factory.createService(), null);
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
