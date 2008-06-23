package org.csstudio.archive.engine;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.IntegerOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.EngineServer;
import org.csstudio.archive.rdb.RDBArchive;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Demo of a non-UI archive engine CSS application.
 *  
 *  @author Kay Kasemir
 */
public class Application implements IApplication
{
    /** Database URL */
    private String url;
    
    /** HTTP Server port */
    private int port;
    
    /** Request file */
    private String engine_name;
    
    /** Application model */
    private EngineModel model;

    /** Obtain settings from preferences and command-line arguments
     *  @param args Command-line arguments
     *  @return <code>true</code> if OK.
     */
    @SuppressWarnings("nls")
    private boolean getSettings(final String args[])
    {
        // Read database URL from preferences
        final IPreferencesService prefs = Platform.getPreferencesService();
        url = prefs.getString(Activator.ID, "url", null, null);
        
        // Create the parser and run it.
        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help",
                        "Display Help");
        final IntegerOption port_opt = new IntegerOption(parser, "-port",
                        "HTTP server port", 4812);
        final StringOption url_opt = new StringOption(parser, "-url",
                        "Database URL, overrides preference setting", this.url);
        final StringOption engine_name_opt = new StringOption(parser,
                        "-engine_name", "Engine config name", null);
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
        {   // Bad options
            System.out.println(ex.getMessage());
            System.out.println(parser.getHelp());
            return false;
        }
        if (help_opt.get())
        {   // Help requested
            System.out.println(parser.getHelp());
            return false;
        }
        
        // Check arguments
        if (engine_name_opt.get() == null)
        {
            System.out.println("Missing option " + engine_name_opt.getOption());
            System.out.println(parser.getHelp());
            return false;
        }

        // Copy stuff from options into member vars.
        url = url_opt.get();
        port = port_opt.get();
        engine_name = engine_name_opt.get();
        return true;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    public Object start(final IApplicationContext context) throws Exception
    {
        final String args[] =
            (String []) context.getArguments().get("application.args");
        if (!getSettings(args))
            return EXIT_OK;
        
        if (url == null)
        {
            System.out.print("No Database URL. Set via preferences or command-line");
            return EXIT_OK;
        }

        // Setup groups, channels, writer
        // This is all single-threaded!
        Activator.getLogger().info("Archive Engine " + EngineModel.VERSION);
        try
        {
            RDBArchive archive;
            try
            {
                archive = RDBArchive.connect(url);
            }
            catch (final Exception ex)
            {
                Activator.getLogger().fatal("Cannot connect to " + url, ex);
                return EXIT_OK;
            }
            model = new EngineModel(archive);
            // Setup takes some time, but engine server should already respond.
            EngineServer server;
            try
            {
                server = new EngineServer(model, port);
            }
            catch (final Exception ex)
            {
                Activator.getLogger().fatal("Cannot start server on port port"
                                + port + ": " + ex.getMessage(), ex);
                return EXIT_OK;
            }
            
            boolean run = true;
            while (run)
            {
                try
                {
                    model.readConfig(engine_name, port);
                }
                catch (final Exception ex)
                {
                    Activator.getLogger().fatal(ex.getMessage());
                    return EXIT_OK;
                }
        
                // Run until model gets stopped (HTTPD or #stop)
                Activator.getLogger().info("Running, CA addr list: "
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
                Activator.getLogger().info("ArchiveEngine ending");
                model.stop();
                model.clearConfig();
            }
            
            Activator.getLogger().info("ArchiveEngine stopped");
            server.stop();
        }
        catch (Exception ex)
        {
            Activator.getLogger().fatal(ex);
        }
        
        return EXIT_OK;
    }

    /** {@inheritDoc} */
    public void stop()
    {
        if (model != null)
            model.requestStop();
    }
}
