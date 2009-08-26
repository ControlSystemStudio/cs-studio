package org.csstudio.config.ioconfig.model;

import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractCssPlugin{

	/**
	 *  The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.csstudio.config.ioconfig.model";

	// The shared instance
    private static Activator plugin;
    
    /**
     * The constructor
     */
    public Activator() {
        plugin = this;
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
     * {@inheritDoc}
     */
    @Override
    protected void doStart(BundleContext context) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop(BundleContext context) throws Exception {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

}
