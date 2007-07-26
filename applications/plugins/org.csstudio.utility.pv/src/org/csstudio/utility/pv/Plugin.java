package org.csstudio.utility.pv;

import org.csstudio.platform.AbstractCssPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/** Plugin-activator for the PV.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssPlugin
{
	public final static String ID = "org.csstudio.utility.pv"; //$NON-NLS-1$
    
    /** The singleton instance */
	private static Plugin plugin;

    /** Constructor */
    public Plugin()
    {
        plugin = this;
    }
    
	@Override
    public String getPluginId()
	{   return ID; }

    /** @see AbstractCssPlugin */
    @SuppressWarnings("nls")
    @Override
    protected void doStart(BundleContext context) throws Exception
    { /* NOP */ }

    /** @see AbstractCssPlugin */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
        plugin = null;
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
