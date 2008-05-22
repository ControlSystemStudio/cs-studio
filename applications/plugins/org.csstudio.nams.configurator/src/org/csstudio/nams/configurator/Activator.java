package org.csstudio.nams.configurator;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationEditingStoreService;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.ams.service.logging.declaration.Logger;
import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.csstudio.nams.configurator.treeviewer.ConfigurationTreeView;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the bundles life cycle
 */
public class Activator implements BundleActivator {

	/** 
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.csstudio.nams.configurator";

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ConfigurationEditingStoreService editingStoreService = BundleActivatorUtils.getAvailableService(context, ConfigurationEditingStoreService.class);
		ConfigurationStoreService storeService = BundleActivatorUtils.getAvailableService(context, ConfigurationStoreService.class);
		Logger logger = BundleActivatorUtils.getAvailableService(context, Logger.class);
		
		// Inject into extension classes.
		ConfigurationTreeView.staticInjectEditingStoreService(editingStoreService);
		ConfigurationTreeView.staticInjectStoreService(storeService);
		ConfigurationTreeView.staticInjectLogger(logger);
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// nothing to do.
	}
}
