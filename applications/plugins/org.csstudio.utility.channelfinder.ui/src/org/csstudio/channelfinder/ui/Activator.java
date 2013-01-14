package org.csstudio.channelfinder.ui;

import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;

import java.util.logging.Logger;

import org.csstudio.utility.channelfinder.ChannelFinderClientFromPreferences;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.channelfinder.ui";

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
				log.info("ChannelFinder clinet property Changed = creating new client");
				
				// Fetch the instanciated extension and reload the configuration
				ChannelFinderClient client = ChannelFinder.getClient();
				if (client instanceof ChannelFinderClientFromPreferences) {
					((ChannelFinderClientFromPreferences) client).reloadConfiguration();
				}
			}
		};
		org.csstudio.utility.channelfinder.Activator.getDefault()
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
		org.csstudio.utility.channelfinder.Activator.getDefault()
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
