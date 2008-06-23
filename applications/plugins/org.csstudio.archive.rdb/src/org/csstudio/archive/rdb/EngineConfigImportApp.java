package org.csstudio.archive.rdb;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.IntegerOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineHelper;
import org.csstudio.archive.rdb.engineconfig.XMLImport;
import org.csstudio.archive.rdb.internal.RDBArchiveImpl;
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
    @SuppressWarnings("nls")
    public Object start(IApplicationContext context) throws Exception
    {
        // Handle command-line options
        final String args[] =
            (String []) context.getArguments().get("application.args");
        final ArgParser parser = new ArgParser();
        final BooleanOption help = new BooleanOption(parser,
                "-help", "show help");
        final StringOption filename = new StringOption(parser,
                "-config", "XML Engine config file", "");
        final StringOption  rdb_url = new StringOption(parser,
                "-rdb", "RDB URL", TestSetup.URL);
        final StringOption  engine_name = new StringOption(parser,
                "-engine", "Engine Name", "");
        final StringOption  engine_description = new StringOption(parser,
                "-description", "Engine Description", "Imported");
        final StringOption  engine_host = new StringOption(parser,
                "-host", "Engine Host", "localhost");
        final IntegerOption engine_port = new IntegerOption(parser,
                "-port", "Engine Port", 4812);
        final BooleanOption replace_engine = new BooleanOption(parser,
                "-replace_engine", "Replace existing engine config, or stop?");
        final BooleanOption steal_channels = new BooleanOption(parser,
                "-steal_channels", "Steal channels that are already in other engine");
        final BooleanOption delete_config = new BooleanOption(parser,
                "-delete_config", "Delete existing engine config");
        
        try
        {
            parser.parse(args);
        }
        catch (Exception ex)
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
        
        try
        {
            // Delete existing config?
            if (delete_config.get())
            {
                deleteEngine(rdb_url.get(), engine_name.get());
                return IApplication.EXIT_OK;
            }

            final URL engine_url =
                new URL("http://" + engine_host.get() + ":" + engine_port.get());

            // Dump options
            RDBPlugin.getLogger().info("Importing     : " + filename.get());
            RDBPlugin.getLogger().info("Engine        : " + engine_name.get());
            RDBPlugin.getLogger().info("Description   : " + engine_description.get());
            RDBPlugin.getLogger().info("URL           : " + engine_url);
            RDBPlugin.getLogger().info("Replace engine: " + replace_engine.get());
            RDBPlugin.getLogger().info("Steal channels: " + steal_channels.get());
            
            // Perform XML Import
            if (filename.get().length() <= 0)
            {
                System.err.println("Missing option " + filename.getOption());
                System.err.println(parser.getHelp());
                return IApplication.EXIT_OK;
            }
            final InputStream stream = new FileInputStream(filename.get());
            final XMLImport importer = new XMLImport(rdb_url.get(), engine_name
                    .get(), engine_description.get(), engine_url,
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
            final String error = ex.getMessage();
            if (error != null  &&  error.length() > 0)
                RDBPlugin.getLogger().fatal(error, ex);
            else
                RDBPlugin.getLogger().fatal(ex);
        }
        return IApplication.EXIT_OK;
    }

    /** Delete existing engine config */
    @SuppressWarnings("nls")
    private void deleteEngine(final String rdb_url,
            final String engine_name) throws Exception
    {
        final RDBArchiveImpl archive = new RDBArchiveImpl(rdb_url);
        try
        {
            final SampleEngineHelper engines =
                new SampleEngineHelper(archive);
            final SampleEngineConfig engine = engines.find(engine_name);
            if (engine == null)
                RDBPlugin.getLogger().warn(engine_name + " not found");
            else
            {
                RDBPlugin.getLogger().info("Deleting " + engine);
                engines.deleteEngineInfo(engine);
            }
        }
        finally
        {
            archive.close();
        }
    }

    /** {@inheritDoc} */
    public void stop()
    {
        // NOP
    }
}
