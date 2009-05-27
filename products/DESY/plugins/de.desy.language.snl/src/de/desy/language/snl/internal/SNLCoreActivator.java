package de.desy.language.snl.internal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SNLCoreActivator extends Plugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "de.desy.language.snl";
	
	/**
	 * The constructor
	 */
	public SNLCoreActivator() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
	}
	
}