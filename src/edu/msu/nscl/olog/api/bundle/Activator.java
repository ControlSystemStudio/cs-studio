package edu.msu.nscl.olog.api.bundle;

import java.util.logging.Logger;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.msu.nscl.olog.api.OlogClient;
import edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder;
import edu.msu.nscl.olog.api.OlogClientManager;

public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "edu.msu.nscl.olog.api";

	private static final Logger log = Logger.getLogger(PLUGIN_ID);

	private static Activator plugin;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		final IPreferencesService prefs = Platform.getPreferencesService();
		log.info("Getting perferences"
				+ prefs.getString(Activator.PLUGIN_ID,
						PreferenceConstants.Olog_URL, "", null));
		initialize();
		registerClients();
	}

	private void initialize() {
		
	}

	private void registerClients() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		OlogClientBuilder ologClientBuilder;
		String url = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.Olog_URL,
				"http://localhost:8080/Olog/resources", null);
		String jcr_url = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.Olog_jcr_URL,
				"http://localhost:8080/Olog/repository/olog", null);
		ologClientBuilder = OlogClientBuilder.serviceURL(url).jcrURI(jcr_url);
		if (prefs.getBoolean(Activator.PLUGIN_ID,
				PreferenceConstants.Use_authentication, false, null)) {
			ologClientBuilder
					.withHTTPAuthentication(true)
					.username(
							prefs.getString(Activator.PLUGIN_ID,
									PreferenceConstants.Username, "username",
									null))
					.password(
							SecureStorage.retrieveSecureStorage(
									Activator.PLUGIN_ID,
									PreferenceConstants.Password));
		}else{
			ologClientBuilder.withHTTPAuthentication(false);
		}
		log.info("Creating Olog client : " + url);
		try {
			OlogClientManager.registerDefaultClient(ologClientBuilder.create());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static AbstractUIPlugin getDefault() {
		return plugin;
	}

}
