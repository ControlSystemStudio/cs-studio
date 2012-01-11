package org.csstudio.utility.channelfinder;

import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClientComp;
import gov.bnl.channelfinder.api.ChannelFinderClientImpl.CFCBuilder;

import java.util.logging.Logger;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.preference.IPreferenceStore;
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

	private static final Logger log = Logger.getLogger(PLUGIN_ID);

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
		// installCFPreferences();
		final IPreferencesService prefs = Platform.getPreferencesService();
		log.info("Getting perferences"
				+ prefs.getString(Activator.PLUGIN_ID,
						PreferenceConstants.ChannelFinder_URL, "", null));
		registerClients();
		registerPreferenceChangeListner();
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

	private void registerPreferenceChangeListner() {
		// IPreferenceStore preferenceStore =
	}

	public void registerClients() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		String url = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.ChannelFinder_URL, "http://localhost/ChannelFinder", null);
		String username = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.Username, null, null);
		String password = SecureStorage.retrieveSecureStorage(
				Activator.PLUGIN_ID, PreferenceConstants.Password);
		log.info("Creating Channelfinder client : "+ username + "@"+url);
		ChannelFinderClientComp compositeClient = ChannelFinderClientComp
				.getInstance();
		compositeClient.setReader(CFCBuilder.serviceURL(url).create());
		compositeClient.setWriter(CFCBuilder.serviceURL(url)
				.withHTTPAuthentication(true).username(username)
				.password(password).create());
		ChannelFinder.setClient(compositeClient);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	// private static String nullToEmpty(String string) {
	// return string == null ? "" : string;
	// }

	// public void installCFPreferences() {
	// final IPreferencesService prefs = Platform.getPreferencesService();
	// Preferences preferences = Preferences
	// .userNodeForPackage(ChannelFinderClient.class);
	// preferences.put("channel_finder_url", prefs.getString(
	// Activator.PLUGIN_ID, PreferenceConstants.ChannelFinder_URL, "",
	// null));
	// preferences.put("username", prefs.getString(Activator.PLUGIN_ID,
	// PreferenceConstants.Username, "", null));
	// preferences.put(
	// "password",
	// nullToEmpty(SecureStorage.retrieveSecureStorage(
	// Activator.PLUGIN_ID,
	// PreferenceConstants.Password)));
	// }

}
