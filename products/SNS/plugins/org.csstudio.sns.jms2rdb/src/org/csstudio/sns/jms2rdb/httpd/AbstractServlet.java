package org.csstudio.sns.jms2rdb.httpd;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.platform.logging.CentralLogger;

/** Base class for servlets.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class AbstractServlet extends HttpServlet
{
    /**  */
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    protected void doGet(final HttpServletRequest req,
                    final HttpServletResponse resp)
                    throws ServletException, IOException
    {
        try
        {
            final HTMLWriter html = new HTMLWriter(resp, "JMS-to-RDB Tool");
            fillBody(html);
            html.close();
        }
        catch (Exception ex)
        {
            if (resp.isCommitted())
            {
                CentralLogger.getInstance().getLogger(this).warn(
                        "HTTP Server exception", ex);
                return;
            }
            resp.sendError(400, "HTTP Server exception" + ex.getMessage());
        }
    }

    abstract protected void fillBody(HTMLWriter html);
}
