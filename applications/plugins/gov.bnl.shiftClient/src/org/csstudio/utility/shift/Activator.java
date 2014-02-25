package org.csstudio.utility.shift;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import gov.bnl.shiftClient.ShiftClient;
import gov.bnl.shiftClient.ShiftClientImpl.ShiftClientBuilder;

public class Activator implements BundleActivator {

	private static Logger log = Logger.getLogger(Activator.class.getName());



	private ShiftClient retrieveClient() {
		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor("gov.bnl.shiftclient");

			if (config.length == 0) {
				log.log(Level.INFO, "No configured client for Shift found: using default configuration");
				ShiftClient client = ShiftClientBuilder.serviceURL().withHTTPAuthentication(false).create();
				return client;
			}

			if (config.length == 1) {
				ShiftClient client = (ShiftClient) config[0].createExecutableExtension("shiftclient");
				return client;
			}

			throw new IllegalStateException("More than one ShiftClient was configured through extensions.");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not retrieve configured client for Shift", e);
			return null;
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(BundleContext context) throws Exception {
		retrieveClient();
	}

}
