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

import org.csstudio.alarm.beast.AlarmConfiguration;
import org.csstudio.alarm.beast.AlarmConfigurationLoader;
import org.csstudio.alarm.beast.AlarmTreeRoot;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** AlarmConfigTool application
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    private String url;
    private String root;
    private enum Mode
    {
        MODIFY, IMPORT, EXPORT, CONVERT_ALH
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
        final StringOption root = new StringOption(parser, "-root",
                "Alarm config root element", Preferences.getAlarmTreeRoot());
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
        if (file.get().length() <= 0)
            return "Missing file name\n" + parser.getHelp();

        this.url = url.get();
        this.root = root.get();
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
        this.filename = file.get();
        return null;
    }

    /** IApplication start */
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
        
        if (mode == Mode.CONVERT_ALH)
            convertAlh();
        else
            importExport();
        return EXIT_OK;
    }
    
    private void convertAlh()
    {
        try
        {
            final ALHConverter converter = new ALHConverter(filename);
            final PrintWriter out = new PrintWriter(System.out);
            out.println("<!-- Generated from ALH config file");
            out.println(" -   File name: '" + filename + "'");
            out.println(" -->");
            final AlarmTreeRoot config = converter.getAlarmTree();
            config.writeXML(out);
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
        AlarmConfiguration config;
        try
        {
            config = new AlarmConfiguration(url, root, mode == Mode.IMPORT);
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
                    out.println("<!-- Alarm configuration snapshot");
                    out.println("     URL : " + url);
                    out.println("     Root: " + root);
                    out.println("  -->");
                    config.getAlarmTree().writeXML(out);
                    out.flush();
                    file.close();
                }
                break;
            case MODIFY:
                {
                    System.out.println("Modifying configuration '" + root + "' from " + filename);
                    new AlarmConfigurationLoader(config, filename);
                }
                break;
            case IMPORT:
                {
                    System.out.println("Importing configuration '" + root + "' from " + filename);
                    config.removeAllItems();
                    new AlarmConfigurationLoader(config, filename);
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
    public void stop()
    {
        // Ignored
    }
}
