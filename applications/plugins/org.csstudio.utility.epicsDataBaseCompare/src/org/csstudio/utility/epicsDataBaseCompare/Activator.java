package org.csstudio.utility.epicsDataBaseCompare;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * @author hrickens
 * @since 09.09.2011
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The plug-in ID.
     */
    public static final String PLUGIN_ID = "org.csstudio.utility.epicsDataBaseCompare";

    /**
     *  The shared instance.
     */
    private static Activator _PLUGIN;

    @Override
    public void start( @Nullable final BundleContext context) throws Exception {
    	super.start(context);
    }

    @Override
    public void stop(@Nullable final BundleContext context) throws Exception {
    	super.stop(context);
    }

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    @Nonnull
    public static Activator getDefault() {
        return _PLUGIN;
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
        return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
