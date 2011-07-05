
package org.csstudio.nams.service.preferenceservice;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.bridge4ui.PreferenceStoreAccessor;
import org.csstudio.nams.service.preferenceservice.definition.PreferenceStoreServiceImpl;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class PreferenceServiceActivator extends AbstractBundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.ams.service.preferenceservice";

	/** The plug-in ID of the AMS-Plugin */
	private static final String AMS_PLUGIN_ID = "org.csstudio.ams";

	/**
	 * The preference store used by this plugin.
	 */
	private IPreferenceStore _preferenceStore;

	/**
	 * The constructor
	 */
	public PreferenceServiceActivator() {
	    // Nothing to do
	}

	/**
	 * Starts the bundle activator instance.
	 * 
	 * @see BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@OSGiBundleActivationMethod
	public OSGiServiceOffers startBundle(@OSGiService
	@Required
	final Logger logger) {
		final OSGiServiceOffers result = new OSGiServiceOffers();

		final IPreferenceStore preferenceStore = this
				.getEclipsePreferenceStoreWithAMSId();

		result.put(PreferenceService.class, new PreferenceStoreServiceImpl());
		result.put(PreferenceStoreAccessor.class,
				new PreferenceStoreAccessor() {
					@Override
                    public IPreferenceStore getPreferenceStore() {
						return preferenceStore;
					}
				});

		PreferenceStoreServiceImpl.staticInject(preferenceStore);

		return result;
	}

	/**
	 * Returns the preference store of the ams plugin.
	 * 
	 * @return the preference store
	 */
	private IPreferenceStore getEclipsePreferenceStoreWithAMSId() {
		if (this._preferenceStore == null) {
			this._preferenceStore = new ScopedPreferenceStore(
					new InstanceScope(),
					PreferenceServiceActivator.AMS_PLUGIN_ID);
		}
		return this._preferenceStore;
	}
}
