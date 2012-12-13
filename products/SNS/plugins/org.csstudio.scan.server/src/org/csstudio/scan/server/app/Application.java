/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server.app;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.internal.ScanServerImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;

/** RCP Application that runs the scan server
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    final private CountDownLatch run = new CountDownLatch(1);

    // It is important to keep a reference to the server implementation!!
    //
    // Otherwise it could be garbage collected,
    // and then clients will get java.rmi.NoSuchObjectException
    // when they try to invoke methods in the server.
    private ScanServerImpl server;

    @Override
    public Object start(final IApplicationContext context) throws Exception
    {
    	final Logger log = Logger.getLogger(getClass().getName());
    	try
    	{
	        // Display config info
	        final String version = (String)
	            context.getBrandingBundle().getHeaders().get("Bundle-Version");
	        log.info(context.getBrandingName() + " " + version);
	        log.info("Beamline config   : " + ScanSystemPreferences.getBeamlineConfigPath());
	        log.info("Simulation config : " + ScanSystemPreferences.getSimulationConfigPath());
	        log.info("Server host:port  : " + ScanSystemPreferences.getServerHost() + ":" + ScanSystemPreferences.getServerPort());
	        log.info("Pre-scan commands : " + Arrays.toString(ScanSystemPreferences.getPreScanPaths()));
	        log.info("Post-scan commands: " + Arrays.toString(ScanSystemPreferences.getPostScanPaths()));
	        log.info("Script paths      : " + Arrays.toString(ScanSystemPreferences.getScriptPaths()));

	        // Start server
	        final int port = ScanSystemPreferences.getServerPort();
	        server = new ScanServerImpl(port);
	        server.start();
	        log.info("Scan Server running on ports " + port + " (RMI Registry) and " + (port + 1) + " (" + ScanServer.RMI_SCAN_SERVER_NAME + " interface)");

	        // Register console commands
	        ConsoleCommands commands = new ConsoleCommands(server);
	        final BundleContext bundle_context = context.getBrandingBundle().getBundleContext();
	        bundle_context.registerService(CommandProvider.class.getName(), commands, null);

	        // Keep running...
	        run.await();

	        // Release commands
	        commands = null;
    	}
    	catch (Exception ex)
    	{
    		log.log(Level.SEVERE, "Exiting on error", ex);
    		return Integer.valueOf(-1);
    	}

        return EXIT_OK;
    }

    @Override
    public void stop()
    {
        System.out.println("Scan Server stop requested.");

        // Signal main thread in start() to exit
        run.countDown();
    }
}
