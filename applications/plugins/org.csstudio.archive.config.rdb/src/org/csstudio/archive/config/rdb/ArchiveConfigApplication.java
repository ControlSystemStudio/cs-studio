/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ArchiveConfigFactory;
import org.csstudio.archive.config.EngineConfig;
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
        final BooleanOption export = new BooleanOption(parser,
                "-export", "export configuration as XML");
		
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
            if (export.get())
            {
                new XMLExport().export(engine_name.get());
                return IApplication.EXIT_OK;
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
