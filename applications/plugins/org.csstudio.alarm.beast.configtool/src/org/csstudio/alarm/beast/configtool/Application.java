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
import java.util.Date;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.client.AlarmConfiguration;
import org.csstudio.alarm.beast.client.AlarmConfigurationLoader;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.logging.LogConfigurator;
import org.csstudio.security.PasswordInput;
import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** AlarmConfigTool application
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    private String url, user, password, schema;
    private String root;
    private enum Mode
    {
        LIST, MODIFY, DELETE, IMPORT, EXPORT, CONVERT_ALH
    }
    private Mode mode;
    private String filename;
    private String path;

    private boolean delete;

    /** Parse arguments, set member variables */
    private String checkArguments(String[] args, IApplicationContext context)
    {
        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help",  "Display help");
        final BooleanOption version_opt = new BooleanOption(parser, "-version", "Display version info");
        final StringOption url = new StringOption(parser, "-rdb_url",
               "Alarm config database URL", Preferences.getRDB_Url());
        final StringOption user = new StringOption(parser, "-rdb_user",
                "Database user", Preferences.getRDB_User());
        final StringOption password = new StringOption(parser, "-rdb_pass",
                "Database password", Preferences.getRDB_Password());
        final StringOption set_password = new StringOption(parser,
                "-set_password", "plugin/key=value", "Set secure preferences", null);
        final StringOption schema = new StringOption(parser, "-rdb_schema",
                "Database scheme", Preferences.getRDB_Schema());
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
        final StringOption do_delete = new StringOption(parser, "-delete",
                "Component to delete (must use full path /root/area/system/element)", "");
        final BooleanOption convert_alh = new BooleanOption(parser, "-alh",
                "Convert ALH config into XML config file");
        final StringOption file = new StringOption(parser, "-file",
                "XML config file to import", "");
        parser.addEclipseParameters();
        try
        {
            parser.parse(args);
        }
        catch (final Exception ex)
        {
            return ex.getMessage() + "\n" + parser.getHelp();
        }
		final String version = (String) context.getBrandingBundle()
				.getHeaders().get("Bundle-Version");
		final String app_info = context.getBrandingName() + " " + version;
		if (help_opt.get())
			return app_info + "\n\n" + parser.getHelp();
		if (version_opt.get()) {
			// Display configuration info
			return app_info;
		}
        final String option = set_password.get();
        if (option != null)
        {   // Split "plugin/key=value"
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
                return ex.getMessage();
            }
            // Returning non-null will end the application
            return "";
        }
        
        this.url = url.get();
        this.user = (user.get() == null || user.get().isEmpty()) ? null : user.get();
        this.password = (password.get() == null || password.get().isEmpty()) ? null : password.get();
        this.schema = (schema.get() == null || schema.get().isEmpty()) ? "" : schema.get();
        if (do_list.get())
        {
            mode = Mode.LIST;
            return null;
        }

        // Except when listing configs, require root and file name
        if (root.get().isEmpty())
            return "Missing configuration root name\n" + parser.getHelp();
        this.root = root.get();
        
		if (do_delete.get().length() > 0) 
		{
			delete = true;
			path = do_delete.get();
		}
        
        if (do_export.get())
            mode = Mode.EXPORT;
        else if (do_modify.get())
            mode = Mode.MODIFY;
        else if (do_import.get())
            mode = Mode.IMPORT;
        else if (convert_alh.get())
            mode = Mode.CONVERT_ALH;
        else if (delete) 
        {
        	mode = Mode.DELETE;
        	return null;
        }
        else
            return "Specify import or export or alh conversion\n" + parser.getHelp();
        
        if (file.get().isEmpty())
            return "Missing file name\n" + parser.getHelp();
        this.filename = file.get();
        
        return null;
    }

    /** IApplication start */
    @Override
    public Object start(IApplicationContext context) throws Exception
    {
        final String version = (String)
                context.getBrandingBundle().getHeaders().get("Bundle-Version");
        
        // Create parser for arguments and run it.
        final String args[] =
            (String []) context.getArguments().get("application.args");

        final String error = checkArguments(args, context);
        if (error != null)
        {
            System.err.println(error);
            return Integer.valueOf(-2);
        }

        System.out.println("Alarm Config Tool " + version);
        // Configure logging
        LogConfigurator.configureFromPreferences();

        // Perform selected action
        try
        {
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
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	return Integer.valueOf(-1);
        }
        return EXIT_OK;
    }

    /** Dump available configuration names
     *  @throws Exception on error
     */
    private void listConfigs() throws Exception
    {
        // Connect to configuration database
        final AlarmConfiguration config;
        try
        {
            config = new AlarmConfiguration(url, user, password, schema);
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
            throw new Exception("Error listing alarm configurations", ex);
        }
        finally
        {
            config.close();
        }
    }

    /** Convert ALH config file to alarm system XML file
     *  @throws Exception on error
     */
    private void convertAlh() throws Exception
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
                "Date     : " + new Date(),
            });
            out.flush();
        }
        catch (Exception ex)
        {
        	throw new Exception("Error while converting ALH config file", ex);
        }
    }

    /** Run based on settings
     *  @throws Exception on error
     */
    private void importExport() throws Exception
    {
        // Connect to configuration database
        final AlarmConfiguration config;
        try
        {
            config = new AlarmConfiguration(url, user, password, schema, false);
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
        	throw new Exception("Error connecting to alarm config database", ex);
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
                        "Alarm configuration snapshot " + new Date(),
                        "URL : " + url,
                        "Root: " + root,
                    });
                    out.flush();
                    file.close();
                }
                break;
            case MODIFY:
                {
                	if(delete) 
                	{
        				final AlarmTreeItem item = config.getAlarmTree().getItemByPath(path);
        				if (item == null)
        					throw new Exception("Cannot locate item with path " + path);
        				System.out.println("Deleting existing RDB configuration for '" + path + "'");
        				config.remove(item);
                	}
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
            case DELETE:
	            {
	            	final AlarmTreeItem item = config.getAlarmTree().getItemByPath(path);
					if (item == null)
						throw new Exception("Cannot locate item with path " + path);
					System.out.println("Deleting existing RDB configuration for '" + path + "'");
					config.remove(item);
	            }
            break;
            default:
                throw new Exception("Unknown mode " + mode.name());
            }
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
