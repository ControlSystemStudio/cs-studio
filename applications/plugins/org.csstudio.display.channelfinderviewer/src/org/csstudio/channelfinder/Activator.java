package org.csstudio.channelfinder;

import gov.bnl.channelfinder.api.ChannelFinderClient;

import java.util.prefs.Preferences;

import org.csstudio.channelfinder.preferences.PreferenceConstants;
import org.csstudio.platform.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.channelfinder"; //$NON-NLS-1$

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
		installPreferences();
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

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void installPreferences() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		Preferences preferences = Preferences
				.userNodeForPackage(ChannelFinderClient.class);
		// try {
		// for (String key : preferences.keys()) {
		// System.out.println(key + " " + preferences.get(key, "default"));
		// }
		// prefs.exportPreferences( prefs.getRootNode() , System.out, null);
		// } catch (BackingStoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		preferences.put("channel_finder_url", prefs.getString(
				Activator.PLUGIN_ID, PreferenceConstants.ChannelFinder_URL, "",
				null));
//		preferences.put("trustStore", prefs.getString(Activator.PLUGIN_ID,
//				PreferenceConstants.TrustStore_Location, "", null));
//		preferences.put("trustPass", SecureStorage.retrieveSecureStorage(
//				Activator.PLUGIN_ID, PreferenceConstants.TrustPass));
		preferences.put("username", prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.Username, "", null));
		preferences.put("password", SecureStorage.retrieveSecureStorage(
				Activator.PLUGIN_ID, PreferenceConstants.Password));
		// try {
		// for (String key : preferences.keys()) {
		// System.out.println(key + " " + preferences.get(key, "default"));
		// prefs.exportPreferences( prefs.getRootNode() , System.out, null);
		// }
		// } catch (BackingStoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
