package org.csstudio.diag.postanalyser;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.postanalyser";

//	 The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}	
	/* (non-Javadoc)
	 * @see org.csstudio.platform.ui.AbstractCssUiPlugin#doStart(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void doStart(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.ui.AbstractCssUiPlugin#doStop(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void doStop(BundleContext context) throws Exception {
		plugin = null;
	}
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	/* (non-Javadoc)
	 * @see org.csstudio.platform.ui.AbstractCssUiPlugin#getPluginId()
	 */
	@Override
	public String getPluginId() {
		// TODO Auto-generated method stub
		return PLUGIN_ID;
	}


}
