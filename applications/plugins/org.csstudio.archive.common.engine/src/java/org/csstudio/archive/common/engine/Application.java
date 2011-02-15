/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.IntegerOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.archive.common.engine.server.EngineHttpServerException;
import org.csstudio.archive.common.engine.server.EngineServer;
import org.csstudio.archive.common.engine.types.ArchiveEngineTypeSupport;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse Application for CSS archive engine
 *  @author Kay Kasemir
 */
public class Application implements IApplication {
    private static final Logger LOG = CentralLogger.getInstance().getLogger(Application.class);

    /** HTTP Server port */
    private int _port;

    /** Request file */
    private String _engineName;

    /** Engine model */
    private EngineModel _model;

    private volatile boolean _run = true;

    /** Obtain settings from preferences and command-line arguments
     *  @param args Command-line arguments
     *  @return <code>true</code> if OK.
     */
    @SuppressWarnings({ "nls", "unused" })
    private boolean getSettings(@Nonnull final String[] args) {
        // Create the parser and run it.
        final ArgParser parser = new ArgParser();
        final BooleanOption helpOpt =
            new BooleanOption(parser, "-help", "Display Help");
        final IntegerOption portOpt =
            new IntegerOption(parser, "-port", "4812", "HTTP server port", 4812);
        final StringOption engineNameOpt =
            new StringOption(parser, "-engine", "demo_engine", "Engine config name", null);
        // Options handled by Eclipse,
        // but this way they show up in the help message
        new StringOption(parser, "-pluginCustomization", "/path/to/mysettings.ini",
                        "Eclipse plugin defaults", null);
        new StringOption(parser, "-data", "/home/fred/Workspace", "Eclipse workspace location", null);
        try {
            parser.parse(args);
        } catch (final Exception ex) { // Bad options
            LOG.error("Option parse error: " + ex.getMessage(), ex);
            LOG.info("Option parse error: " + parser.getHelp());
            return false;
        }
        if (helpOpt.get()) {   // Help requested
            LOG.info(parser.getHelp());
            return false;
        }

        // Check arguments
        if (engineNameOpt.get() == null) {
            LOG.info("Missing option " + engineNameOpt.getOption());
            LOG.info(parser.getHelp());
            return false;
        }

        // Copy stuff from options into member vars.
        _port = portOpt.get();
        _engineName = engineNameOpt.get();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("nls")
    public Object start(@Nonnull final IApplicationContext context) {
        final String[] args = (String[]) context.getArguments().get("application.args");
        if (!getSettings(args)) {
            return EXIT_OK;
        }
        // Install the type supports for these engines
        ArchiveEngineTypeSupport.install();

        // Setup groups, channels, writer
        // This is all single-threaded!
        LOG.info("Archive Engine " + EngineModel.VERSION);
        _run = true;
        try {
            _model = new EngineModel();
            // Setup takes some time, but engine server should already respond.
            final EngineServer httpServer = new EngineServer(_model, _port);

            while (_run) {
                LOG.info("Reading configuration for engine '" + _engineName + "'");
                final BenchmarkTimer timer = new BenchmarkTimer();

                _model.readConfig(_engineName, _port);

                timer.stop();
                LOG.info("Read configuration: " + _model.getChannels().size() + " channels in " + timer.toString());

                // Run until model gets stopped via HTTPD or #stop()
                LOG.info("Running, CA addr list: " + System.getProperty("com.cosylab.epics.caj.CAJContext.addr_list"));
                _model.start();

                while (true) {
                    Thread.sleep(1000);
                    if (_model.getState() == EngineModel.State.SHUTDOWN_REQUESTED) {
                        _run = false;
                        break;
                    }
                    if (_model.getState() == EngineModel.State.RESTART_REQUESTED) {
                        break;
                    }
                }
                // Stop sampling
                LOG.info("ArchiveEngine ending");
                _model.stop();
                _model.clearConfig();
            }

            LOG.info("ArchiveEngine stopped");
            httpServer.stop();

        } catch (final EngineHttpServerException e) {
            LOG.fatal("Cannot start server on port " + _port + ": " + e.getMessage(), e);
            return EXIT_OK;
        } catch (final EngineModelException e) {
            LOG.error("Archive engine model error - try to shutdown.", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return EXIT_OK;
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        if (_model != null) {
            _model.requestStop();
        }
    }
}
