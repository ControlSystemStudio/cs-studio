package org.csstudio.nams.configurator;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.configurator.actions.DeleteConfugurationBeanAction;
import org.csstudio.nams.configurator.actions.DuplicateConfigurationBeanAction;
import org.csstudio.nams.configurator.editor.AbstractEditor;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceImpl;
import org.csstudio.nams.configurator.service.synchronize.SynchronizeServiceImpl;
import org.csstudio.nams.configurator.views.AbstractNamsView;
import org.csstudio.nams.configurator.views.SyncronizeView;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
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
	ConfigurationServiceFactory configurationServiceFactory, @OSGiService
	@Required
	Logger logger,
	@OSGiService
	@Required
	ExecutionService executionService
	) {
		LocalStoreConfigurationService localStoreConfigurationService = configurationServiceFactory
				.getConfigurationService(
						"oracle.jdbc.driver.OracleDriver",
						// "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS =
						// (PROTOCOL = TCP) (HOST = dbsrv01.desy.de)(PORT =
						// 1521)) (ADDRESS = (PROTOCOL = TCP) (HOST =
						// dbsrv02.desy.de)(PORT = 1521)) (ADDRESS = (PROTOCOL =
						// TCP) (HOST= dbsrv03.desy.de) (PORT = 1521))
						// (LOAD_BALANCE = yes) (CONNECT_DATA = (SERVER =
						// DEDICATED)(SERVICE_NAME = desy_db.desy.de)
						// (FAILOVER_MODE =(TYPE = NONE) (METHOD = BASIC)
						// (RETRIES = 180)(DELAY = 5))))",
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION),
						"org.hibernate.dialect.Oracle10gDialect", // FIXME mz2008-07-10: Könnte Problem bei der DESY erklären, wenn die keine 10er einsetzen wird das scheitern!
						// "krykmant",
						// "krykmant");
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER),
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD));

		// prepare bean-service
		ConfigurationBeanServiceImpl.staticInject(logger);
		ConfigurationBeanService beanService = new ConfigurationBeanServiceImpl(
				localStoreConfigurationService);

		// prepare Views
		AbstractNamsView.staticInject(beanService);

		// prepare editors
		AbstractEditor.staticInject(beanService);

		// prepare actions
		DeleteConfugurationBeanAction.staticInject(beanService);
		DuplicateConfigurationBeanAction.staticInject(beanService);
		
		// prepare sync-view
		SyncronizeView.staticInjectSynchronizeService(new SynchronizeServiceImpl(logger, executionService, localStoreConfigurationService));
	}
}