package org.csstudio.rap.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RAPCorePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.rap.core"; //$NON-NLS-1$

	// The shared instance
	private static RAPCorePlugin plugin;
	
	private ServerHeartBeatThread serverHeartBeatThread;

	final private static Logger logger = Logger.getLogger(PLUGIN_ID);
	private static String startupTime;

	
	/**
	 * The constructor
	 */
	public RAPCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		serverHeartBeatThread = ServerHeartBeatThread.getInstance();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
		startupTime = sdf.format(cal.getTime());
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
	public static RAPCorePlugin getDefault() {
		return plugin;
	}
	
	
	public ServerHeartBeatThread getServerHeartBeatThread() {
		return serverHeartBeatThread;
	}
	
	/** @return Logger for the plugin */
	public static Logger getLogger()
	{
	    return logger;
	}
	
	public static String getStartupTime() {
		return startupTime;
	}

}
