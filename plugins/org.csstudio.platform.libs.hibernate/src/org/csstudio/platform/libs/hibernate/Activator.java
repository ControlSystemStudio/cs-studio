package org.csstudio.platform.libs.hibernate;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

    /**
     *  The plug-in ID.
     */
    public static final String PLUGIN_ID = "org.csstudio.platform.libs.hibernate";

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
    public void start(BundleContext context) throws Exception {
    	super.start(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext context) throws Exception {
    	super.stop(context);
    }
}
