package org.csstudio.utility.epicsDataBaseCompare;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractCssUiPlugin{

    /**
     * The plug-in ID.
     */ 
    public static final String PLUGIN_ID = "org.csstudio.utility.epicsDataBaseCompare";

    /** 
     *  The shared instance.
     */
    private static Activator _plugin;

    @Override
    protected void doStart(BundleContext context) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void doStop(BundleContext context) throws Exception {
        // TODO Auto-generated method stub
        
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
        return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.ui.AbstractCssUiPlugin#getPluginId()
     */
    /**
     * @return The PlugIn Id
     */
    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

}
