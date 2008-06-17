package org.csstudio.nams.configurator.ui;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.configurator.model.declaration.ConfigurationElementModelAccessService;
import org.csstudio.nams.configurator.ui.views.AlarmbearbeiterView;
import org.csstudio.nams.service.logging.declaration.Logger;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConfiguratorUIActivator extends AbstractBundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.configurator.ui";


	@OSGiBundleActivationMethod
	public void activateBundle(
			@OSGiService @Required final Logger logger,
			@OSGiService @Required final ConfigurationElementModelAccessService configurationElementModelAccessService
	) {
		AlarmbearbeiterView.staticInject(logger);
		AlarmbearbeiterView.staticInject(configurationElementModelAccessService);
	}
}
