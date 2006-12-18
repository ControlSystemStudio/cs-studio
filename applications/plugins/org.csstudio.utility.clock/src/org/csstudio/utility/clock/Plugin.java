package org.csstudio.utility.clock;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssUiPlugin
{
    public final static String ID = "org.csstudio.utility.clock"; //$NON-NLS-1$
    // The shared instance.
    private static Plugin plugin;

    /** Constructor. */
    public Plugin()
    {
        plugin = this;
    }

    /** @see org.csstudio.platform.ui.AbstractCssUiPlugin#getPluginId() */
    @Override
    public String getPluginId()
    {   return ID;   }

    /** @see org.csstudio.platform.ui.AbstractCssUiPlugin#getPluginId() */
    @Override
    protected void doStart(BundleContext context) throws Exception
    {}

    /** @see org.csstudio.platform.ui.AbstractCssUiPlugin#getPluginId() */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
        plugin = null;
    }

    /** @return Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }
}
