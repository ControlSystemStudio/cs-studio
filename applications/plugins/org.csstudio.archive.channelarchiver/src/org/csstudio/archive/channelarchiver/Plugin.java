package org.csstudio.archive.channelarchiver;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/** Plugin lifecycle handler.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Plugin extends org.eclipse.core.runtime.Plugin
{
    final private static String ID = "org.csstudio.archive.channelarchiver";
    
    /** Shared instance */
    private static Plugin plugin = null;
    
    /** Constructor */
    public Plugin()
    {
        plugin = this;
    }
    
    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
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
        log(IStatus.ERROR, message, ex);
    }
    
    /** Add a message to the log.
     *  @param type
     *  @param message
     *  @param ex Exception or <code>null</code>
     */
    private static void log(int type, String message, Throwable ex)
    {
        if (plugin == null)
        {
            System.out.println(message);
            if (ex != null)
                ex.printStackTrace();
            return;
        }
        plugin.getLog().log(new Status(type, ID, IStatus.OK, message, ex));
    }
}
