package org.csstudio.nams.configurator;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.configurator.editor.EditorUIUtils;
import org.csstudio.nams.configurator.editor.FilterbedingungEditor;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceImpl;
import org.csstudio.nams.configurator.service.synchronize.SynchronizeServiceImpl;
import org.csstudio.nams.configurator.views.AbstractNamsView;
import org.csstudio.nams.configurator.views.SyncronizeView;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
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
		// prepare bean-service
		ConfigurationBeanServiceImpl.staticInject(logger);

		// prepare utilities
		EditorUIUtils.staticInject(logger);

		// prepare Views (Editors will be injected by views!)
		AbstractNamsView.staticInject(logger);
		AbstractNamsView.staticInject(preferenceService);
		AbstractNamsView.staticInject(configurationServiceFactory);		

		IProcessVariableConnectionService pvConnectionService = pvConnectionServiceFactory
				.createProcessVariableConnectionService();
		FilterbedingungEditor.staticInject(pvConnectionService);

		// prepare sync-view
		SyncronizeView
				.staticInjectSynchronizeService(new SynchronizeServiceImpl(
						logger, executionService, preferenceService, configurationServiceFactory));
	}
}