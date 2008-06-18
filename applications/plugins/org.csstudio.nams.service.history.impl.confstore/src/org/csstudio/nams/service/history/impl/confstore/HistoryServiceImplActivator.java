package org.csstudio.nams.service.history.impl.confstore;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class HistoryServiceImplActivator extends AbstractBundleActivator implements BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.history.impl.confstore";
	
	@OSGiBundleActivationMethod
	public void startBundle(@OSGiService @Required LocalStoreConfigurationService localStoreConfigurationService) {
		HistoryServiceFactoryImpl.injectLocalStoreConfigurationService(localStoreConfigurationService);
	}
}
