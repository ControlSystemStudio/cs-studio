package org.csstudio.iter.utility.ologauth;

import org.csstudio.auth.security.SecurityFacade;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	/**
	 * This plugin's ID.
	 */
	public static final String PLUGIN_ID = "org.csstudio.iter.utility.ologauth";

	// The shared instance
	private static Activator plugin;
	private OlogAuthAdapter ologAuthAdapter;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		plugin = this;
		ologAuthAdapter = new OlogAuthAdapter();
		SecurityFacade.getInstance().addUserManagementListener(ologAuthAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		SecurityFacade.getInstance().removeUserManagementListener(
				ologAuthAdapter);
		ologAuthAdapter = null;
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
