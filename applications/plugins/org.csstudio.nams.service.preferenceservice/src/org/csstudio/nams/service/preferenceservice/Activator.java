package org.csstudio.nams.service.preferenceservice;

import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.definition.PreferenceStoreServiceImpl;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	/**
	 * The preference store used by this plugin.
	 */
	private IPreferenceStore _preferenceStore;

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.ams.service.preferenceservice";

	/** The plug-in ID of the AMS-Plugin */
	private static final String AMS_PLUGIN_ID = "org.csstudio.ams";

	/** The shared instance */
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * Startet dieses Bundle und registriert die Standvariante des
	 * {@link PreferenceService}s.
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		context.registerService(PreferenceService.class.getName(),
				new PreferenceStoreServiceImpl(), null);
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance to used by the service implementation.
	 * 
	 * @return the shared instance
	 */
	public static Activator getInstanceForServiceImplementation() {
		return plugin;
	}

	/**
	 * Returns the preference store of the ams plugin.
	 * 
	 * @return the preference store
	 */
	public IPreferenceStore getEclipsePreferenceStoreWithAMSId() {
		if (_preferenceStore == null) {
			_preferenceStore = new ScopedPreferenceStore(new InstanceScope(),
					AMS_PLUGIN_ID);
		}
		return _preferenceStore;
	}

}
