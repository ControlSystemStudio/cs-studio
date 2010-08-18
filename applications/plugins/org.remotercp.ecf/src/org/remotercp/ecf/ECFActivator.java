package org.remotercp.ecf;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.ecf.session.SessionServiceImpl;

/**
 * The activator class controls the plug-in life cycle
 */
public class ECFActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.ecf";

	// The shared instance
	private static ECFActivator plugin;
	
	private static BundleContext bundleContext;

	/**
	 * The constructor
	 */
	public ECFActivator() {
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

		this.registerServices(context);
	}

	private void registerServices(BundleContext context) {
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(Constants.SERVICE_VENDOR, "org.eclipsercp");
		props.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);

		context.registerService(ISessionService.class.getName(),
				new SessionServiceImpl(), props);

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
	public static ECFActivator getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the bundle context of this plug-in.
	 * 
	 * @return the bundle context.
	 */
	public static BundleContext getBundleContext() {
		return bundleContext;
	}

}
