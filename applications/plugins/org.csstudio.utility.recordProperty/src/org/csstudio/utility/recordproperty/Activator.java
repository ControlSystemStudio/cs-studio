package org.csstudio.utility.recordproperty;

import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceTracker;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.recordProperty";

	// The shared instance
	private static Activator INSTANCE;

    private LdapServiceTracker _ldapServiceTracker;

	/**
	 * The constructor
	 */
	public Activator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception {
    	super.start(context);
        _ldapServiceTracker = new LdapServiceTracker(context);
        _ldapServiceTracker.open();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
    	super.stop(context);
        _ldapServiceTracker.close();
    }

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return INSTANCE;
	}


    /**
     * @return the LDAP service
     */
    public ILdapService getLdapService() {
        return (ILdapService) _ldapServiceTracker.getService();
    }

}
