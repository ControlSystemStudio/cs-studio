package org.csstudio.utility.channelfinder;

import gov.bnl.channelfinder.api.ChannelFinderClient;

import java.util.prefs.Preferences;

import org.csstudio.platform.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.channelfinder";

	// The shared instance
	private static Activator plugin;

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
		super.start(context);
		plugin = this;
		installCFPreferences();
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
	public static Activator getDefault() {
		return plugin;
	}
	
	private static String nullToEmpty(String string) {
		return string == null ? "" : string;
	}

	public void installCFPreferences() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		Preferences preferences = Preferences
				.userNodeForPackage(ChannelFinderClient.class);
		preferences.put("channel_finder_url", prefs.getString(
				Activator.PLUGIN_ID, PreferenceConstants.ChannelFinder_URL, "",
				null));
		preferences.put("username", prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.Username, "", null));
		// FIXME 3.0.0 SecureStorage dependency
		preferences.put(
				"password",
				nullToEmpty(SecureStorage.retrieveSecureStorage(
								Activator.PLUGIN_ID,
								PreferenceConstants.Password)));
	}

}
