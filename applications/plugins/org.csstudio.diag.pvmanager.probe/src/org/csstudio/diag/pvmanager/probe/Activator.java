package org.csstudio.diag.pvmanager.probe;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.pvmanager.probe";

	// The shared instance
	private static Activator plugin;
    
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}
	
	@Override
    public void start(BundleContext context) throws Exception
    {
	    super.start(context);
		setPlugin(this);	
    }

	@Override
    public void stop(BundleContext context) throws Exception
    {
		setPlugin(this);	
	    super.stop(context);
    }

	/** Static setter to avoid FindBugs warning */
	private static void setPlugin(final Activator the_plugin)
	{
		plugin = the_plugin;
	}

	/** @eturn The shared instance. */
	public static Activator getDefault()
    {
		return plugin;
	}
}
