package org.csstudio.nams.service.configurationaccess.localstore;


import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class LocalConfigurationStoreServiceActivator extends
		AbstractBundleActivator implements BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.configurationAccess.localStore";
	private ConfigurationServiceFactoryImpl configurationServiceFactoryImpl;

	@OSGiBundleActivationMethod
	public OSGiServiceOffers startBundle() throws Exception {
		OSGiServiceOffers result = new OSGiServiceOffers();
		try {
			configurationServiceFactoryImpl = new ConfigurationServiceFactoryImpl();
			result.put(ConfigurationServiceFactory.class, configurationServiceFactoryImpl);
		} catch (final Throwable t) {
			throw new RuntimeException(
					"Failed to start LocalConfigurationStoreService's bundle",
					t);
		}
		return result;
	}

	@OSGiBundleDeactivationMethod
	public void stopBundle() throws Exception {
		configurationServiceFactoryImpl.closeSessions();
	}
}
