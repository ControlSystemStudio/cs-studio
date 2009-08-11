package org.csstudio.opibuilder;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.util.RhinoScriptService;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OPIBuilderPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.opibuilder";
	
	/**
	 * The ID of the widget extension point.
	 */
	public static final String EXTPOINT_WIDGET = PLUGIN_ID + ".widget"; //$NON-NLS-1$

	// The shared instance
	private static OPIBuilderPlugin plugin;
	
	private static Logger log;
	
	/**
	 * The constructor
	 */
	public OPIBuilderPlugin() {
		plugin = this;
	}



	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static OPIBuilderPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		RhinoScriptService.getInstance();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		RhinoScriptService.getInstance().exit();
		
	}


}
