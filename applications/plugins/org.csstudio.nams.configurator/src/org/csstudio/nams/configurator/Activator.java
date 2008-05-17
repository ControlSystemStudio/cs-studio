package org.csstudio.nams.configurator;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationEditingStoreService;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.ams.service.logging.declaration.Logger;
import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.configurator";

	// The shared instance
	private static Activator plugin;

	private ConfigurationEditingStoreService _editingStoreService;

	private ConfigurationStoreService _storeService;

	private Logger _logger;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		_editingStoreService = BundleActivatorUtils.getAvailableService(context, ConfigurationEditingStoreService.class);
		_storeService = BundleActivatorUtils.getAvailableService(context, ConfigurationStoreService.class);
		_logger = BundleActivatorUtils.getAvailableService(context, Logger.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public ConfigurationEditingStoreService getEditingStoreService() {
		return _editingStoreService;
	}

	public ConfigurationStoreService getStoreService() {
		return _storeService;
	}

	public Logger getLogger() {
		return _logger;
	}

}
