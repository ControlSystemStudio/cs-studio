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
    
    /** Add informational message to the plugin log. */
    public static void logInfo(String message)
    {
        plugin.getLog().log(
                     new Status(IStatus.INFO, ID, IStatus.OK, message, null));
    }
}
