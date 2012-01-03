package org.csstudio.config.authorizeid;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceTracker;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AuthorizeIdActivator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.config.authorizeid"; //$NON-NLS-1$


    private static AuthorizeIdActivator INSTANCE;


    private LdapServiceTracker _ldapServiceTracker;

    /**
     * Don't instantiate.
     * Called by framework.
     */
    public AuthorizeIdActivator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("TreeModelActivator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

	@Override
	protected void doStart(final BundleContext context) throws Exception {
        _ldapServiceTracker = new LdapServiceTracker(context);
        _ldapServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doStop(@Nonnull final BundleContext context) throws Exception {
        _ldapServiceTracker.close();
    }

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

    /**
     * @return the LDAP service or null
     */
	@CheckForNull
    public ILdapService getLdapService() {
        return (ILdapService) _ldapServiceTracker.getService();
    }

    /**
     * @return the shared instance
     */
    public static AuthorizeIdActivator getDefault() {
        return INSTANCE;
    }
}
