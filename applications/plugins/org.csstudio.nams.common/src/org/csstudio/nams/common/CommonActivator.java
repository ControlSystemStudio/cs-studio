package org.csstudio.nams.common;

import org.csstudio.nams.common.service.ExecutionService;
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

	public void start(BundleContext context) throws Exception {
		context.registerService(ExecutionService.class.getName(),
				new DefaultExecutionService(), null);
	}

	public void stop(BundleContext context) throws Exception {
	}
}
