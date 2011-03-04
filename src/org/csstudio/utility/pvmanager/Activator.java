package org.csstudio.utility.pvmanager;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.epics.pvmanager.*;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.pvmanager";

	private static final String defaultDataSource = "epics";

	/*
	* (non-Javadoc)
	* @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	*/
	public void start(BundleContext context) throws Exception {
		super.start(context);
		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry()
			.getConfigurationElementsFor("org.csstudio.utility.pvmanager.datasource");
			
			CompositeDataSource composite = new CompositeDataSource();
			for (IConfigurationElement iConfigurationElement : config) {
				final Object o = iConfigurationElement
						.createExecutableExtension("datasource");
				final String prefix = iConfigurationElement.getAttribute("prefix");
				if (o instanceof DataSource) {					
					DataSource ds = (DataSource) o;
					// Add each valid DataSource to the PVManager
					composite.putDataSource(prefix, ds);
					if(prefix.equals(defaultDataSource))
						composite.setDefaultDataSource(prefix);
				}
			}
			PVManager.setDefaultDataSource(composite);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
