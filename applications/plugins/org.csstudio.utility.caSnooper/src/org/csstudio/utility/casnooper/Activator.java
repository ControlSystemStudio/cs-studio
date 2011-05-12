package org.csstudio.utility.casnooper;

import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.caSnooper";

	// The shared instance
	private static Activator plugin;
	
	private GenericServiceTracker<ISessionService> _genericServiceTracker;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	@Override
	protected void doStart(BundleContext context) throws Exception {
	
		_genericServiceTracker = new GenericServiceTracker<ISessionService>(
				context, ISessionService.class);
		_genericServiceTracker.open();
		//create a defaultscope for the plugin. Otherwise the preference initialzier
		//will be called AFTER StartupService and the LoginCallbackhandler
		//has no preference values.
//		IEclipsePreferences prefs = new DefaultScope().getNode(
//				Activator.getDefault().getPluginId());
		
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		CentralLogger.getInstance().info(this, "CaSnooper stopped"); 
		
	}

	@Override
	public String getPluginId() {
		// TODO Auto-generated method stub
		return PLUGIN_ID;
	}

	/**
	 * Get the preferences from the XML file.
	 * TODO: implement a preference in the core that one
	 * can use the common eclipse preferences
	 *  
	 * @return Preferences for this plugin
	 */
//	public Preferences getPreferences()
//	{
//		//TODO put this in the core preferences!
//		boolean readPreferencesFromXML = true;
//		
//		Preferences prefs = getPluginPreferences();
//		
//		if(readPreferencesFromXML) {
//			CssPreferences result = new CssPreferences(PLUGIN_ID);
//			
//			String[] prefNames = prefs.defaultPropertyNames();
//			
//			for (int i = 0; i < prefNames.length; i++)
//				result.setValue(prefNames[i], prefs.getString(prefNames[i]));
//			
//			return result;
//		} else {
//			return prefs;
//		}
//	}
	
    /** Add informational message to the plugin log. */
    public static void logInfo(String message)
    {
        getDefault().log(IStatus.INFO, message, null);
    }

    /** Add error message to the plugin log. */
    public static void logError(String message)
    {
        getDefault().log(IStatus.ERROR, message, null);
    }

    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     * @param type
     * @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type, PLUGIN_ID, IStatus.OK, message, e));
    }

	public void addSessionServiceListener(
			IGenericServiceListener<ISessionService> sessionServiceListener) {
		_genericServiceTracker.addServiceListener(sessionServiceListener);
	}
}
