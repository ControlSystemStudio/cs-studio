package org.csstudio.platform.libs.epics;

import org.csstudio.platform.libs.epics.preferences.PreferenceConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/** The main plugin class to be used in the desktop.
 *  @author Original author unknown
 *  @author Kay Kasemir
  */
public class EpicsPlugin extends AbstractUIPlugin
{
	public static final String ID = "org.csstudio.platform.libs.epics";
    //The shared instance.
	private static EpicsPlugin plugin;
	
	/** The constructor. */
	public EpicsPlugin()
    {
		plugin = this;
	}

    /** Update the CAJ settings with the data from the
     *  preference page.
     *  <p>
     *  Unfortunately this only takes effect after a restart,
     *  the current setup seems to remain unaffected.
     */
	public void installPreferences()
    {
        try
        {
            System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", getDefault().getPluginPreferences().getString(PreferenceConstants.constants[0]));
    		boolean yes_no = getDefault().getPluginPreferences().getBoolean(PreferenceConstants.constants[1]);
            System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
                            (yes_no ? "YES" : "NO"));
    		System.setProperty("com.cosylab.epics.caj.CAJContext.connection_timeout", getDefault().getPluginPreferences().getString(PreferenceConstants.constants[2]));
    		System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period", getDefault().getPluginPreferences().getString(PreferenceConstants.constants[3]));
    		System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port", getDefault().getPluginPreferences().getString(PreferenceConstants.constants[4]));
    		System.setProperty("com.cosylab.epics.caj.CAJContext.server_port", getDefault().getPluginPreferences().getString(PreferenceConstants.constants[5]));
    		System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", getDefault().getPluginPreferences().getString(PreferenceConstants.constants[6]));
        }
        catch (Exception e)
        {
            logException("Cannot set preferences", e);
        }
	}
	
    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     *  @param type
     *  @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type,
                                getClass().getName(),
                                IStatus.OK,
                                message,
                                e));
    }
    	
	/** This method is called upon plug-in activation */
	public void start(BundleContext context) throws Exception 
    {
		super.start(context);
		installPreferences();
	}

	/** This method is called when the plug-in is stopped */
	public void stop(BundleContext context) throws Exception
    {
		super.stop(context);
		plugin = null;
	}

	/** @return Returns the shared instance. */
	public static EpicsPlugin getDefault()
    {
		return plugin;
	}
}
