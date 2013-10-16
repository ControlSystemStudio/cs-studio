package org.csstudio.utility.scan.ui;

import java.util.logging.Logger;

import org.csstudio.utility.scan.ScanServerClientFromPreferences;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.msu.frib.scanserver.api.ScanServer;
import edu.msu.frib.scanserver.api.ScanServerClient;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.scan.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private static IPropertyChangeListener preferenceListener;
	
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
		preferenceListener = new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				log.info("ScanServer client property changed = creating new client");
				
				// Fetch the instantiated extension and reload the configuration
				ScanServerClient client = ScanServer.getClient();
				if (client instanceof ScanServerClientFromPreferences) {
					((ScanServerClientFromPreferences) client).reloadConfiguration();
				}
			}
		};
		org.csstudio.utility.scan.Activator.getDefault()
				.getPreferenceStore()
				.addPropertyChangeListener(preferenceListener);
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
		org.csstudio.utility.scan.Activator.getDefault()
				.getPreferenceStore()
				.removePropertyChangeListener(preferenceListener);
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
