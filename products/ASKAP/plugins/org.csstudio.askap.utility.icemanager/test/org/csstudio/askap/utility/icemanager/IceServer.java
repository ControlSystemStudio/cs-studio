package org.csstudio.askap.utility.icemanager;

import java.util.logging.Level;
import java.util.logging.Logger;

import Ice.Communicator;
import Ice.ObjectAdapter;

public class IceServer {
	private static Logger logger = Logger.getLogger(IceServer.class.getName());

	public static void main(String[] args) {

		Communicator ic = null;
		logger.info("Starting ice server...");

		try {
			Ice.Properties props = Ice.Util.createProperties();
			// Initialize a communicator with these properties.
			Ice.InitializationData id = new Ice.InitializationData();
			props.setProperty("Ice.Default.Locator", "IceGrid/Locator:tcp -h localhost -p 4061");
			props.setProperty("XinyuIceStub.AdapterId", "XinyuIceStub");
			props.setProperty("XinyuIceStub.Endpoints", "tcp");
						
			id.properties = props;

			ic = Ice.Util.initialize(id);
			ObjectAdapter adapter = ic.createObjectAdapter("XinyuIceStub");

			MonitoringProviderStub monitorStub = new MonitoringProviderStub();
			adapter.add(monitorStub, ic.stringToIdentity("MonitoringProvider"));
			logger.info("Registering MonitoringProvider");

			adapter.activate();
			logger.info("Server is ready!");

			ic.waitForShutdown();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not start IceServer", e);
		}
	}

}
