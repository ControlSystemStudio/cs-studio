package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RegelwerkBuilderImplActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.service.regelwerkbuilder.impl.confstore";
	
	/**
	 * The constructor
	 */
	public RegelwerkBuilderImplActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ConfigurationStoreService configurationStoreService = BundleActivatorUtils.getAvailableService(context, ConfigurationStoreService.class);
		IProcessVariableConnectionService pvConnectionService = ProcessVariableConnectionServiceFactory.getProcessVariableConnectionService();
		RegelwerkBuilderServiceImpl.staticInject(pvConnectionService);
		RegelwerkBuilderServiceImpl.staticInject(configurationStoreService);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

}
