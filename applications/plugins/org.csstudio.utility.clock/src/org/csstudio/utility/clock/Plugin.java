package org.csstudio.utility.clock;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractUIPlugin
{
    // The shared instance.
    private static Plugin plugin;

    /** Constructor. */
    public Plugin()
    {
        plugin = this;
    }

    /** This method is called when the plug-in is stopped */
    public void stop(BundleContext context) throws Exception
    {
        super.stop(context);
        plugin = null;
    }

    /** @return Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }
}
