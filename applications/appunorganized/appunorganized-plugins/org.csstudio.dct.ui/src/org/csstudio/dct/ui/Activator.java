package org.csstudio.dct.ui;


import org.csstudio.dct.DctActivator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public final class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.dct.ui";

    // The shared instance
    private static Activator plugin;

    /**
     * The preference store to access the sds core preferences.
     */
    private static IPreferenceStore _preferenceStore;

    /**
     *{@inheritDoc}
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     *{@inheritDoc}
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
    public static Activator getDefault() {
        return plugin;
    }

    public static IPreferenceStore getCorePreferenceStore() {
        if (_preferenceStore == null) {
            String qualifier = DctActivator.getDefault().getBundle().getSymbolicName();
            _preferenceStore = new ScopedPreferenceStore(new InstanceScope(), qualifier);
        }
        return _preferenceStore;

    }
}
