package org.csstudio.utility.olog.ui;

import java.util.logging.Logger;

import org.csstudio.auth.security.SecureStorage;
import org.csstudio.utility.olog.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.msu.nscl.olog.api.Olog;
import edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.olog.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	private IPropertyChangeListener preferenceListner;
	private static Logger log = Logger.getLogger(PLUGIN_ID);

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
		preferenceListner = new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				final IPreferencesService prefs = Platform
						.getPreferencesService();
				OlogClientBuilder ologClientBuilder;
				String url = prefs.getString(
						org.csstudio.utility.olog.Activator.PLUGIN_ID,
						PreferenceConstants.Olog_URL,
						"http://localhost:8080/Olog/resources", null);
				String jcr_url = prefs.getString(
						org.csstudio.utility.olog.Activator.PLUGIN_ID,
						PreferenceConstants.Olog_jcr_URL,
						"http://localhost:8080/Olog/repository/olog", null);
				ologClientBuilder = OlogClientBuilder.serviceURL(url).jcrURI(
						jcr_url);
				if (prefs.getBoolean(
						org.csstudio.utility.olog.Activator.PLUGIN_ID,
						PreferenceConstants.Use_authentication, false, null)) {
					ologClientBuilder
							.withHTTPAuthentication(true)
							.username(
									prefs.getString(
											org.csstudio.utility.olog.Activator.PLUGIN_ID,
											PreferenceConstants.Username,
											"username", null))
							.password(
									SecureStorage
											.retrieveSecureStorage(
													org.csstudio.utility.olog.Activator.PLUGIN_ID,
													PreferenceConstants.Password));
				} else {
					ologClientBuilder.withHTTPAuthentication(false);
				}
				log.info("Creating Olog client : " + url);
				try {
					// OlogClientManager.registerDefaultClient(ologClientBuilder
					// .create());
					Olog.setClient(ologClientBuilder.create());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		org.csstudio.utility.olog.Activator.getDefault().getPreferenceStore()
				.addPropertyChangeListener(preferenceListner);
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
		org.csstudio.utility.olog.Activator.getDefault().getPreferenceStore()
				.removePropertyChangeListener(preferenceListner);
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
