package org.csstudio.rap.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

import org.csstudio.rap.core.preferences.PreferenceHelper;
import org.csstudio.rap.core.preferences.ServerScope;
import org.eclipse.core.internal.preferences.PreferencesService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		serverHeartBeatThread = ServerHeartBeatThread.getInstance();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
		startupTime = sdf.format(cal.getTime());
		
		//Set the default preference lookup order for all plugins 
		((PreferencesService)Platform.getPreferencesService()). setDefaultDefaultLookupOrder(
				new String[] { //
				InstanceScope.SCOPE, //
				ConfigurationScope.SCOPE, //
				ServerScope.SCOPE, //$NON-NLS-1$
				DefaultScope.SCOPE});
		
		// set security configuration
		Configuration.setConfiguration(new Configuration() {

			@Override
			public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
				Map<String, String> optionsMap = PreferenceHelper.getLoginModuleOptions();
				optionsMap.put("extensionId", //$NON-NLS-1$
						PreferenceHelper.getLoginModuleExtensionId());
				AppConfigurationEntry entry = new AppConfigurationEntry(
						"org.eclipse.equinox.security.auth.module.ExtensionLoginModule", //$NON-NLS-1$
						LoginModuleControlFlag.REQUIRED, optionsMap);
				return new AppConfigurationEntry[] { entry };
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	public static Logger getLogger() {
		return logger;
	}

	public static String getStartupTime() {
		return startupTime;
	}

}
