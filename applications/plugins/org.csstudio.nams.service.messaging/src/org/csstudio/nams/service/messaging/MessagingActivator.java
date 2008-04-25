package org.csstudio.nams.service.messaging;

import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;
import org.csstudio.nams.service.messaging.declaration.ProducerFactoryService;
import org.csstudio.nams.service.messaging.extensionPoint.ConsumerFactoryServiceFactory;
import org.csstudio.nams.service.messaging.extensionPoint.ProducerFactoryServiceFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MessagingActivator extends Plugin {

	private static final String NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT = "implementation";
	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.messaging";

	// private ServiceTracker _ensureJustOneInstanceTracker;

	/**
	 * The constructor
	 */
	public MessagingActivator() {
	}

	/**
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// ConsumerFactoryServiceFactory
		String extensionPointIdConsumerServiceFactory = ConsumerFactoryServiceFactory.class
				.getName();
		IConfigurationElement[] elementsConsumerFactoryServiceFactory = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointIdConsumerServiceFactory);

		if (elementsConsumerFactoryServiceFactory.length != 1) {
			throw new RuntimeException(
					"One and only one extension for extension point \""
							+ extensionPointIdConsumerServiceFactory
							+ "\" should be present in current runtime configuration!");
		} else {
			Object executableExtension = elementsConsumerFactoryServiceFactory[0]
					.createExecutableExtension(NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT);
			if (!(executableExtension instanceof ConsumerFactoryServiceFactory)) {
				throw new RuntimeException("Only a extension of type "
						+ ConsumerFactoryServiceFactory.class.getName()
						+ " for extension point \""
						+ extensionPointIdConsumerServiceFactory
						+ "\" is valid!");
			}
			ConsumerFactoryServiceFactory factory = (ConsumerFactoryServiceFactory) executableExtension;
			context.registerService(ConsumerFactoryService.class.getName(),
					factory.createConsumerFactoryService(), null);
		}

		// ProducerFactoryServiceFactory
		String extensionPointIdProducerServiceFactory = ProducerFactoryServiceFactory.class
				.getName();
		IConfigurationElement[] elementsProducerFactoryServiceFactory = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointIdProducerServiceFactory);

		if (elementsProducerFactoryServiceFactory.length != 1) {
			throw new RuntimeException(
					"One and only one extension for extension point \""
							+ extensionPointIdProducerServiceFactory
							+ "\" should be present in current runtime configuration!");
		} else {
			Object executableExtension = elementsProducerFactoryServiceFactory[0]
					.createExecutableExtension(NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT);
			if (!(executableExtension instanceof ProducerFactoryServiceFactory)) {
				throw new RuntimeException("Only a extension of type "
						+ ProducerFactoryServiceFactory.class.getName()
						+ " for extension point \""
						+ extensionPointIdProducerServiceFactory
						+ "\" is valid!");
			}
			ProducerFactoryServiceFactory factory = (ProducerFactoryServiceFactory) executableExtension;
			context.registerService(ProducerFactoryService.class.getName(),
					factory.createProducerFactoryService(), null);
		}
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		// _ensureJustOneInstanceTracker.close();
	}
}
