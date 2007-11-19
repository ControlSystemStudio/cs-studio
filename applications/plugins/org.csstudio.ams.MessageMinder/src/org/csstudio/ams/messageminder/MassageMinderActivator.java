package org.csstudio.ams.messageminder;

import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class MassageMinderActivator extends AbstractCssPlugin {

	/**
	 *  The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.csstudio.ams.MessageMinder";

	/**
	 *  The shared instance.
	 */
	private static MassageMinderActivator _plugin;
	
	/**
	 * The constructor.
	 */
	public MassageMinderActivator() {
	    System.out.println("Message Minder Activator.Activator()");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doStart(BundleContext context) throws Exception {
		_plugin = this;
         for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
             s.run();
         }
         CentralLogger.getInstance().info(this, "MessageMinder started...");
	}

    /**
     * {@inheritDoc}
     */
	public void doStop(BundleContext context) throws Exception {
		_plugin = null;
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static MassageMinderActivator getDefault() {
		return _plugin;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginId() {
        // TODO Auto-generated method stub
        return PLUGIN_ID;
    }

}
