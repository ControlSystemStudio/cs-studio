package org.csstudio.nams.common;

import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonActivator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.common";

	/**
	 * The constructor
	 */
	public CommonActivator() {
	}

	/**
	 * Logger
	 */
	private static Logger logger;
	
	public void start(BundleContext context) throws Exception {
		context.registerService(ExecutionService.class.getName(),
				new DefaultExecutionService(), null);
		logger = BundleActivatorUtils.getAvailableService(context, Logger.class);

	}

	public static Logger getLogger(){
		return logger;
	}
	
	public void stop(BundleContext context) throws Exception {
	}
}
