package org.csstudio.utility.treemodel;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.utility.treemodel";

    private static Activator INSTANCE;

    private Bundle _bundle;

    /**
     * Don't instantiate.
     * Called by framework.
     */
    public Activator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(@CheckForNull final BundleContext context) throws Exception {
	    _bundle = context.getBundle();
	    // EMPTY
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(@CheckForNull final BundleContext context) throws Exception {
	    // EMPTY
	}

	/**
	 * The plugin id.
	 * @return the id.
	 */
    @Nonnull
    public String getPluginId() {
        return PLUGIN_ID;
    }

    /**
     * Returns the singleton instance.
     *
     * @return the instance
     */
    @Nonnull
    public static Activator getDefault() {
        return INSTANCE;
    }

    public Bundle getBundle() {
        return _bundle;
    }
}
