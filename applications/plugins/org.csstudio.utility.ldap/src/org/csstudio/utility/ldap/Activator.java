package org.csstudio.utility.ldap;
//
import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {
//public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.ldap";

	//	 The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#doStart(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void doStart(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#doStop(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void doStop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#getPluginId()
	 */
	@Override
	public String getPluginId() {
		// TODO Auto-generated method stub
		return PLUGIN_ID;
	}

	public static Activator getDefault() {
		return plugin;
	}

}
