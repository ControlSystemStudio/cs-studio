package org.csstudio.alarm.dal2jms;

import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.CSSPlatformPlugin;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.alarm.dal2jms";

	// The shared instance
	private static Activator plugin;

    private IAlarmService _alarmService;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public String getPluginId() {
	    return PLUGIN_ID;
	}

	@Override
	public void doStart(BundleContext context) throws Exception {

		_alarmService = getService(context, IAlarmService.class);

		plugin = this;
	}

	@Override
	public void doStop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
    /**
     * @return the alarm service or null
     */
    public IAlarmService getAlarmService() {
        return _alarmService;
    }


}
