package org.csstudio.dct.treemodelexporter;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TreemodelExporterActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.dct.treemodelexporter";

	// The shared instance
	private static TreemodelExporterActivator plugin;

	/**
	 * The constructor
	 */
	public TreemodelExporterActivator() {
	    // EMPTY
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TreemodelExporterActivator getDefault() {
		return plugin;
	}

}
