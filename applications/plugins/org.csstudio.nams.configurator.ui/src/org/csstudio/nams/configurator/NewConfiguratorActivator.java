package org.csstudio.nams.configurator;

import org.csstudio.ams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.ams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.configurator.actions.DeleteAlarmbearbeiterAction;
import org.csstudio.nams.configurator.editor.AlarmbearbeiterEditor;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceImpl;
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

//		ModelFactory modelFactory = new ModelFactory(
//				localStoreConfigurationService);
		
		ConfigurationBeanService beanService = new ConfigurationBeanServiceImpl(localStoreConfigurationService);
		
		//prepare Controler
		AlarmbearbeitergruppenView.staticInject(beanService);
		AlarmbearbeiterView.staticInject(beanService);
		AlarmtopicView.staticInject(beanService);
		FilterView.staticInject(beanService);
		FilterbedingungView.staticInject(beanService);
		
		AlarmbearbeiterEditor.staticInject(beanService);
		
		DeleteAlarmbearbeiterAction.staticInject(beanService);
		
//		ConfigurationModel.staticInject(localStoreConfigurationService);
//		DeleteConfiguration.staticInject(localStoreConfigurationService);
		
	}
}