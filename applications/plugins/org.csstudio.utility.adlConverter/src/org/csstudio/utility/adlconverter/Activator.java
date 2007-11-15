package org.csstudio.utility.adlconverter;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractCssUiPlugin{

	/**
	 *  The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.csstudio.utility.adlConverter"; //$NON-NLS-1$

	/** 
     *  The shared instance.
     */
    private static Activator _plugin;
    
    /**
     * The constructor.
     */
    public Activator() {
        _plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    /**
     * @param context The Context.
     * @exception Exception The Thrown excepton
     */
    public void doStart(final BundleContext context) throws Exception {
//      super.start(context);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    /**
     * @param context The Context.
     * @exception Exception The Thrown excepton.
     */
    public final void doStop(final BundleContext context) throws Exception {
        _plugin = null;
//      super.stop(context);
    }

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return _plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(final String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * @return The PlugIn Id
     */
    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }
}
