package org.csstudio.platform.libs.jersey;

import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.platform.libs.jersey";

	private static final String[] PACKAGES = new String[] {"com.sun.jersey" };
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	
	public void start(BundleContext context) throws Exception {
	    super.start(context);
	    plugin = this;

	    Level logLevel = Preferences.getVerboseLogLevel();
	    for (String verbosePackage : PACKAGES) {
		Logger logger = Logger.getLogger(verbosePackage);
		logger.setLevel(logLevel);
		for (Handler handler : logger.getHandlers())
		    handler.setLevel(logLevel);
	    }
	}

	
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

}
