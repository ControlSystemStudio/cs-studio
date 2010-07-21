package org.csstudio.testsuite;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.testsuite";

    /**
     *  The shared instance
     */
    private static Activator INSTANCE;

	private static BundleContext CONTEXT;

	/**
	 * The constructor
	 */
	public Activator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void start(final BundleContext context) throws Exception {
		super.start(context);
		CONTEXT = context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void stop(final BundleContext context) throws Exception {
		super.stop(context);
	}


	public static Bundle[] getBundles() {
		return CONTEXT.getBundles();
	}

}
