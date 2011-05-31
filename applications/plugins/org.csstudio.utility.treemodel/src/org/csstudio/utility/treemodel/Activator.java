package org.csstudio.utility.treemodel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 02.06.2010
 */
public class Activator implements BundleActivator {

    /**
     * The id of this Java plug-in (value <code>{@value}</code> as defined in MANIFEST.MF.
     */
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
	@Override
    public void start(@Nullable final BundleContext context) throws Exception {
	    if (context != null) {
	        _bundle = context.getBundle(); // for the test class
	    }
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(@Nullable final BundleContext context) throws Exception {
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
