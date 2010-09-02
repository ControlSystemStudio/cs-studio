package org.remotercp.preferences;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PreferencesActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.preferences";

	// The shared instance
	private static PreferencesActivator plugin;

	private static BundleContext bundleContext;


	/**
	 * The constructor
	 */
	public PreferencesActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		bundleContext = context;

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
	public static PreferencesActivator getDefault() {
		return plugin;
	}
	
	public static BundleContext getBundleContext(){
		return bundleContext;
	}

}
