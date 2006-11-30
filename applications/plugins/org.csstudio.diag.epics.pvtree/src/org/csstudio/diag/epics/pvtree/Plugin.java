package org.csstudio.diag.epics.pvtree;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/** Plugin class for EPICS PV Tree.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractUIPlugin
{
    // The shared instance.
    private static Plugin plugin;

    /** The constructor. */
    public Plugin()
    {
        plugin = this;
    }

    /** Called when the plug-in is stopped */
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
    
    /** Returns an image descriptor for the image file at the given plug-in
     *  relative path.
     *  @param path the path
     *  @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(
                "org.csstudio.diag.epics.pvtree", path);
    }
}
