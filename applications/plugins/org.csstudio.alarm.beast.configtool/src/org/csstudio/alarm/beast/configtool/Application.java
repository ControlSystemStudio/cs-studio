/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.configtool;

import java.io.FileWriter;
import java.io.PrintWriter;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.client.AlarmConfiguration;
import org.csstudio.alarm.beast.client.AlarmConfigurationLoader;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.logging.LogConfigurator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** AlarmConfigTool application
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    private String url, user, password;
    private String root;
    private enum Mode
    {
        LIST, MODIFY, IMPORT, EXPORT, CONVERT_ALH
    }
    private Mode mode;
    private String filename;

    /** Parse arguments, set member variables */
    private String checkArguments(String[] args)
    {
        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help",
               "Display Help");
        final StringOption url = new StringOption(parser, "-rdb_url",
               "Alarm config database URL", Preferences.getRDB_Url());
        final StringOption user = new StringOption(parser, "-rdb_user",
                "Database user", Preferences.getRDB_User());
        final StringOption password = new StringOption(parser, "-rdb_pass",
                "Database password", Preferences.getRDB_Password());
        final BooleanOption do_list = new BooleanOption(parser, "-list",
                "List available configurations");
        final StringOption root = new StringOption(parser, "-root",
                "Alarm configuraton name (root element)", Preferences.getAlarmTreeRoot());
        final BooleanOption do_export = new BooleanOption(parser, "-export",
                "Export XML config file");
        final BooleanOption do_modify = new BooleanOption(parser, "-modify",
                "Modify existing config with imported XML file");
        final BooleanOption do_import = new BooleanOption(parser, "-import",
                "Import XML config file (replace existing config)");
        final BooleanOption convert_alh = new BooleanOption(parser, "-alh",
                "Convert ALH config into XML config file");
        final StringOption file = new StringOption(parser, "-file",
                "XML config file to import", "");
        // Options handled by Eclipse,
        // but this way they show up in the help message
        new StringOption(parser, "-pluginCustomization",
                        "Eclipse plugin defaults", null);
        new StringOption(parser, "-data", "Eclipse workspace location", null);
        try
        {
            parser.parse(args);
        }
        catch (final Exception ex)
        {
            return ex.getMessage() + "\n" + parser.getHelp();
        }
        if (help_opt.get())
            return parser.getHelp();

        this.url = url.get();
        this.user = user.get().isEmpty() ? null : user.get();
        this.password = password.get().isEmpty() ? null : password.get();
        if (do_list.get())
        {
            mode = Mode.LIST;
            return null;
        }

        // Except when listing configs, require root and file name
        if (root.get().isEmpty())
            return "Missing configuration root name\n" + parser.getHelp();
        this.root = root.get();

        if (file.get().isEmpty())
            return "Missing file name\n" + parser.getHelp();
        this.filename = file.get();

        if (do_export.get())
            mode = Mode.EXPORT;
        else if (do_modify.get())
            mode = Mode.MODIFY;
        else if (do_import.get())
            mode = Mode.IMPORT;
        else if (convert_alh.get())
            mode = Mode.CONVERT_ALH;
        else
            return "Specify import or export or alh conversion\n" + parser.getHelp();
        return null;
    }

    /** IApplication start */
    @Override
    public Object start(IApplicationContext context) throws Exception
    {
        System.out.println("Alarm Config Tool");
        // Create parser for arguments and run it.
        final String args[] =
            (String []) context.getArguments().get("application.args");

        final String error = checkArguments(args);
        if (error != null)
        {
            System.err.println(error);
            return EXIT_OK;
        }

        // Configure logging
        LogConfigurator.configureFromPreferences();

        // Perform selected action
        switch (mode)
        {
        case LIST:
            listConfigs();
            break;
        case CONVERT_ALH:
            convertAlh();
            break;
        default:
            importExport();
        }
        return EXIT_OK;
    }

    /** Dump available configuration names */
    private void listConfigs()
    {
        // Connect to configuration database
        final AlarmConfiguration config;
        try
        {
            config = new AlarmConfiguration(url, user, password);
        }
        catch (Exception ex)
        {
            System.err.println("Error connecting to alarm config database: " +
                    ex.getMessage());
            return;
        }
        // List configs
        try
        {
            final String[] configs = config.listConfigurations();
            for (String name : configs)
                System.out.println("'" + name + "'");
        }
        catch (Exception ex)
        {
            System.err.println("Error listing alarm configurations: " +
                    ex.getMessage());
        }
        finally
        {
            config.close();
        }
    }

    /** Convert ALH config file to alarm system XML file */
    private void convertAlh()
    {
        try
        {
            final ALHConverter converter = new ALHConverter(filename);
            final PrintWriter out = new PrintWriter(System.out);
            final AlarmTreeRoot config = converter.getAlarmTree();
            config.writeXML(out, new String[]
            {
                "Generated from ALH config file",
                "File name: '" + filename + "'",
                "Date     : " + TimestampFactory.now(),
            });
            out.flush();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** Run based on settings */
    private void importExport()
    {
        // Connect to configuration database
        final AlarmConfiguration config;
        try
        {
            config = new AlarmConfiguration(url, user, password, false);
            System.out.println("Reading RDB configuration of '" + root + "'");
            config.readConfiguration(root, mode == Mode.IMPORT, new NullProgressMonitor()
			{
				@Override
				public void beginTask(final String name, final int totalWork)
				{
					System.out.println(name);
				}
				
				@Override
				public void subTask(final String name)
				{
					System.out.println(name);
				}
				@Override
				public void done()
				{
					System.out.println("Done.");
				}
			});
        }
        catch (Exception ex)
        {
            System.err.println("Error connecting to alarm config database: " +
                    ex.getMessage());
            return;
        }

        try
        {
            switch (mode)
            {
            case EXPORT:
                {
                    System.out.println("Writing configuration '" + root + "' to " + filename);
                    final FileWriter file = new FileWriter(filename);
                    final PrintWriter out = new PrintWriter(file);
                    config.getAlarmTree().writeXML(out, new String[]
                    {
                        "Alarm configuration snapshot " + TimestampFactory.now(),
                        "URL : " + url,
                        "Root: " + root,
                    });
                    out.flush();
                    file.close();
                }
                break;
            case MODIFY:
                {
                    System.out.println("Modifying configuration '" + root + "' from " + filename);
                    new AlarmConfigurationLoader(config).load(filename);
                }
                break;
            case IMPORT:
                {
                    System.out.println("Deleting existing RDB configuration for '" + root + "'");
                    config.removeAllItems();
                    System.out.println("Importing configuration '" + root + "' from " + filename);
                    new AlarmConfigurationLoader(config).load(filename);
                }
                break;
            default:
                throw new Exception("Mode " + mode.name());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            config.close();
        }
    }

    /** IApplication stop */
    @Override
    public void stop()
    {
        // Ignored
    }
}
