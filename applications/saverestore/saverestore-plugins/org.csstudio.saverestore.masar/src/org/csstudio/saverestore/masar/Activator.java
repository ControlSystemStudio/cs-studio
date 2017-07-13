/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.masar;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * <code>Activator</code> provides access to preferences of the saverestore.git plugin.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Activator extends AbstractUIPlugin {

    /** The plugin ID */
    public static final String ID = "org.csstudio.saverestore.masar";

    /** The preference name for the services list */
    public static final String PREF_SERVICES = "services";
    /** The preference name for the timeout for accessing masar service */
    public static final String PREF_TIMEOUT = "timeout";
    /** The preference name for the connection timeout */
    public static final String PREF_CONNECTION_TIMEOUT = "connectionTimeout";

    private static Activator defaultInstance;

    /**
     * The default instance.
     *
     *
     * @return the instance
     */
    public static Activator getInstance() {
        return defaultInstance;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        defaultInstance = this;
    }

    /**
     * Reads the {@link #PREF_SERVICES} preference and splits it by comma. These are the names of available MASAR
     * services and are used as branch names returned by the {@link MasarDataProvider}.
     *
     * @return the array of available MASAR services or a zero-length array if none defined
     */
    public String[] getServices() {
        String str = getPreferenceStore().getString(PREF_SERVICES);
        return str == null ? new String[0] : str.split("\\,");
    }

    /**
     * Set the new list of MASAR services. They will be stored into preferences under the name {@link #PREF_SERVICES}.
     *
     * @param services the list of services
     */
    public void setServices(String[] services) {
        String value = "";
        if (services != null && services.length > 0) {
            StringBuilder sb = new StringBuilder(services.length*20);
            sb.append(services[0]);
            for (int i = 1; i < services.length; i++) {
                sb.append(',').append(services[i]);
            }
            value = sb.toString();
        }
        getPreferenceStore().setValue(PREF_SERVICES, value);
    }

    /**
     * Returns the timeout for making requests to masar service in seconds.
     *
     * @return the timeout in seconds
     */
    public int getTimeout() {
        int timeout = getPreferenceStore().getInt(PREF_TIMEOUT);
        return timeout < 0 ? 0 : timeout;
    }

    /**
     * Returns the connection timeout for connecting to masar service in seconds.
     *
     * @return the timeout in seconds
     */
    public int getConnectionTimeout() {
        int timeout = getPreferenceStore().getInt(PREF_CONNECTION_TIMEOUT);
        return timeout <= 0 ? 3 : timeout;
    }

    /**
     * Returns the true if MASAR service is allowed to update configurations.
     *
     * @return if update enabled
     */
    public boolean isEnableUpdate() {
        boolean enableUpdate = getPreferenceStore().getBoolean("enable.update");
        return enableUpdate;
    }
}
