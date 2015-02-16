package org.csstudio.auth.internal;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.auth.internal.preferences.SystemPropertyPreferenceEntry;
import org.csstudio.auth.security.SecurityFacade;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AuthActivator implements BundleActivator {

	/**
	 * This plugin's ID.
	 */
	public static final String ID = "org.csstudio.auth"; //$NON-NLS-1$

	private static BundleContext context;

	private static AuthActivator INSTANCE;
	
	private static final Logger log = Logger.getLogger(AuthActivator.class.getName());
	
	static BundleContext getContext() {
		return context;
	}

	
    /**
     * Don't instantiate.
     * Called by framework.
     */
    public AuthActivator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }
    
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		AuthActivator.context = bundleContext;
		applySystemPropertyDefaults();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		AuthActivator.context = null;
	}
	
	/**
	 * Returns the shared instance.
	 * 
	 * @return Return the shared instance.
	 */
	public static AuthActivator getDefault() {
		return INSTANCE;
	}
	
	/**
	 * Applies the default values for system properties set up in the CSS
	 * preferences.
	 */
	private void applySystemPropertyDefaults() {
		Collection<SystemPropertyPreferenceEntry> properties = SystemPropertyPreferenceEntry
				.loadFromPreferences();
		for (SystemPropertyPreferenceEntry entry : properties) {
			// the preferences are for defaults, so they are applied only if
			// the system property is not already set to some other value
			if (System.getProperty(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue());
				log.log(Level.FINE, "Setting system property: " + entry);
			}
		}
	}
}
