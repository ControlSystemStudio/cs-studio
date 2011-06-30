
package org.csstudio.nams.common;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.material.regelwerk.ProcessVariableRegel;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.logging.declaration.Logger;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonActivator extends AbstractBundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.common";

	@OSGiBundleActivationMethod
	public OSGiServiceOffers bundleStart(@OSGiService
	@Required
	final Logger logger) {
		logger.logInfoMessage(this, "Plugin " + CommonActivator.PLUGIN_ID
				+ " is starting...");

		final OSGiServiceOffers serviceOffers = new OSGiServiceOffers();
		serviceOffers
				.put(ExecutionService.class, new DefaultExecutionService());
		ProcessVariableRegel.staticInject(logger);

		return serviceOffers;
	}

	@OSGiBundleDeactivationMethod
	public void stopBundle(@OSGiService
	@Required
	final Logger logger) throws Exception {
		logger.logInfoMessage(this, "Plugin " + CommonActivator.PLUGIN_ID
				+ " stopped succesfully.");
	}
}
