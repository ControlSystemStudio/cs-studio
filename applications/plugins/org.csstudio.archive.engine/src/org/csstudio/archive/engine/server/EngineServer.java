package org.csstudio.archive.engine.server;

import org.csstudio.archive.engine.Activator;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/** Web server for the engine.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineServer
{
    /** TCP port used by the web server */
    final private int port;
    
    /** Construct and start the server
     *  @param model Model to serve
     *  @param port TCP port
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public EngineServer(final EngineModel model,
                        final int port) throws Exception
    {
        this.port = port;
        final BundleContext context =
            Activator.getDefault().getBundle().getBundleContext();
        final HttpService http = HttpServiceHelper.createHttpService(context, port);
        
        final HttpContext http_context = http.createDefaultHttpContext();
        http.registerResources("/", "/webroot", http_context);
        
        http.registerServlet("/main", new MainResponse(model), null, http_context);
        http.registerServlet("/groups", new GroupsResponse(model), null, http_context);
        http.registerServlet("/group", new GroupResponse(model), null, http_context);
        http.registerServlet("/channel", new ChannelResponse(model), null, http_context);
        http.registerServlet("/environment", new EnvironmentResponse(model), null, http_context);
        http.registerServlet("/restart", new RestartResponse(model), null, http_context);
        http.registerServlet("/reset", new ResetResponse(model), null, http_context);
        http.registerServlet("/stop", new StopResponse(model), null, http_context);
        
        Activator.getLogger().info("Engine HTTP Server port " + port);
    }

    /** Stop the server */
    public void stop()
    {
        try
        {
            HttpServiceHelper.stopHttpService(port);
        }
        catch (Exception ex)
        {
            Activator.getLogger().warn(ex);
        }
    }
}
