
package org.csstudio.nams.service.configurationaccess.localstore;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.logging.declaration.ILogger;
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
	public OSGiServiceOffers startBundle(@OSGiService
	@Required
	final ILogger logger) throws Exception {

		/*- XXX Needed for HSQL-Mode.
		Class.forName("org.hsqldb.jdbcDriver");*/

		final OSGiServiceOffers result = new OSGiServiceOffers();
		try {
			this.configurationServiceFactoryImpl = new ConfigurationServiceFactoryImpl(
					logger);
			result.put(ConfigurationServiceFactory.class,
					this.configurationServiceFactoryImpl);
		} catch (final Throwable t) {
			throw new RuntimeException(
					"Failed to start LocalConfigurationStoreService's bundle",
					t);
		}

		// prepare Configuration
		Configuration.staticInject(logger);

		return result;
	}

	@OSGiBundleDeactivationMethod
	public void stopBundle() throws Exception {
		this.configurationServiceFactoryImpl.closeSessions();
	}
}
