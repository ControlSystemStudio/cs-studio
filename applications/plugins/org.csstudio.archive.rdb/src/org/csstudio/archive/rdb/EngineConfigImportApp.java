/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.IntegerOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineHelper;
import org.csstudio.archive.rdb.engineconfig.XMLExport;
import org.csstudio.archive.rdb.engineconfig.XMLImport;
import org.csstudio.logging.LogConfigurator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse Application to import engine configuration file into RDB.
 *  @author Kay Kasemir
 */
public class EngineConfigImportApp implements IApplication
{
    /** "Main" Routine
     *  @see IApplication#start(IApplicationContext)
     */
    @Override
    @SuppressWarnings("nls")
    public Object start(final IApplicationContext context) throws Exception
    {
        // Handle command-line options
        final String args[] =
            (String []) context.getArguments().get("application.args");
        final ArgParser parser = new ArgParser();
        final BooleanOption help = new BooleanOption(parser,
                "-help", "show help");
        final StringOption filename = new StringOption(parser,
                "-config", "my_config.xml", "XML Engine config file", "");
        final StringOption  rdb_url = new StringOption(parser,
                "-rdb_url", "jdbc:...", "RDB URL", RDBArchivePreferences.getURL());
        final StringOption  user = new StringOption(parser,
                "-rdb_user", "user", "RDB User", RDBArchivePreferences.getUser());
        final StringOption  password = new StringOption(parser,
                "-rdb_password", "password", "RDB Password", RDBArchivePreferences.getPassword());
        final StringOption  engine_name = new StringOption(parser,
                "-engine", "my_engine", "Engine Name", "");
        final BooleanOption export = new BooleanOption(parser,
                "-export", "export configuration as XML");
        final StringOption  engine_description = new StringOption(parser,
                "-description", "'My Engine'", "Engine Description", "Imported");
        final StringOption  engine_host = new StringOption(parser,
                "-host", "my.host.org", "Engine Host", "localhost");
        final IntegerOption engine_port = new IntegerOption(parser,
                "-port", "4812", "Engine Port", 4812);
        final BooleanOption replace_engine = new BooleanOption(parser,
                "-replace_engine", "Replace existing engine config, or stop?");
        final BooleanOption steal_channels = new BooleanOption(parser,
                "-steal_channels", "Steal channels that are already in other engine");
        final BooleanOption delete_config = new BooleanOption(parser,
                "-delete_config", "Delete existing engine config");

        // NOTE:
        // On OS X, the application will have a file
        // EngineConfigImport.app/Contents/Info.plist
        // that includes a default option "-showlocation",
        // which the parser will see but not understand.
        // Solution for now: Remove that from Info.plist
        try
        {
            parser.parse(args);
        }
        catch (final Exception ex)
        {
            System.err.println(ex.getMessage());
            return IApplication.EXIT_OK;
        }
        if (help.get())
        {
            System.out.println(parser.getHelp());
            return IApplication.EXIT_OK;
        }
        if ((rdb_url.get() == null)  ||  (rdb_url.get().length() <= 0))
        {
            System.err.println("Missing option " + rdb_url.getOption());
            System.err.println(parser.getHelp());
            return IApplication.EXIT_OK;
        }
        if (engine_name.get().length() <= 0)
        {
            System.err.println("Missing option " + engine_name.getOption());
            System.err.println(parser.getHelp());
            return IApplication.EXIT_OK;
        }

        LogConfigurator.configureFromPreferences();

        try
        {
            if (export.get())
            {
                new XMLExport(rdb_url.get(),
                    user.get(), password.get(),
                    engine_name.get());
                return IApplication.EXIT_OK;
            }
            // Delete existing config?
            if (delete_config.get())
            {
                deleteEngine(rdb_url.get(), user.get(), password.get(),
                             engine_name.get());
                return IApplication.EXIT_OK;
            }

            final String engine_url = "http://" + engine_host.get() + ":" + engine_port.get() + "/main";

            // Dump options
            Activator.getLogger().info("Importing     : " + filename.get());
            Activator.getLogger().info("Engine        : " + engine_name.get());
            Activator.getLogger().info("Description   : " + engine_description.get());
            Activator.getLogger().info("URL           : " + engine_url);
            Activator.getLogger().info("Replace engine: " + replace_engine.get());
            Activator.getLogger().info("Steal channels: " + steal_channels.get());

            // Perform XML Import
            if (filename.get().length() <= 0)
            {
                System.err.println("Missing option " + filename.getOption());
                System.err.println(parser.getHelp());
                return IApplication.EXIT_OK;
            }
            final InputStream stream = new FileInputStream(filename.get());
            final XMLImport importer = new XMLImport(rdb_url.get(),
                    user.get(),
                    password.get(),
                    engine_name.get(),
                    engine_description.get(),
                    engine_url,
                    replace_engine.get(),
                    steal_channels.get());
            try
            {
                importer.parse(stream);
            }
            finally
            {
                importer.close();
            }
        }
        catch (final Throwable ex)
        {
            Activator.getLogger().log(Level.SEVERE, "Exception", ex);
        }
        return IApplication.EXIT_OK;
    }

    /** Delete existing engine config
     *  @param rdb_url
     *  @param user
     *  @param password
     *  @param engine_name
     */
    @SuppressWarnings("nls")
    private void deleteEngine(final String rdb_url,  final String user,
            final String password, final String engine_name) throws Exception
    {
        final RDBArchive archive = RDBArchive.connect(rdb_url, user, password);
        try
        {
            final SampleEngineHelper engines =
                new SampleEngineHelper(archive);
            final SampleEngineConfig engine = engines.find(engine_name);
            if (engine == null)
                Activator.getLogger().log(Level.WARNING, "Engine {0} not found", engine_name);
            else
            {
                Activator.getLogger().log(Level.INFO, "Deleting Engine {0}", engine_name);
                engines.deleteEngineInfo(engine, true);
            }
        }
        finally
        {
            archive.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        // NOP
    }
}
