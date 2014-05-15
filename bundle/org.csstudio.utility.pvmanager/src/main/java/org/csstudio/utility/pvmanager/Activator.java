package org.csstudio.utility.pvmanager;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Plugin;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceFactory;
import org.epics.pvmanager.service.ServiceRegistry;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	private static final Logger log = Logger.getLogger(Activator.class.getName());

	// The plug-in ID
	public static final String ID = "org.csstudio.utility.pvmanager";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		try {
			// Create the composite data source
			CompositeDataSource composite = new CompositeDataSource();

			// Retrieve configured data sources and add them to the
			// composite
			Map<String, DataSource> dataSources = ConfigurationHelper
					.configuredDataSources();
			for (Map.Entry<String, DataSource> entry : dataSources.entrySet()) {
				log.log(Level.CONFIG, "Adding data source {0}", entry.getKey());
				composite.putDataSource(entry.getKey(), entry.getValue());
			}

			// Sets the default data source
			String defaultDataSourceName = ConfigurationHelper
					.defaultDataSourceName();
			DataSource defaultDataSource = dataSources
					.get(defaultDataSourceName);
			if (defaultDataSource != null) {
				log.log(Level.CONFIG, "Setting default data source to {0}", defaultDataSourceName);
				composite.setDefaultDataSource(defaultDataSourceName);
			}

			// set as default data source
			PVManager.setDefaultDataSource(composite);

		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Couldn't configure PVManager with Datasources", e);
		}

		// Retrieve pvmanager services
		try {
			for (Service service : ConfigurationHelper.configuredServices()) {
				log.log(Level.CONFIG, "Adding pvmanager service " + service.getName());
				ServiceRegistry.getDefault().registerService(service);
			}
			for (ServiceFactory serviceFactory : ConfigurationHelper
					.configuredServiceFactories()) {
				log.log(Level.CONFIG, "Processing pvmanager service factory" + serviceFactory);
				for (Service service : serviceFactory.createServices()) {
					log.log(Level.CONFIG, "Adding pvmanager service " + service.getName());
					ServiceRegistry.getDefault().registerService(service);
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Couldn't configure pvmanager services", e);
		}

	}

}
