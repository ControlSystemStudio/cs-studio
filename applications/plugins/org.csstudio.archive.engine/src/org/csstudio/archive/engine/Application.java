/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.IntegerOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ArchiveConfigFactory;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.EngineServer;
import org.csstudio.logging.LogConfigurator;
import org.csstudio.security.PasswordInput;
import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse Application for CSS archive engine
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto - added "-skip_last" option.
 */
public class Application implements IApplication
{
    /** HTTP Server port */
    private int port;

    /** Request file */
    private String engine_name;

    /** Application model */
    private EngineModel model;
    
    /** Option: if skip reading last sample time or not */
    private boolean skip_last = false;

    /** Obtain settings from preferences and command-line arguments
     *  @param args Command-line arguments
     *  @return <code>true</code> if continue, <code>false</code> to end application
     */
    @SuppressWarnings("nls")
    private boolean getSettings(final String args[], final IApplicationContext context)
    {
    	// Display configuration info
        final String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;
    	
        // Create the parser and run it.
        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help", "Display help");
        final BooleanOption version_opt = new BooleanOption(parser, "-version", "Display version info");
        final BooleanOption skip_last_opt = new BooleanOption(parser, "-skip_last",
                "Skip reading last sample time from RDB on start-up");
        final IntegerOption port_opt = new IntegerOption(parser, "-port", "4812",
                    "HTTP server port", 4812);
        final StringOption engine_name_opt = new StringOption(parser,
                    "-engine", "demo_engine", "Engine config name", null);
        final StringOption set_password_opt = new StringOption(parser,
                "-set_password", "plugin/key=value", "Set secure preferences", null);
        parser.addEclipseParameters();
        try
        {
            parser.parse(args);
        }
        catch (final Exception ex)
        {   // Bad options
            System.out.println(ex.getMessage());
            System.out.println(parser.getHelp());
            return false;
        }
        if (help_opt.get())
        {   // Help requested
            System.out.println(app_info + "\n\n" + parser.getHelp());
            return false;
        }
        if (version_opt.get())
        {   // Version requested
            System.out.println(app_info);
            return false;
        }

        // Check arguments
        String option = set_password_opt.get();
		if (option != null)
        {	// Split "plugin/key=value"
        	final String pref, value;
        	final int sep = option.indexOf("=");
        	if (sep >= 0)
        	{
        		pref = option.substring(0, sep);
        		value = option.substring(sep + 1);
        	}
        	else
        	{
        		pref = option;
        		value = PasswordInput.readPassword("Value for " + pref + ":");
        	}
        	try
        	{
        		SecurePreferences.set(pref, value);
        	}
        	catch (Exception ex)
        	{
        		ex.printStackTrace();
        	}
        	return false;
        }
        
        if (engine_name_opt.get() == null)
        {
            System.out.println("Missing option " + engine_name_opt.getOption());
            System.out.println(parser.getHelp());
            return false;
        }

        // Copy stuff from options into member vars.
        port = port_opt.get();
        engine_name = engine_name_opt.get();
        skip_last = skip_last_opt.get();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public Object start(final IApplicationContext context) throws Exception
    {
        final String args[] =
            (String []) context.getArguments().get("application.args");
        if (!getSettings(args, context))
            return Integer.valueOf(-2);

        // Initialize logging
        LogConfigurator.configureFromPreferences();

        // Setup groups, channels, writer
        // This is all single-threaded!
        final Logger logger = Activator.getLogger();
        EngineModel.VERSION =  (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
        logger.info("Archive Engine " + EngineModel.VERSION);
        try
        {
            model = new EngineModel();
            // Setup takes some time, but engine server should already respond.
            EngineServer server;
            try
            {
                server = new EngineServer(model, port);
            }
            catch (final Exception ex)
            {
                logger.log(Level.SEVERE, "Cannot start server on port " + port, ex);
                return EXIT_OK;
            }

            boolean run = true;
            while (run)
            {
                logger.info("Reading configuration '" + engine_name + "'");
                BenchmarkTimer timer = new BenchmarkTimer();
                ArchiveConfig config = null;
                try
                {
                    config = ArchiveConfigFactory.getArchiveConfig();
                    model.readConfig(config, engine_name, port, skip_last);
                }
                catch (final Exception ex)
                {
                    logger.log(Level.SEVERE, "Cannot read configuration", ex);
                    return EXIT_OK;
                }
                finally
                {
                    if (config != null)
                        config.close();
                }
                timer.stop();
                logger.info("Read configuration: " + model.getChannelCount() +
                            " channels in " + timer.toString());

                // Run until model gets stopped via HTTPD or #stop()
                logger.info("Running, CA addr list: "
                    + System.getProperty("com.cosylab.epics.caj.CAJContext.addr_list"));
                model.start();
                while (true)
                {
                    Thread.sleep(1000);
                    if (model.getState() == EngineModel.State.SHUTDOWN_REQUESTED)
                    {
                        run = false;
                        break;
                    }
                    if (model.getState() == EngineModel.State.RESTART_REQUESTED)
                        break;
                }
                // Stop sampling
                logger.info("ArchiveEngine ending");
                model.stop();
                model.clearConfig();
            }

            logger.info("ArchiveEngine stopped");
            server.stop();
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Unhandled Main Loop Error", ex);
            return Integer.valueOf(-1);
        }

        return EXIT_OK;
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        if (model != null)
            model.requestStop();
    }
}
