package org.csstudio.config.authorizeid;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceTracker;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AuthorizeIdActivator extends AbstractUIPlugin {

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
	public void start(final BundleContext context) throws Exception {
		super.start(context);
        _ldapServiceTracker = new LdapServiceTracker(context);
        _ldapServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop(@Nonnull final BundleContext context) throws Exception {
    	super.stop(context);
        _ldapServiceTracker.close();
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
