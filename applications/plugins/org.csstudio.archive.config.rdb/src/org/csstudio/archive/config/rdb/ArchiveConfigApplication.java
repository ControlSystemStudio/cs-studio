/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.IntegerOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ArchiveConfigFactory;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.rdb.Activator;
import org.csstudio.logging.LogConfigurator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** [Headless] RCP application for a command-line archive config tool
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveConfigApplication implements IApplication
{
    @Override
	public Object start(final IApplicationContext context) throws Exception
	{
        // Handle command-line options
        final String args[] =
            (String []) context.getArguments().get("application.args");

        final ArgParser parser = new ArgParser();
        final BooleanOption help = new BooleanOption(parser,
                "-help", "show help");
        final StringOption  engine_name = new StringOption(parser,
                "-engine", "my_engine", "Engine Name", "");
        final StringOption filename = new StringOption(parser,
                "-config", "my_config.xml", "XML Engine config file", "");
        final BooleanOption do_export = new BooleanOption(parser,
                "-export", "export configuration as XML");
        final BooleanOption do_import = new BooleanOption(parser,
                "-import", "import configuration from XML");
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
        if (engine_name.get().length() <= 0)
        {
            System.err.println("Missing option " + engine_name.getOption());
            System.err.println(parser.getHelp());
            return IApplication.EXIT_OK;
        }
        
        LogConfigurator.configureFromPreferences();

        try
        {
            if (do_export.get())
            {
            	final PrintStream out;
            	if (filename.get().isEmpty())
            		out = System.out;
            	else
            	{
            		out = new PrintStream(filename.get());
            		System.out.println("Exporting config for engine " + engine_name.get()
            				+ " to " + filename.get());
            	}
                new XMLExport().export(out, engine_name.get());
                if (out != System.out)
                	out.close();
                return IApplication.EXIT_OK;
            }
            else if (do_import.get())
            {
            	if (filename.get().isEmpty())
            	{
                    System.err.println("Missing option " + filename.getOption());
                    System.err.println(parser.getHelp());
                    return IApplication.EXIT_OK;
            	}
                final String engine_url = "http://" + engine_host.get() + ":" + engine_port.get() + "/main";
                System.out.println("Importing     : " + filename.get());
                System.out.println("Engine        : " + engine_name.get());
                System.out.println("Description   : " + engine_description.get());
                System.out.println("URL           : " + engine_url);
                System.out.println("Replace engine: " + replace_engine.get());
                System.out.println("Steal channels: " + steal_channels.get());
                final InputStream stream = new FileInputStream(filename.get());
//                final XMLImport importer = new XMLImport(engine_name.get(),
//                    engine_description.get(),
//                    engine_url,
//                    replace_engine.get(),
//                    steal_channels.get());
//            	importer.parse(stream);
            }	
        }
        catch (final Throwable ex)
        {
        	Logger.getLogger(Activator.ID).log(Level.SEVERE, "Exception", ex);
        }
        return IApplication.EXIT_OK;
	}

	@Override
	public void stop()
	{
		// Ignored
	}
}
