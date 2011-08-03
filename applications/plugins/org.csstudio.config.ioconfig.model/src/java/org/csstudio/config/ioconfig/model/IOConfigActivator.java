package org.csstudio.config.ioconfig.model;

import javax.annotation.Nonnull;

import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class IOConfigActivator extends AbstractCssPlugin {
    
    /**
     *  The plug-in ID.
     */
    public static final String PLUGIN_ID = "org.csstudio.config.ioconfig.model";
    
    /**
     *  The shared instance
     */
    private static IOConfigActivator INSTANCE;
    
    /**
     * The constructor
     */
    public IOConfigActivator() {
        if (INSTANCE != null) { // ENSURE SINGLETON
            throw new IllegalStateException("Class " + PLUGIN_ID + " already exists.");
        }
        INSTANCE = this;
    }
    
    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    @Nonnull
    public static IOConfigActivator getDefault() {
        return INSTANCE;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart(@Nonnull final BundleContext context) throws Exception {
        // nothing to start
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop(@Nonnull final BundleContext context) throws Exception {
        // nothing to stop
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getPluginId() {
        return PLUGIN_ID;
    }
    
}
