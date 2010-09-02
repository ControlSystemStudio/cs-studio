package org.remotercp.login;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LoginActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.login";

	// The shared instance
	private static LoginActivator plugin;

	private static BundleContext bundlecontext;

	/**
	 * The constructor
	 */
	public LoginActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		bundlecontext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
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
	public static LoginActivator getDefault() {
		return plugin;
	}

	public static BundleContext getBundleContext() {
		return bundlecontext;
	}

	public static ImageDescriptor getImageDescriptor(String imageFilePath) {
		return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
	}
}
