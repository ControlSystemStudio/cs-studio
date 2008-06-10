package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
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
			@OSGiService @Required ConfigurationStoreService configurationStoreService
	) {
		//TODO pvConnectionService auf nicht statischen Weg beziehen!
		IProcessVariableConnectionService pvConnectionService = ProcessVariableConnectionServiceFactory.getProcessVariableConnectionService();
		
		RegelwerkBuilderServiceImpl.staticInject(pvConnectionService);
		RegelwerkBuilderServiceImpl.staticInject(configurationStoreService);
	}
}
