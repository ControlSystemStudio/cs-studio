package org.csstudio.archive.engine;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import java.util.logging.Logger;

/**
 * This starts a JMX server so we can remotely monitor the GUI.
 * We do it here rather than with the JMX command line arguments so that we can dynamically allocate ports
 */ 
public final class JMXServer {	
	
	/**
	 * Private constructor for JMX Server utility class.
	 */
	private JMXServer() {
	    
	}
	
	/**
	 * Starts the server.
	 */
	public static void startJMXServer() {
		final Logger logger = Activator.getLogger();
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			JMXServiceURL url = new JMXServiceURL("rmi", null, 0);
			JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
			cs.start();
		    logger.info("JMX url: " + cs.getAddress().toString());
		} catch (Exception e) {
			logger.info("Failed to start JMX server: " + e.getMessage());
		}
	}
}
