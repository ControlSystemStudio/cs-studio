package org.csstudio.nams.common;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonActivator extends AbstractBundleActivator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.common";

	@OSGiBundleActivationMethod
	public OSGiServiceOffers bundleStart(
			@OSGiService @Required Logger logger
	) {
		logger.logInfoMessage(this, "Plugin " + PLUGIN_ID
				+ " is starting...");
		
		OSGiServiceOffers serviceOffers = new OSGiServiceOffers();
		serviceOffers.put(ExecutionService.class, new DefaultExecutionService());
		//TODO move inject to org.csstudio.nams.common.regelwerk
//		ProcessVariableRegel.staticInject(logger);

		return serviceOffers;
	}
	
	@OSGiBundleDeactivationMethod
	public void stopBundle(@OSGiService @Required Logger logger) throws Exception {
		logger.logInfoMessage(this, "Plugin " + PLUGIN_ID
				+ " stopped succesfully.");
	}
}
