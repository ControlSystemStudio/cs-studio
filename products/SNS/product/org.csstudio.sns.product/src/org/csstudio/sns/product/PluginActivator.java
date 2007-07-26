package org.csstudio.sns.product;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plugin lifecycle.
 *  @author Kay Kasemir
 */
public class PluginActivator extends AbstractUIPlugin
{
    final public static String ID = "org.csstudio.sns.product"; //$NON-NLS-1$
    private static PluginActivator plugin;
    
    public PluginActivator()
    {
        plugin = this;
    }

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
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
