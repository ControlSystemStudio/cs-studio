package org.csstudio.utility.olog;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.msu.nscl.olog.api.Olog;
import edu.msu.nscl.olog.api.OlogClient;
import edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder;

public class Activator implements BundleActivator {

	private static Logger log = Logger.getLogger(Activator.class.getName());

	@Override
	public void start(BundleContext context) throws Exception {
//		OlogClientManager.registerDefaultClient(retrieveClient());
		Olog.setClient(retrieveClient());
	}

	private OlogClient retrieveClient() {
		try {
			IConfigurationElement[] config = Platform
					.getExtensionRegistry()
					.getConfigurationElementsFor("edu.msu.nscl.olog.api.client");

			if (config.length == 0) {
				log.log(Level.INFO,
						"No configured client for Olog found: using default configuration");
				OlogClient client = OlogClientBuilder.serviceURL()
						.withHTTPAuthentication(false).create();
				return client;
			}

			if (config.length == 1) {
				OlogClient client = (OlogClient) config[0]
						.createExecutableExtension("ologclient");
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

}
