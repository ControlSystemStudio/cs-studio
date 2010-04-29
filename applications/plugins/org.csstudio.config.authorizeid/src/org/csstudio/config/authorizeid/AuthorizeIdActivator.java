package org.csstudio.config.authorizeid;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.csstudio.utility.ldap.service.ILdapService;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AuthorizeIdActivator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.config.authorizeid"; //$NON-NLS-1$


    /**
     * The alarm service
     */
    private ILdapService _ldapService;

    private static AuthorizeIdActivator INSTANCE;

    /**
     * Don't instantiate.
     * Called by framework.
     */
    public AuthorizeIdActivator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

	@Override
	protected void doStart(final BundleContext context) throws Exception {
		_ldapService = getService(context, ILdapService.class);
	}

	@Override
	protected void doStop(final BundleContext context) throws Exception {
	    // EMPTY
	}

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

    /**
     * @return the LDAP service or null
     */
    public ILdapService getLdapService() {
        return _ldapService;
    }

    /**
     * @return the shared instance
     */
    public static AuthorizeIdActivator getDefault() {
        return INSTANCE;
    }
}
