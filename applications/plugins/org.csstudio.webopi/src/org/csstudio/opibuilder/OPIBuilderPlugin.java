package org.csstudio.opibuilder;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OPIBuilderPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.webopi"; //$NON-NLS-1$

	// The shared instance
	private static OPIBuilderPlugin plugin;
	
	
	/** File extension used for OPI files */
	public static final String OPI_FILE_EXTENSION = "opi"; //$NON-NLS-1$
	
	/**
	 * The ID of the widget extension point.
	 */
	public static final String EXTPOINT_WIDGET = "org.csstudio.opibuilder.widget"; //$NON-NLS-1$


	/**
	 * The ID of the widget extension point.
	 */
	public static final String EXTPOINT_FEEDBACK_FACTORY = PLUGIN_ID + ".graphicalFeedbackFactory"; //$NON-NLS-1$

	
	final private static Logger logger = Logger.getLogger(PLUGIN_ID);

	/**
	 * The constructor
	 */
	public OPIBuilderPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static OPIBuilderPlugin getDefault() {
		return plugin;
	}
	
	/** @return Logger for plugin ID */
	public static Logger getLogger()
	{
	    return logger;
	}

}
