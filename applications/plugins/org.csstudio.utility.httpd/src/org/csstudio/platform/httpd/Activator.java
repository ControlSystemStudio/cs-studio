package org.csstudio.platform.httpd;

import javax.servlet.ServletException;

import org.csstudio.platform.httpd.servlets.ServerTest;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/** TODO Turn into pure utility, don't start any web server in here
 */
@SuppressWarnings("nls")
public class Activator extends Plugin
{
    // The plug-in ID
    public static final String PLUGIN_ID = "HTTPD"; //$NON-NLS-1$

    final private static int port = 9005;

    
    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        try
        {
            final HttpService http =
                HttpServiceHelper.createHttpService(context, port);
            configureHttpService(http);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        System.out.println("HTTPD stop");
        HttpServiceHelper.stopHttpService(port);
        super.stop(context);
    }

    private void configureHttpService(final HttpService http)
                    throws NamespaceException, ServletException
    {
        final HttpContext http_context = http.createDefaultHttpContext();
        http.registerResources("/", "/webroot", http_context);
        http.registerServlet("/hello", new ServerTest(), null, http_context);
    }
}
