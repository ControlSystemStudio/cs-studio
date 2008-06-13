package org.csstudio.platform.httpd;

import javax.servlet.ServletException;

import org.csstudio.platform.httpd.servlets.ServerTest;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/** Plugin Activator.
 *  <p>
 *  When <code>demo_port</code> is set, it registers a demo web server.
 *  Otherwise it doesn't do anything useful.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator extends Plugin
{
    /** Set to >0 at compile time to include demo server */
    final private static int demo_port = 0 /* 9005 */;
    
    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        if (demo_port > 0)
        {
            try
            {
                final HttpService http =
                    HttpServiceHelper.createHttpService(context, demo_port);
                configureHttpService(http);
                System.out.println("Try these URLs from web browser:");
                System.out.println("http://localhost:" + demo_port + "/test.html");
                System.out.println("http://localhost:" + demo_port + "/hello");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        if (demo_port > 0)
            HttpServiceHelper.stopHttpService(demo_port);
        super.stop(context);
    }

    /** Register resources and servlet */
    private void configureHttpService(final HttpService http)
                    throws NamespaceException, ServletException
    {
        final HttpContext http_context = http.createDefaultHttpContext();
        http.registerResources("/", "/webroot", http_context);
        http.registerServlet("/hello", new ServerTest(), null, http_context);
    }
}
