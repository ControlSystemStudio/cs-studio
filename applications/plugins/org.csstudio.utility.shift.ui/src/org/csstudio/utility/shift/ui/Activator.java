package org.csstudio.utility.shift.ui;

import gov.bnl.shiftClient.ShiftClientCreator;
import gov.bnl.shiftClient.ShiftClientImpl.ShiftClientBuilder;

import java.util.logging.Logger;
import org.csstudio.utility.shift.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.shift.ui"; //$NON-NLS-1$

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
				final IPreferencesService prefs = Platform.getPreferencesService();
				ShiftClientBuilder shiftClientBuilder;
				String url = prefs.getString(org.csstudio.utility.shift.Activator.PLUGIN_ID,
						PreferenceConstants.Shift_URL, "http://localhost:8080/Shift/resources", null);
				shiftClientBuilder = ShiftClientBuilder.serviceURL(url);			
				shiftClientBuilder.withHTTPAuthentication(false);
				log.info("Creating Shift client : " + url);
				try {
					ShiftClientCreator.setClient(shiftClientBuilder.create());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		org.csstudio.utility.shift.Activator.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceListner);
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
		org.csstudio.utility.shift.Activator.getDefault().getPreferenceStore().removePropertyChangeListener(preferenceListner);
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
