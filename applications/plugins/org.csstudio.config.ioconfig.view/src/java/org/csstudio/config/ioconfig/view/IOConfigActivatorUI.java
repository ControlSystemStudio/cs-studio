package org.csstudio.config.ioconfig.view;

import javax.annotation.Nonnull;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class IOConfigActivatorUI extends AbstractUIPlugin {
    
    /**
     * The plug-in ID.
     */
    public static final String PLUGIN_ID = "org.csstudio.config.ioconfig.view";
    
    /**
     *  The shared instance.
     */
    private static IOConfigActivatorUI INSTANCE;
    
    /**
     * The constructor.
     */
    public IOConfigActivatorUI() {
        if (INSTANCE != null) { // ENSURE SINGLETON
            throw new IllegalStateException("Class " + PLUGIN_ID + " already exists.");
        }
        INSTANCE = this;
    }
    
    /**
     * @param context The Context.
     * @exception Exception The Thrown excepton
     */
    @Override
    public void start(@Nonnull final BundleContext context) throws Exception {
    	super.start(context);
        // nothing to start
    }
    
    /**
     * @param context The Context.
     * @exception Exception The Thrown excepton
     */
    @Override
    public final void stop(@Nonnull final BundleContext context) throws Exception {
    	super.stop(context);
        // nothing to stop
    }
    
    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    @Nonnull
    public static IOConfigActivatorUI getDefault() {
        return INSTANCE;
    }
    
    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    @Nonnull
    public static ImageDescriptor getImageDescriptor(@Nonnull final String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
