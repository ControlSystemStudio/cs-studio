package org.csstudio.nams.configurator;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationEditingStoreService;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.ams.service.logging.declaration.Logger;
import org.csstudio.nams.configurator.testutils.AbstractBundleActivator;
import org.csstudio.nams.configurator.testutils.OSGiBundleActivationMethod;
import org.csstudio.nams.configurator.testutils.OSGiService;
import org.csstudio.nams.configurator.testutils.Required;
import org.csstudio.nams.configurator.treeviewer.ConfigurationTreeView;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the bundles life cycle
 */
public class Activator extends AbstractBundleActivator implements
		BundleActivator {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.csstudio.nams.configurator";

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@OSGiBundleActivationMethod
	public void bundleStart(
			@OSGiService @Required ConfigurationEditingStoreService editingStoreService,
			@OSGiService @Required ConfigurationStoreService storeService,
			@OSGiService @Required Logger logger
	) {
		// for debugging : Alle Services injected?
		if (editingStoreService != null && storeService != null
				&& logger != null) {
			logger.logInfoMessage(this, "activated...");
		}

		// Inject into extension classes.
		ConfigurationTreeView
				.staticInjectEditingStoreService(editingStoreService);
		ConfigurationTreeView.staticInjectStoreService(storeService);
		ConfigurationTreeView.staticInjectLogger(logger);
	}
}
