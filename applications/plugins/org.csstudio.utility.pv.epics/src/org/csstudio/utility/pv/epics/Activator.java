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

    /** Log an exception */
    static void logException(final String message, final Throwable ex)
    {
        if (plugin != null)
            plugin.getLog().log(new Status(IStatus.ERROR, ID, IStatus.OK,
                                           message, ex));
        else
            System.err.println("Error: " + message); //$NON-NLS-1$
        if (ex != null)
            ex.printStackTrace();
    }

    /** Log an error */
    public static void logError(final String message)
    {
        logException(message, null);
    }
}
