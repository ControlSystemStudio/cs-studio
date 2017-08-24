package org.csstudio.archive.engine.server;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.Activator;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.html.ChannelListResponse;
import org.csstudio.archive.engine.server.html.ChannelResponse;
import org.csstudio.archive.engine.server.html.DebugResponse;
import org.csstudio.archive.engine.server.html.DisconnectedResponse;
import org.csstudio.archive.engine.server.html.EnvironmentResponse;
import org.csstudio.archive.engine.server.html.GroupResponse;
import org.csstudio.archive.engine.server.html.GroupsResponse;
import org.csstudio.archive.engine.server.html.MainResponse;
import org.csstudio.archive.engine.server.html.ResetResponse;
import org.csstudio.archive.engine.server.html.RestartResponse;
import org.csstudio.archive.engine.server.html.StopResponse;

public class ResponseFactory extends HttpServlet {
    /** Required by Serializable */
    private static final long serialVersionUID = 1L;
    /** Model from which to serve info */
    final protected EngineModel model;

    final protected Page page;

    /** Construct <code>HttpServlet</code>
     *  @param title Page title
     */
    protected ResponseFactory(final EngineModel model, Page page)
    {
        this.page = page;
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    protected void doGet(final HttpServletRequest req,
                    final HttpServletResponse resp)
                    throws ServletException, IOException
    {
        try
        {
            final String format = req.getParameter("format");
            AbstractResponse reponseWriter;
            switch (page) {
                case MAIN:
                    reponseWriter = new MainResponse(model);
                    break;
                case CHANNEL:
                    reponseWriter = new ChannelResponse(model);
                    break;
                case CHANNEL_LIST:
                    reponseWriter = new ChannelListResponse(model);
                    break;
                case DISCONNECTED:
                    reponseWriter = new DisconnectedResponse(model);
                    break;
                case ENVIRONMENT:
                    reponseWriter = new EnvironmentResponse(model);
                    break;
                case GROUP:
                    reponseWriter = new GroupResponse(model);
                    break;
                case GROUPS:
                    reponseWriter = new GroupsResponse(model);
                    break;
                case DEBUG:
                    reponseWriter = new DebugResponse(model);
                    break;
                case RESET:
                    reponseWriter = new ResetResponse(model);
                    break;
                case RESTART:
                    reponseWriter = new RestartResponse(model);
                    break;
                case STOP:
                    reponseWriter = new StopResponse(model);
                    break;
                default:
                    reponseWriter = new NoResponse(model);

            }

            reponseWriter.fillResponse(req, resp);
        }

        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "HTTP Server exception", ex);
            if (resp.isCommitted())
                return;
            resp.sendError(400, "HTTP Server exception" + ex.getMessage());
        }
    }
}
