package org.csstudio.diag.epics.pvtree;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/** Plugin class for EPICS PV Tree.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssUiPlugin
{
    public static final String ID = "org.csstudio.diag.epics.pvtree"; //$NON-NLS-1$

    // The shared instance.
    private static Plugin plugin;

    /** The constructor. */
    public Plugin()
    {
        plugin = this;
    }
    
    /** @see AbstractCssUiPlugin */
    @Override
    public String getPluginId()
    {   return ID;  }

    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStart(BundleContext context) throws Exception
    {
        // NOP
    }

    /** @see AbstractCssUiPlugin */
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

    /** Add info message to the plugin log. */
    public static void logInfo(String message)
    {
        log(IStatus.INFO, message, null);
    }
    
    /** Add error message to the plugin log. */
    public static void logError(String message)
    {
        log(IStatus.ERROR, message, null);
    }
  
    /** Add an exception to the plugin log. */
    public static void logException(String message, Throwable ex)
    {
        ex.printStackTrace();
        log(IStatus.ERROR, message, ex);
    }
  
    /** Add a message to the log.
     *  @param type
     *  @param message
     *  @param e Exception or <code>null</code>
     */
    private static void log(int type, String message, Throwable ex)
    {
        if (plugin == null)
            System.out.println(message);
        else
            plugin.getLog().log(new Status(type, ID, IStatus.OK, message, ex));
        if (ex != null)
            ex.printStackTrace();
    }
}
