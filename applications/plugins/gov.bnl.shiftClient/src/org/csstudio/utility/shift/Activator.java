package org.csstudio.utility.shift;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import gov.bnl.shiftClient.ShiftApiClient;
import gov.bnl.shiftClient.ShiftApiClientImpl.ShiftClientBuilder;

public class Activator implements BundleActivator {

	private static Logger log = Logger.getLogger(Activator.class.getName());



	private ShiftApiClient retrieveClient() {
		try {
			IConfigurationElement[] config = Platform
					.getExtensionRegistry()
					.getConfigurationElementsFor("gov.bnl.shiftclient");

			if (config.length == 0) {
				log.log(Level.INFO,
						"No configured client for Shift found: using default configuration");
				ShiftApiClient client = ShiftClientBuilder.serviceURL().withHTTPAuthentication(false).create();
				return client;
			}

			if (config.length == 1) {
				ShiftApiClient client = (ShiftApiClient) config[0].createExecutableExtension("shiftclient");
				return client;
			}

			throw new IllegalStateException(
					"More than one OlogClient was configured through extensions.");
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Could not retrieve configured client for Olog", e);
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
