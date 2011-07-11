
package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class RegelwerkBuilderImplActivator extends AbstractBundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.regelwerkbuilder.impl.confstore";

	@OSGiBundleActivationMethod
	public void startBundle(@OSGiService
	                        @Required
	                        final ProcessVariableConnectionServiceFactory pvConnectionServiceFactory,
	                        @OSGiService
	                        @Required
	                        final PreferenceService preferenceService,
	                        @OSGiService
	                        @Required
	                        final ConfigurationServiceFactory configurationServiceFactory,
	                        @OSGiService
	                        @Required
	                        final ILogger logger) {

		final String connection = preferenceService.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_CONNECTION);
        final String username = preferenceService.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_USER);
        final String password = preferenceService.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_PASSWORD);

        final LocalStoreConfigurationService configurationStoreService =
		    configurationServiceFactory.getConfigurationService(connection,
		                                                        DatabaseType.Derby,
		                                                        username,
		                                                        password);

		final IProcessVariableConnectionService pvConnectionService = pvConnectionServiceFactory
				.createProcessVariableConnectionService();

		RegelwerkBuilderServiceImpl.staticInject(logger);
		RegelwerkBuilderServiceImpl.staticInject(pvConnectionService);
		RegelwerkBuilderServiceImpl.staticInject(configurationStoreService);
		
	    //TODO: Ist das hier die richtige Stelle?
        //      Augenscheinlich wird der Klasse StringRegel nirgendwo im Code ein Logger zugeordnet.
        //      Es kommt jedoch vor, dass die Property VALUE keinen Zahlenwert enthält, sondern einen
        //      String (Ein, Aus, ...). Das löst eine NumberFormatException aus, die auch korrekt
        //      abgefangen wird. Der Logger, den die Klasse zur Fehlerausgabe benutzt, ist jedoch null.
        StringRegel.staticInject(logger);
	}
}
