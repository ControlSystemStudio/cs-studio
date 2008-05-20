package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.service.regelwerkbuilder.impl.confstore";

	// The shared instance
	private static Activator plugin;

	private ConfigurationStoreService configurationStoreService;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		configurationStoreService = BundleActivatorUtils.getAvailableService(context, ConfigurationStoreService.class);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public ConfigurationStoreService getConfigurationStoreService() {
		return configurationStoreService;
	}

}
