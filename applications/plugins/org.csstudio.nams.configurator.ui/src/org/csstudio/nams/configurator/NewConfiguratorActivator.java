package org.csstudio.nams.configurator;

import org.csstudio.ams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.ams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.configurator.actions.DeleteConfiguration;
import org.csstudio.nams.configurator.modelmapping.ConfigurationModel;
import org.csstudio.nams.configurator.modelmapping.ModelFactory;
import org.csstudio.nams.configurator.views.AlarmbearbeiterView;
import org.csstudio.nams.configurator.views.AlarmbearbeitergruppenView;
import org.csstudio.nams.configurator.views.AlarmtopicView;
import org.csstudio.nams.configurator.views.FilterView;
import org.csstudio.nams.configurator.views.FilterbedingungView;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.osgi.framework.BundleActivator;

public class NewConfiguratorActivator extends AbstractBundleActivator implements
		BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.newconfigurator";

	@OSGiBundleActivationMethod
	public void startBundle(@OSGiService
	@Required
	PreferenceService preferenceService, @OSGiService
	@Required
	ConfigurationServiceFactory configurationServiceFactory) {
		LocalStoreConfigurationService localStoreConfigurationService = configurationServiceFactory
				.getConfigurationService(
						"oracle.jdbc.driver.OracleDriver",
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION),
						"org.hibernate.dialect.Oracle10gDialect",
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER),
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD));

		ModelFactory modelFactory = new ModelFactory(
				localStoreConfigurationService);
		
		//prepare Controler
		AlarmbearbeitergruppenView.staticInject(modelFactory);
		AlarmbearbeiterView.staticInject(modelFactory);
		AlarmtopicView.staticInject(modelFactory);
		FilterView.staticInject(modelFactory);
		FilterbedingungView.staticInject(modelFactory);
		ConfigurationModel.staticInject(localStoreConfigurationService);
		DeleteConfiguration.staticInject(localStoreConfigurationService);
		
	}
}