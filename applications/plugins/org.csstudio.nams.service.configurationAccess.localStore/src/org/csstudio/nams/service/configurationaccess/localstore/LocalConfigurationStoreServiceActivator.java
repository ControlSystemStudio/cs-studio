package org.csstudio.nams.service.configurationaccess.localstore;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LocalConfigurationStoreServiceActivator extends Plugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.configurationAccess.localStore";

	public void start(BundleContext context) throws Exception {
		super.start(context);

		ConfigurationStoreService amsConfStoreService = BundleActivatorUtils
				.getAvailableService(context, ConfigurationStoreService.class);

//		LocalConfigurationStoreService serviceImpl = new LocalConfigurationStoreServiceImpl(
//				amsConfStoreService);
//		context.registerService(LocalConfigurationStoreService.class.getName(),
//				serviceImpl, null);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
}
