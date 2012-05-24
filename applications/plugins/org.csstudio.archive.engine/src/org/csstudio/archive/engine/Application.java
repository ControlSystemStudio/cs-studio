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
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse Application for CSS archive engine
 *  @author Kay Kasemir
 */
public class Application implements IApplication
{
    /** HTTP Server port */
    private int port;

    /** Request file */
    private String engine_name;

    /** Application model */
    private EngineModel model;

    /** Obtain settings from preferences and command-line arguments
     *  @param args Command-line arguments
     *  @return <code>true</code> if OK.
     */
    @SuppressWarnings("nls")
    private boolean getSettings(final String args[])
    {
        // Create the parser and run it.
        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help",
                    "Display Help");
        final IntegerOption port_opt = new IntegerOption(parser, "-port", "4812",
                    "HTTP server port", 4812);
        final StringOption engine_name_opt = new StringOption(parser,
                    "-engine", "demo_engine", "Engine config name", null);
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
            System.out.println(parser.getHelp());
            return false;
        }

        // Check arguments
        if (engine_name_opt.get() == null)
        {
            System.out.println("Missing option " + engine_name_opt.getOption());
            System.out.println(parser.getHelp());
            return false;
        }

        // Copy stuff from options into member vars.
        port = port_opt.get();
        engine_name = engine_name_opt.get();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public Object start(final IApplicationContext context) throws Exception
    {
        final String args[] =
            (String []) context.getArguments().get("application.args");
        if (!getSettings(args))
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
                    model.readConfig(config, engine_name, port);
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
