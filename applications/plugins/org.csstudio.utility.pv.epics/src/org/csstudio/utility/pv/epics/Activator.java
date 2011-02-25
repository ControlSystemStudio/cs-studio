package org.csstudio.utility.pv.epics;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.osgi.framework.BundleContext;

/** Plugin-activator for the EPICS PV.
 *  @author Kay Kasemir
 */
public class Activator extends AbstractCssPlugin
{
	/** Plug-in ID registered in MANIFEST.MF */
	public static final String ID = "org.csstudio.utility.pv.epics"; //$NON-NLS-1$

	/** Logger */
	private static Logger logger = Logger.getLogger(ID);

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
            PVContext.monitor_mask = EpicsPlugin.getDefault().getMonitorMask();
        }
        catch (Throwable e)
        {
            getLogger().log(Level.SEVERE, "Cannot load EPICS_V3_PV", e);
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

	/** Log levels:
	 *  CONFIG - Config info,
	 *  FINE   - JCA start/stop,
	 *  FINER  - PV create/dispose,
	 *  FINER  - Value traffic.
	 *  @return Logger associated with the plugin */
	public static Logger getLogger()
	{
	    return logger;
	}
}
