package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.csstudio.nams.common.testhelper.ForTesting;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JMSActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.service.messaging.impl.jms";
	
	/**
	 * Logger
	 */
	private static Logger logger;
	
	/**
	 * The constructor
	 */
	public JMSActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		logger = BundleActivatorUtils.getAvailableService(context, Logger.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		logger = null;
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	@ForTesting
	public static void setLogger(Logger logger) {
		JMSActivator.logger = logger;
	}
}
