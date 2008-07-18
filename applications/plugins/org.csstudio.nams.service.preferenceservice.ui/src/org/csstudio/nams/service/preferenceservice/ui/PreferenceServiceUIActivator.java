package org.csstudio.nams.service.preferenceservice.ui;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.preferenceservice.declaration.bridge4ui.PreferenceStoreAccessor;
import org.csstudio.nams.service.preferenceservice.ui.preferencepages.AMSInternalMessagingServerSetUp;
import org.csstudio.nams.service.preferenceservice.ui.preferencepages.AMSInternalMessagingTopicsSetUp;
import org.csstudio.nams.service.preferenceservice.ui.preferencepages.ApplicationDatabaseSetUp;
import org.csstudio.nams.service.preferenceservice.ui.preferencepages.ConfigurationDatabaseSetUp;
import org.csstudio.nams.service.preferenceservice.ui.preferencepages.PreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class PreferenceServiceUIActivator extends AbstractBundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.preferenceservice.ui";

	/**
	 * Starts the bundle activator instance.
	 * 
	 * @see BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@OSGiBundleActivationMethod
	public void startBundle(@OSGiService
	@Required
	final Logger logger, 
	@OSGiService
	@Required PreferenceStoreAccessor preferenceStoreAccessor
	) {
		IPreferenceStore preferenceStore = preferenceStoreAccessor.getPreferenceStore();
		
		PreferenceInitializer.staticInject(preferenceStore);
		ConfigurationDatabaseSetUp.staticInject(preferenceStore);
		ApplicationDatabaseSetUp.staticInject(preferenceStore);
		AMSInternalMessagingServerSetUp.staticInject(preferenceStore);
		AMSInternalMessagingTopicsSetUp.staticInject(preferenceStore);
	}
}
