
package org.csstudio.nams.service.history;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.ExecutableEclipseRCPExtension;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.extensionPoint.HistoryServiceFactory;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class HistoryActivator extends AbstractBundleActivator implements
		BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.history";

	@OSGiBundleActivationMethod
	public OSGiServiceOffers startBundle(
			@OSGiService
			@Required
			final Logger logger,
			@ExecutableEclipseRCPExtension(extensionId = HistoryServiceFactory.class)
			@Required
			final Object historyServiceFactory) throws Exception {
		final OSGiServiceOffers result = new OSGiServiceOffers();

		logger.logInfoMessage(this, "starting bundle: "
				+ HistoryActivator.PLUGIN_ID);

		final HistoryServiceFactory factory = (HistoryServiceFactory) historyServiceFactory;
		result.put(HistoryService.class, factory.createService());
		return result;
	}
}
