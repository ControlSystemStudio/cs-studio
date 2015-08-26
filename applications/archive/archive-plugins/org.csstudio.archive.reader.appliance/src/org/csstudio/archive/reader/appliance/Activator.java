package org.csstudio.archive.reader.appliance;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.archive.reader.appliance"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private Boolean useStatistics;
    private Boolean useNewOptimizedOperator;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
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
     * Returns the shared instance.
     *
     * @return The shared instance.
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * @return true if statistics type should be used for optimised data or false if normal type
     *             should be used
     */
    public boolean isUseStatistics() {
        if (useStatistics == null) {
            final IPreferencesService prefs = Platform.getPreferencesService();
            if (prefs == null) {
                useStatistics = false;
            } else {
                useStatistics = prefs.getBoolean(Activator.PLUGIN_ID, "useStatisticsForOptimizedData", false, null);
            }
        }
        return useStatistics;
    }

    /**
     * @return true if the new optimised operator should be used or false if not. If it is known in advance that the
     *              appliance installation does not yet support the optimised post processing this property can be
     *              set to false in order to reduce the number of queries
     */
    public boolean isUseNewOptimizedOperator() {
        if (useNewOptimizedOperator == null) {
            final IPreferencesService prefs = Platform.getPreferencesService();
            if (prefs == null) {
                useNewOptimizedOperator = true;
            } else {
                useNewOptimizedOperator = prefs.getBoolean(Activator.PLUGIN_ID, "useNewOptimizedOperator", true, null);
            }
        }
        return useNewOptimizedOperator;
    }
}