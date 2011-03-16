package org.csstudio.display.pvtable;

import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Plugin extends AbstractUIPlugin
{
    /** The plug-in ID */
    final public static String ID = "org.csstudio.display.pvtable";

    /** File extension for config. files */
    final public static String FileExtension = "css-pvtable";

    /** Logger */
    final private static Logger logger = Logger.getLogger(ID);

    /** The shared instance */
    private static Plugin plugin;

    /** The constructor. */
    public Plugin()
    {
        if (plugin != null)
            throw new IllegalStateException("Plugin is singleton");
        plugin = this;
    }

    /** @return Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
    }


    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return logger;
    }

    /** @return Returns an image descriptor for the image file at the given plug-in
     *  relative path.
     *  @param path The path
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }
}
