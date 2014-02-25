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

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.logging.LogConfigurator;
import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.server.httpd.ScanWebServer;
import org.csstudio.scan.server.internal.ScanServerImpl;
import org.csstudio.scan.server.pvaccess.PVAccessServer;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/** RCP Application that runs the scan server
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    private static String bundle_version = "?";
    final private CountDownLatch run = new CountDownLatch(1);

    // It is important to keep a reference to the server implementation!!
    //
    // Otherwise it could be garbage collected,
    // and then clients will get java.rmi.NoSuchObjectException
    // when they try to invoke methods in the server.
    private ScanServerImpl server;
    
    /** @return Bundle version info */
    public static String getBundleVersion()
    {
        synchronized (Application.class)
        {
            return bundle_version;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object start(final IApplicationContext context) throws Exception
    {
		// Display configuration info
		synchronized (Application.class) 
		{
			final String version = (String) context.getBrandingBundle()
					.getHeaders().get("Bundle-Version");
			bundle_version = context.getBrandingName() + " " + version;
		}
        
    	// Create parser for arguments and run it.
        final String args[] = (String []) context.getArguments().get("application.args");

		final ArgParser parser = new ArgParser();
		final BooleanOption help_opt = new BooleanOption(parser, "-help", "Display help");
		final BooleanOption version_opt = new BooleanOption(parser, "-version", "Display version info");
		parser.addEclipseParameters();
		try {
			parser.parse(args);
		} catch (final Exception ex) {
			System.out.println(ex.getMessage() + "\n" + parser.getHelp());
			return IApplication.EXIT_OK;
		}
		if (help_opt.get()) {
			System.out.println(bundle_version + "\n\n" + parser.getHelp());
			return IApplication.EXIT_OK;
		}
		if (version_opt.get()) {
			System.out.println(bundle_version);
			return IApplication.EXIT_OK;
		}
    	
    	final Logger log = Logger.getLogger(getClass().getName());
    	try
    	{
	        // Display config info
	        final Bundle bundle = context.getBrandingBundle();
	        log.info(bundle_version);
            
            LogConfigurator.configureFromPreferences();
            
	        log.info("Scan config       : " + ScanSystemPreferences.getScanConfigPath());
	        log.info("Simulation config : " + ScanSystemPreferences.getSimulationConfigPath());
	        log.info("Server host:port  : " + ScanSystemPreferences.getServerHost() + ":" + ScanSystemPreferences.getServerPort());
	        log.info("Pre-scan commands : " + Arrays.toString(ScanSystemPreferences.getPreScanPaths()));
	        log.info("Post-scan commands: " + Arrays.toString(ScanSystemPreferences.getPostScanPaths()));
	        log.info("Script paths      : " + Arrays.toString(ScanSystemPreferences.getScriptPaths()));

	        // Start server
	        final int port = ScanSystemPreferences.getServerPort();
	        server = new ScanServerImpl();
	        server.start();
	        log.info("Scan Server running on port " + port + " (REST interface)");
	        final ScanWebServer httpd = new ScanWebServer(bundle.getBundleContext(), server, port);
	        final PVAccessServer pva = new PVAccessServer(server);
	        pva.initializeServerContext();
        
	        // Register console commands
	        ConsoleCommands commands = new ConsoleCommands(server);
	        final BundleContext bundle_context = bundle.getBundleContext();
	        bundle_context.registerService(CommandProvider.class.getName(), commands, null);

	        // Keep running...
	        run.await();
	        server.stop();

	        httpd.stop();
	        pva.destroyServerContext();
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

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        System.out.println("Scan Server stop requested.");

        // Signal main thread in start() to exit
        run.countDown();
    }
}
