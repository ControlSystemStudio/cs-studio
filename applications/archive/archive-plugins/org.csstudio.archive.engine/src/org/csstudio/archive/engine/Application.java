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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.prefs.BackingStoreException;

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

    String[] getPrefValue(final String option) {
        if (option != null) { // Split "plugin/key=value"
            String[] pref_val = new String[2];
            final int sep = option.indexOf("=");
            if (sep >= 0) {
                pref_val[0] = option.substring(0, sep);
                pref_val[1] = option.substring(sep + 1);
            } else {
                pref_val[0] = option;
                pref_val[1] = null;
            }
            return pref_val;
        }
        return null;
    }

    /** Obtain settings from preferences and command-line arguments
     *  @param args Command-line arguments
     *  @return <code>true</code> if continue, <code>false</code> to end application
     */
    @SuppressWarnings("nls")
    private boolean getSettings(final String args[], final IApplicationContext context)
    {
        // Display configuration info
        final String version = context.getBrandingBundle().getHeaders().get("Bundle-Version");
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
        final StringOption preference_opt = new StringOption(parser, "-set_pref", "plugin.name/preference=value",
                "Set a preference for a specific plugin", null);
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
        String[] pref_val = getPrefValue(set_password_opt.get());

        if (pref_val != null)
        {
            if (pref_val[1] == null)
            {
                pref_val[1] = PasswordInput.readPassword("Value for " + pref_val[0] + ":");
            }
            try
            {
                SecurePreferences.set(pref_val[0], pref_val[1]);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return false;
        }

        for (final String pref_opt : preference_opt.getMany())
        {
            pref_val = getPrefValue(pref_opt);
            if (pref_val != null)
            {
                final String pref = pref_val[0];
                final String value = pref_val[1];

                if (value == null) {
                    System.out.println("Malformed option " + preference_opt.getOption() + " " + pref_opt);
                    return false;
                }
                final int sep = pref.indexOf('/');
                if (sep < 0) {
                    System.out.println(
                            "Malformed plugin/preference for option " + preference_opt.getOption() + " " + pref_opt);
                    return false;
                }
                final String plugin = pref.substring(0, sep);
                final String preference = pref.substring(sep + 1);

                final IEclipsePreferences pref_node = InstanceScope.INSTANCE.getNode(plugin);
                pref_node.put(preference, value);
                try {
                    pref_node.flush();
                } catch (BackingStoreException e) {
                    Activator.getLogger().log(Level.SEVERE,
                            "Could not set plugin preference " + plugin + "/" + preference, e);
                }
            }
        }

        engine_name = engine_name_opt.get();

        if (engine_name == null)
        {
            System.out.println("Must specificy engine option: " + engine_name_opt.getOption());
            System.out.println(parser.getHelp());
            return false;
        }

        // Copy stuff from options into member vars.
        port = port_opt.get();
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
        EngineModel.VERSION =  context.getBrandingBundle().getHeaders().get("Bundle-Version");
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
