package org.csstudio.utility.pv.epics;

import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/** Plugin-activator for the EPICS PV.
 *  @author Kay Kasemir
 */
public class Activator extends AbstractCssPlugin
{
	// The plug-in ID
	public static final String ID = "org.csstudio.utility.pv.epics"; //$NON-NLS-1$

    /** The singleton instance */
	private static Activator plugin;
	
	/** Constructor */
	public Activator()
    {	plugin = this;	}

    @Override
    public String getPluginId()
    {   return ID; }

    /** @see AbstractCssPlugin */
    @SuppressWarnings("nls")
    @Override
    protected void doStart(BundleContext context) throws Exception
    {
        try
        {
            PVContext.use_pure_java = EpicsPlugin.getDefault().usePureJava();
            final String message = PVContext.use_pure_java ?
                                "Using pure java CAJ" : "Using JCA with JNI";
            getLog().log(new Status(IStatus.INFO, ID, IStatus.OK, message, null));
        }
        catch (Throwable e)
        {
            logException("Cannot load EPICS_V3_PV", e);
        }
    }

    /** @see AbstractCssPlugin */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
        plugin = null;
    }

	/** @return the shared instance */
	public static Activator getDefault()
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
