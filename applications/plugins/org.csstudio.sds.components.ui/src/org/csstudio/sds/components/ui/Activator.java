package org.csstudio.sds.components.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Sven Wende
 */
public final class Activator extends AbstractUIPlugin {

	/**
	 * The plug-in ID.
	 * 
	 */
	public static final String PLUGIN_ID = "org.csstudio.sds.components.ui";

	/**
	 * The shared instance.
	 */
	private static Activator _instance;

	/**
	 * The constructor.
	 */
	public Activator() {
		_instance = this;
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
		_instance = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance.
	 */
	public static Activator getDefault() {
		return _instance;
	}

}
