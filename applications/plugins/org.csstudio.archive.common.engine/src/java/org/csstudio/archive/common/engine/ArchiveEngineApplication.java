/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.common.engine.httpserver.EngineHttpServer;
import org.csstudio.archive.common.engine.httpserver.EngineHttpServerException;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.archive.common.engine.model.EngineState;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.engine.service.ServiceProvider;
import org.csstudio.domain.desy.epics.typesupport.EpicsIMetaDataTypeSupport;
import org.csstudio.domain.desy.epics.typesupport.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.time.StopWatch;
import org.csstudio.domain.desy.time.StopWatch.RunningStopWatch;
import org.csstudio.domain.desy.time.TimeInstant;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Eclipse Application for CSS archive engine
 *  @author Kay Kasemir
 */
public class ArchiveEngineApplication implements IApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveEngineApplication.class);

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
            LOG.error("Option parse error: {}.", ex.getMessage());
            LOG.info("Option parse error: {}.", parser.getHelp());
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
        _engineName = engineNameOpt.get();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final Object start(@Nonnull final IApplicationContext context) {

        final IServiceProvider provider = new ServiceProvider();

        final String[] args = (String[]) context.getArguments().get("application.args");
        if (!getSettings(args)) {
            return EXIT_OK;
        }
        EpicsIMetaDataTypeSupport.install();
        EpicsIValueTypeSupport.install();

        LOG.info("DESY Archive Engine Version {}.", provider.getPreferencesService().getVersion());
        EngineHttpServer httpServer = null;
        try {
            _run = true;
            _model = new EngineModel(_engineName, provider);

            httpServer = startHttpServer(_model, provider);
            if (httpServer == null) {
                return EXIT_OK;
            }
            while (_run) {
                configureAndRunEngine(_model);
                stopEngineAndClearConfiguration(_model);
            }
        } catch (final EngineModelException e) {
            LOG.error("Archive engine model error - try to shutdown.", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return killEngineAndHttpServer(_model, httpServer);
    }

    private void stopEngineAndClearConfiguration(@Nonnull final EngineModel model) throws EngineModelException {
        LOG.info("Stopping engine.");
        model.stop();
        model.clearConfiguration();
    }

    @Nonnull
    private Integer killEngineAndHttpServer(@Nonnull final EngineModel model,
                                   @Nonnull final EngineHttpServer httpServer) {
        if (httpServer != null) {
            httpServer.stop();
        }
        try {
            if (model != null) {
                model.stop();
            }
        } catch (final EngineModelException e) {
            LOG.error("Stopping of the engine failed. System exit.", e);
        }
        return EXIT_OK;
    }

    /**
     * Run until model gets stopped via HTTPD or #stop()
     * @param model
     *
     * @throws EngineModelException
     * @throws InterruptedException
     */
    private void configureAndRunEngine(@Nonnull final EngineModel model) throws EngineModelException, InterruptedException {

        readEngineConfiguration(model);

        LOG.info("Running, CA addr list: {}.", System.getProperty("com.cosylab.epics.caj.CAJContext.addr_list"));

        model.start();

        while (true) {
            Thread.sleep(1000);
            if (model.getState() == EngineState.SHUTDOWN_REQUESTED) {
                _run = false;
                break;
            }
            if (model.getState() == EngineState.RESTART_REQUESTED) {
                break;
            }
        }
    }

    private void readEngineConfiguration(@Nonnull final EngineModel model) throws EngineModelException {
        LOG.info("Reading configuration for engine '{}'.", model.getName());
        final RunningStopWatch watch = StopWatch.start();
        model.readConfig();
        final long millis = watch.getElapsedTimeInMillis();
        LOG.info("Read configuration: {} channels in {}.",
                 model.getChannels().size(),
                 TimeInstant.STD_DURATION_WITH_MILLIS_FMT.print(Period.millis((int) millis)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (_model != null) {
            _model.requestStop();
        }
    }

    @CheckForNull
    private EngineHttpServer startHttpServer(@Nonnull final EngineModel model,
                                             @Nonnull final IServiceProvider provider) {
        EngineHttpServer httpServer = null;
        try {
            // Setup takes some time, but engine server should already respond.
            httpServer = new EngineHttpServer(model, provider, model.getHttpPort());
        } catch (final EngineHttpServerException e) {
            LOG.error("Cannot start HTTP server on port {}: {}", provider, e.getMessage());
        }
        return httpServer;
    }
}
