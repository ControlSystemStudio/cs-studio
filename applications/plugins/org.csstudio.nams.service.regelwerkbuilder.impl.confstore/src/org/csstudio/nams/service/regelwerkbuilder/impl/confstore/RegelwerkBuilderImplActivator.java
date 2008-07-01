package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class RegelwerkBuilderImplActivator extends AbstractBundleActivator
		implements BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.regelwerkbuilder.impl.confstore";

	@OSGiBundleActivationMethod
	public void startBundle(
			@OSGiService @Required
	        ProcessVariableConnectionServiceFactory pvConnectionServiceFactory,
	        @OSGiService @Required
	        PreferenceService preferenceService,
	        @OSGiService @Required
	        ConfigurationServiceFactory configurationServiceFactory
	        
	) {
		
		
		LocalStoreConfigurationService configurationStoreService = configurationServiceFactory.getConfigurationService(
				"org.apache.derby.jdbc.ClientDriver",
				preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_CONNECTION),
				"org.hibernate.dialect.DerbyDialect",
				preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_USER),
				preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_PASSWORD));
		
		IProcessVariableConnectionService pvConnectionService = pvConnectionServiceFactory
				.createProcessVariableConnectionService();

		RegelwerkBuilderServiceImpl.staticInject(pvConnectionService);
		RegelwerkBuilderServiceImpl.staticInject(configurationStoreService);
	}
}
