package org.csstudio.utility.channelfinder;

import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		ChannelFinder.setClient(configuredClient());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private static final Logger log = Logger.getLogger(Activator.class.getName());
	
	/**
	 * Retrieves the data sources that have been registered through the extension point.
	 * 
	 * @return the registered data sources
	 */
	public static ChannelFinderClient configuredClient() {
		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry()
			.getConfigurationElementsFor("gov.bnl.channelfinder.api.client");
			
			if (config.length == 0) {
				log.log(Level.INFO, "No configured client for ChannelFinder found: using default configuration");
				return null;
			}
			
			if (config.length == 1) {
				ChannelFinderClient client = (ChannelFinderClient) config[0].createExecutableExtension("channelfinderclient");
				return client;
			}
			
			throw new IllegalStateException("More than one ChannelFinderClient was configured through extensions.");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not retrieve configured client for ChannelFinder", e);
			return null;
		}
	}
	

}
