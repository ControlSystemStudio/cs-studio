package org.csstudio.nams.configurator;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.configurator.actions.DeleteConfugurationBeanAction;
import org.csstudio.nams.configurator.actions.DuplicateConfigurationBeanAction;
import org.csstudio.nams.configurator.editor.AbstractEditor;
import org.csstudio.nams.configurator.editor.EditorUIUtils;
import org.csstudio.nams.configurator.editor.FilterbedingungEditor;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceImpl;
import org.csstudio.nams.configurator.service.synchronize.SynchronizeServiceImpl;
import org.csstudio.nams.configurator.views.AbstractNamsView;
import org.csstudio.nams.configurator.views.SyncronizeView;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
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
	Logger logger, @OSGiService
	@Required
	ExecutionService executionService, @OSGiService
	@Required
	ProcessVariableConnectionServiceFactory pvConnectionServiceFactory) {
		LocalStoreConfigurationService localStoreConfigurationService = configurationServiceFactory
				.getConfigurationService(

						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION),
						DatabaseType.Oracle10g, // FIXME mz2008-07-10: Könnte
												// Problem bei der DESY
												// erklären, wenn die keine 10er
												// einsetzen wird das scheitern!
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER),
						preferenceService
								.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD));

		// prepare bean-service
		ConfigurationBeanServiceImpl.staticInject(logger);
		ConfigurationBeanService beanService = new ConfigurationBeanServiceImpl(
				localStoreConfigurationService);

		// prepare utilities
		EditorUIUtils.staticInject(logger);

		// prepare Views
		AbstractNamsView.staticInject(logger);
		AbstractNamsView.staticInject(preferenceService);
		AbstractNamsView.staticInject(configurationServiceFactory);		

		// prepare editors
		AbstractEditor.staticInject(beanService);

		IProcessVariableConnectionService pvConnectionService = pvConnectionServiceFactory
				.createProcessVariableConnectionService();
		FilterbedingungEditor.staticInject(pvConnectionService);

		// prepare actions TODO Dieses sollten die Views selber tun.
		DeleteConfugurationBeanAction.staticInject(beanService);
		DuplicateConfigurationBeanAction.staticInject(beanService);

		// prepare sync-view
		SyncronizeView
				.staticInjectSynchronizeService(new SynchronizeServiceImpl(
						logger, executionService,
						localStoreConfigurationService));
	}
}