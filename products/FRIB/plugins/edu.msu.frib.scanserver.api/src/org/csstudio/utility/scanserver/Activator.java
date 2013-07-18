package org.csstudio.utility.scanserver;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.msu.frib.scanserver.api.ScanServer;
import edu.msu.frib.scanserver.api.ScanServerClient;



public class Activator implements BundleActivator {

	private static final Logger log = Logger.getLogger(Activator.class.getName());

	@Override
	public void start(BundleContext context) throws Exception {
		ScanServer.setClient(configuredClient());

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}
	

	
	/**
	 * Retrieves the data sources that have been registered through the extension point.
	 * 
	 * @return the registered data sources
	 */
	public static ScanServerClient configuredClient() {
		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry()
			.getConfigurationElementsFor("edu.msu.frib.scanserver.api.client");
			
			if (config.length == 0) {
				log.log(Level.INFO, "No configured client for ScanServer found: using default configuration");
				return null;
			}
			
			if (config.length == 1) {
				ScanServerClient client = (ScanServerClient) config[0].createExecutableExtension("scanserverclient");
				return client;
			}
			
			throw new IllegalStateException("More than one ScanServerClient was configured through extensions.");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not retrieve configured client for ScanServer", e);
			return null;
		}
	}
	

}

