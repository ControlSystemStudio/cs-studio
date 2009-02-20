package org.csstudio.archive.rdb;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.IntegerOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineHelper;
import org.csstudio.archive.rdb.engineconfig.XMLImport;
import org.csstudio.archive.rdb.internal.RDBArchiveImpl;
import org.csstudio.platform.logging.CentralLogger;
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
                "-rdb_url", "RDB URL", RDBArchivePreferences.getURL());
        final StringOption  user = new StringOption(parser,
                "-rdb_user", "RDB User", RDBArchivePreferences.getUser());
        final StringOption  password = new StringOption(parser,
                "-rdb_password", "RDB Password", RDBArchivePreferences.getPassword());
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
        if (rdb_url.get() == null  ||  rdb_url.get().length() <= 0)
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
        
        final Logger logger = CentralLogger.getInstance().getLogger(this);
        try
        {
            // Delete existing config?
            if (delete_config.get())
            {
                deleteEngine(rdb_url.get(), user.get(), password.get(),
                             engine_name.get());
                return IApplication.EXIT_OK;
            }

            final URL engine_url =
                new URL("http://" + engine_host.get() + ":" + engine_port.get() + "/main");

            // Dump options
            logger.info("Importing     : " + filename.get());
            logger.info("Engine        : " + engine_name.get());
            logger.info("Description   : " + engine_description.get());
            logger.info("URL           : " + engine_url);
            logger.info("Replace engine: " + replace_engine.get());
            logger.info("Steal channels: " + steal_channels.get());
            
            // Perform XML Import
            if (filename.get().length() <= 0)
            {
                System.err.println("Missing option " + filename.getOption());
                System.err.println(parser.getHelp());
                return IApplication.EXIT_OK;
            }
            final InputStream stream = new FileInputStream(filename.get());
            final XMLImport importer = new XMLImport(rdb_url.get(),
                    user.get(), password.get(),
                    engine_name.get(), engine_description.get(), engine_url,
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
                logger.fatal(error, ex);
            else
                logger.fatal(ex);
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
        final RDBArchiveImpl archive = new RDBArchiveImpl(rdb_url, user, password);
        try
        {
            final SampleEngineHelper engines =
                new SampleEngineHelper(archive);
            final SampleEngineConfig engine = engines.find(engine_name);
            if (engine == null)
                CentralLogger.getInstance().getLogger(this).warn(engine_name + " not found");
            else
            {
                CentralLogger.getInstance().getLogger(this).info("Deleting " + engine);
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
