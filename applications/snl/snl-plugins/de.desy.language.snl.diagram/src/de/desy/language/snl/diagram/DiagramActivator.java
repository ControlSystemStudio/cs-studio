package de.desy.language.snl.diagram;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DiagramActivator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "de.desy.language.snl.diagram";

    // The shared instance
    private static DiagramActivator plugin;

    /**
     * The constructor
     */
    public DiagramActivator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static DiagramActivator getDefault() {
        return plugin;
    }

}
