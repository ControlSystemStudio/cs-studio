package org.csstudio.utility.pv.epics;

import org.apache.log4j.Logger;
import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.osgi.framework.BundleContext;

/** Plugin-activator for the EPICS PV.
 *  @author Kay Kasemir
 */
public class Activator extends AbstractCssPlugin
{
	// The plug-in ID
	public static final String ID = "org.csstudio.utility.pv.epics"; //$NON-NLS-1$

    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

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
            getLogger().debug(message);
        }
        catch (Throwable e)
        {
            getLogger().error("Cannot load EPICS_V3_PV", e);
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

	/** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
