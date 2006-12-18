package org.csstudio.display.pvtable;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 * 
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssUiPlugin
{
    public static final String ID = "org.csstudio.display.pvtable"; //$NON-NLS-1$
    // The shared instance.
    private static Plugin plugin;
    
    /** The constructor. */
    public Plugin()
    {
        if (plugin != null)
            throw new IllegalStateException("Plugin is singleton"); //$NON-NLS-1$
        plugin = this;
    }

    /** @return Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }
    
    /** @see AbstractCssUiPlugin */
    @Override
    public String getPluginId()
    {   return ID;  }
    
    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStart(BundleContext context) throws Exception
    {}

    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
        plugin = null;
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
        getLog().log(new Status(type, ID, IStatus.OK, message, e));
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
