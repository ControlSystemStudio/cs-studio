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

    public static final String ID = "org.csstudio.saverestore.masar";

    public static final String PREF_SERVICES = "services";

    private static Activator INSTANCE;

    /**
     * The default instance.
     *
     *
     * @return the instance
     */
    public static Activator getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        INSTANCE = this;
    }

    /**
     * Reads the {@link #PREF_SERVICES} preference and splits it by comma. These are the names of available MASAR
     * services and are used as branch names returned by the {@link MasarDataProvider}.
     *
     * @return the array of available MASAR services or a zero-length array if none defined
     */
    public String[] getServices() {
        String str = getPreferenceStore().getString(PREF_SERVICES);
        if (str == null) {
            return new String[0];
        } else {
            return str.split("\\,");
        }
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
}
