package de.desy.language.editor.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * Core Plug-in of the language editor support.
 *
 * The activator class controls the plug-in life cycle.
 */
public final class Activator extends Plugin {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "de.desy.language.editor.core"; //$NON-NLS-1$

    /**
     * The constructor
     */
    public Activator() {
        /*- Nothing additional to do */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        /*- Nothing additional to do */
        super.start(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        /*- Nothing additional to do */
        super.stop(context);
    }
}
