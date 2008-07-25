package org.csstudio.platform.httpd;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/** Helper for creating and stopping a HttpService at a known port.
 *  <p>
 *  Might have to set command-line option
 *  <pre>-Dorg.eclipse.equinox.http.jetty.autostart=false<pre>
 *  to prevent additional auto-started web server instances.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HttpServiceHelper
{
    /** @return Id used to identify the HTTP server */
    static private String createServerId(final int port)
    {
        final String pid = "HTTPD" + port;
        return pid;
    }

    /** Create HttpService
     *  @param context Bundle context (see Plugin.getBundle().getBundleContext())
     *  @param port TCP port for server
     *  @return HttpService
     *  @throws Exception on error
     *  @see #stopHttpService
     */
    static public HttpService createHttpService(final BundleContext context,
                    final int port) throws Exception,
                    InvalidSyntaxException
    {
        final String pid = createServerId(port);

        // Create a custom HttpService
        // avoid the auto-started instance
        final Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
        dictionary.put("http.port", new Integer(port));
        dictionary.put("other.info", pid);
        JettyConfigurator.startServer(pid, dictionary);

        // Locate that custom server so we can add servlets
        // (thanks to Gunnar Wagenknecht for this info)
        // Tried to use the service.pid instead of other.info,
        // but that didn't seem to work.
        final String filter =
            String.format("(&(objectClass=%s)(other.info=%s))",
                            HttpService.class.getName(),
                            pid);
        ServiceTracker http_tracker = new ServiceTracker(context,
                        context.createFilter(filter), null);
        http_tracker.open();
        final Object[] services = http_tracker.getServices();
        if (services == null)
            throw new Exception("No HttpService found");
        if (services.length != 1)
            throw new Exception("Found " + services.length
                            + " HttpServices instead of one");
        if (! (services[0] instanceof HttpService))
            throw new Exception("Got " + services[0].getClass().getName()
                            + " instead of HttpService");
        final HttpService http = (HttpService) services[0];
        
        // Don't close the tracker?
        // When closed, the HttpService seems to get
        // closed as well, at least in _some_ tests!?
        // http_tracker.close();
        
        return http;
    }

    /** Stop a HttpService that was started at given port.
     *  <p>
     *  Will only work with HttpServices that were started by
     *  <code>createHttpService</code>
     *  @port Port where the HttpService was started
     *  @throws Exception on error
     *  @see #createHttpService
     */
    static public void stopHttpService(final int port) throws Exception
    {
        JettyConfigurator.stopServer(createServerId(port));
    }
}
