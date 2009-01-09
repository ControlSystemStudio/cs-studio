package org.csstudio.dct;

import org.csstudio.dct.model.persistence.IPersistenceService;
import org.csstudio.dct.model.persistence.internal.PersistenceService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DctActivator extends AbstractUIPlugin {
	private IPersistenceService persistenceService;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.dct2";

	// The shared instance
	private static DctActivator plugin;
	
	/**
	 * The constructor
	 */
	public DctActivator() {
		persistenceService = new PersistenceService();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
	public static DctActivator getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the persistence service.
	 * 
	 * @return the persistence service
	 */
	public IPersistenceService getPersistenceService() {
		return persistenceService;
	}

}
