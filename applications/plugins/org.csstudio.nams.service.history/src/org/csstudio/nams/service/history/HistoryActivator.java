package org.csstudio.nams.service.history;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.extensionPoint.HistoryServiceFactory;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class HistoryActivator extends AbstractBundleActivator implements BundleActivator {

	private static final String NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT = "implementation";
	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.history";

	@OSGiBundleActivationMethod
	public OSGiServiceOffers startBundle(@OSGiService @Required Logger logger) throws Exception {
		OSGiServiceOffers result = new OSGiServiceOffers();
		
		logger.logInfoMessage(this, "starting bundle: "+PLUGIN_ID);
		
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
			result.put(HistoryService.class, factory.createService());
		}
		return result;
	}
}
