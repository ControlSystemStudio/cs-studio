
package org.csstudio.nams.service.history.impl.confstore;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;

/**
 * The activator class controls the plug-in life cycle
 */
public class HistoryServiceImplActivator extends AbstractBundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.history.impl.confstore";

	@OSGiBundleActivationMethod
	public void startBundle(@OSGiService
	@Required
	final PreferenceService preferenceService, @OSGiService
	@Required
	final ConfigurationServiceFactory configurationServiceFactory) {

		final LocalStoreConfigurationService localStoreConfigurationService = configurationServiceFactory
				.getConfigurationService(
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_CONNECTION),
						DatabaseType.Derby,
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_USER),
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_PASSWORD));

		HistoryServiceFactoryImpl
				.injectLocalStoreConfigurationService(localStoreConfigurationService);
	}
}
