package org.csstudio.display.pvtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 * 
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractUIPlugin
{
    public static final String ID = "org.csstudio.pvtable";
    // The shared instance.
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

    /** Add informational message to the plugin log. */
    public static void logInfo(String message)
    {
        getDefault().log(IStatus.INFO, message, null);
    }

    /** Add error message to the plugin log. */
    public static void logError(String message)
    {
        getDefault().log(IStatus.ERROR, message, null);
    }

    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     * @param type
     * @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type,
                                getClass().getName(),
                                IStatus.OK,
                                message,
                                e));
    }
    
    /** This method is called upon plug-in activation */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
    }

    /** This method is called when the plug-in is stopped */
    public void stop(BundleContext context) throws Exception
    {
        super.stop(context);
        plugin = null;
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
