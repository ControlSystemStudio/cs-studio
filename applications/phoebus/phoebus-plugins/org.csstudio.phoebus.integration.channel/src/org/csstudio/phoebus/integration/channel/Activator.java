package org.csstudio.phoebus.integration.channel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private static BundleContext context;

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.phoebus.integration.channel";

    static BundleContext getContext() {
        return context;
    }

    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
    }

    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }

}
