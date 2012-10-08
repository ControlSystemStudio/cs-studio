/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;

import org.csstudio.archive.common.engine.ArchiveEngineActivator;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Web server for the engine.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineHttpServer {
    private static final String EX_MSG = "Engine HTTP server could not be instantiated.";

    private static final Logger LOG = LoggerFactory.getLogger(EngineHttpServer.class);

    private String _pid;

    private ServiceTracker _httpTracker;

    /** Construct and start the server
     *  @param model Model to serve
     *  @param provider TCP port
     * @throws EngineHttpServerException
     * @throws InvalidSyntaxException
     * @throws
     *  @throws Exception on error
     */
    public EngineHttpServer(@Nonnull final EngineModel model,
                            @Nonnull final IServiceProvider provider) throws EngineHttpServerException {
        final BundleContext context =
            ArchiveEngineActivator.getDefault().getBundle().getBundleContext();
        HttpService httpService;
        try {
            final Integer port = model.getHttpPort();
            if (port == null) {
                throw new EngineHttpServerException("Port is not present in model. HTTP server couldn't be started.", null);
            }
            httpService = createHttpService(context, port);

            createContextAndRegisterServlets(model, provider, httpService);

            LOG.info("Engine HTTP Server port: {}", port);

        } catch (final InvalidSyntaxException e) {
            throw new EngineHttpServerException(EX_MSG, e);
        } catch (final ServletException e) {
            throw new EngineHttpServerException(EX_MSG, e);
        } catch (final NamespaceException e) {
            throw new EngineHttpServerException(EX_MSG, e);
        } catch (final Exception e) {
            throw new EngineHttpServerException(EX_MSG, e);
        }
    }


    private void createContextAndRegisterServlets(@Nonnull final EngineModel model,
                                                  @Nonnull final IServiceProvider provider,
                                                  @Nonnull final HttpService httpService) throws NamespaceException, ServletException {
        final HttpContext httpContext = httpService.createDefaultHttpContext();
        httpService.registerResources("/", "/webroot", httpContext);

        registerEngineAdministrationServlets(model, provider, httpService, httpContext);

        registerGroupsServlets(model, provider, httpService, httpContext);

        registerChannelServlets(model, provider, httpService, httpContext);

        registerDebugAndInfoServlets(model, provider, httpService, httpContext);
    }


    private void registerDebugAndInfoServlets(@Nonnull final EngineModel model,
                                              @Nonnull final IServiceProvider provider,
                                              @Nonnull final HttpService httpService,
                                              @Nonnull final HttpContext httpContext)
                                              throws ServletException,
                                                     NamespaceException {
        final String adminParamKey = provider.getPreferencesService().getHttpAdminKey();
        httpService.registerServlet(EnvironmentResponse.baseUrl(),
                                    new EnvironmentResponse(model), null, httpContext);
        httpService.registerServlet(HelpResponse.baseUrl(),
                                    new HelpResponse(model, adminParamKey), null, httpContext);
    }


    private void registerEngineAdministrationServlets(@Nonnull final EngineModel model,
                                                      @Nonnull final IServiceProvider provider,
                                                      @Nonnull final HttpService httpService,
                                                      @Nonnull final HttpContext httpContext) throws ServletException,
                                                                                    NamespaceException {
        final String version = provider.getPreferencesService().getVersion();
        final String adminParamKey = provider.getPreferencesService().getHttpAdminKey();
        final String adminParamValue = provider.getPreferencesService().getHttpAdminValue();

        httpService.registerServlet(MainResponse.baseUrl(), new MainResponse(model, version),
                                    null, httpContext);
        httpService.registerServlet(RestartResponse.baseUrl(), new RestartResponse(model, adminParamKey, adminParamValue),
                                    null, httpContext);
        httpService.registerServlet(ResetResponse.baseUrl(), new ResetResponse(model, adminParamKey, adminParamValue),
                                    null, httpContext);
        httpService.registerServlet(ShutdownResponse.baseUrl(), new ShutdownResponse(model, adminParamKey, adminParamValue),
                                    null, httpContext);
        httpService.registerServlet(ImportResponse.baseUrl(), new ImportResponse(model, adminParamKey, adminParamValue),
                                    null, httpContext);

    }


    private void registerGroupsServlets(@Nonnull final EngineModel model,
                                        @Nonnull final IServiceProvider provider,
                                        @Nonnull final HttpService httpService,
                                        @Nonnull final HttpContext httpContext) throws ServletException,
                                                                                       NamespaceException {
        final String adminParamKey = provider.getPreferencesService().getHttpAdminKey();
        final String adminParamValue = provider.getPreferencesService().getHttpAdminValue();

        httpService.registerServlet(GroupsResponse.baseUrl(),
                                    new GroupsResponse(model), null, httpContext);
        httpService.registerServlet(ShowGroupResponse.baseUrl(),
                                    new ShowGroupResponse(model), null, httpContext);
        httpService.registerServlet(StartGroupResponse.baseUrl(),
                                    new StartGroupResponse(model), null, httpContext);
        httpService.registerServlet(StopGroupResponse.baseUrl(), new StopGroupResponse(model, adminParamKey, adminParamValue),
                                    null, httpContext);
        httpService.registerServlet(AddGroupResponse.baseUrl(),
                                    new AddGroupResponse(model), null, httpContext);
    }


    private void registerChannelServlets(@Nonnull final EngineModel model,
                                         @Nonnull final IServiceProvider provider,
                                         @Nonnull final HttpService httpService,
                                         @Nonnull final HttpContext httpContext)
                                         throws ServletException,
                                                NamespaceException {
        final String adminParamKey = provider.getPreferencesService().getHttpAdminKey();
        final String adminParamValue = provider.getPreferencesService().getHttpAdminValue();

        httpService.registerServlet(ChannelListResponse.baseUrl(),
                                    new ChannelListResponse(model), null, httpContext);
        httpService.registerServlet(DisconnectedResponse.baseUrl(),
                                    new DisconnectedResponse(model), null, httpContext);
        httpService.registerServlet(ShowChannelResponse.baseUrl(),
                                    new ShowChannelResponse(model), null, httpContext);
        httpService.registerServlet(StartChannelResponse.baseUrl(),
                                    new StartChannelResponse(model), null, httpContext);
        httpService.registerServlet(StopChannelResponse.baseUrl(), new StopChannelResponse(model, adminParamKey, adminParamValue),
                                    null, httpContext);
        httpService.registerServlet(AddChannelResponse.baseUrl(),
                                    new AddChannelResponse(model), null, httpContext);
        httpService.registerServlet(RemoveChannelResponse.baseUrl(), new RemoveChannelResponse(model, adminParamKey, adminParamValue),
                                    null, httpContext);
        httpService.registerServlet(PermanentDisableChannelResponse.baseUrl(), new PermanentDisableChannelResponse(model, adminParamKey, adminParamValue),
                                    null, httpContext);
        httpService.registerServlet(ImportResultResponse.baseUrl(), new ImportResultResponse(model), null, httpContext);
    }


    /** Stop the server */
    public void stop() {
        try {
            stopHttpService(_pid);
        } catch (final Exception ex) {
            LOG.warn("Unknown exception while stopping Http Server", ex);
        }
    }

    @Nonnull
    private HttpService createHttpService(@Nonnull final BundleContext context,
                                          final int port) throws Exception {
        _pid = "HTTPD" + port;

        // Create a custom HttpService
        // avoid the auto-started instance
        final Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
        dictionary.put("http.port", Integer.valueOf(port));
        dictionary.put("other.info", _pid);

        JettyConfigurator.startServer(_pid, dictionary);

        // Locate that custom server so we can add servlets
        // (thanks to Gunnar Wagenknecht for this info)
        // Tried to use the service.pid instead of other.info,
        // but that didn't seem to work.
        final String filter =
            String.format("(&(objectClass=%s)(other.info=%s))",
                            HttpService.class.getName(), _pid);
        _httpTracker =
            new ServiceTracker(context, context.createFilter(filter), null);
        _httpTracker.open();

        final Object[] services = _httpTracker.getServices();
        if (services == null) {
            throw new Exception("No HttpService found");
        }
        if (services.length != 1) {
            throw new Exception("Found " + services.length + " HttpServices instead of one");
        }
        if (!(services[0] instanceof HttpService)) {
            throw new Exception("Got " + services[0].getClass().getName() + " instead of HttpService");
        }

        return (HttpService) services[0];
    }

    /**
     * Stop a HttpService that was started at given port.
     *  <p>
     *  Will only work with HttpServices that were started by
     *  <code>createHttpService</code>
     *  @port Port where the HttpService was started
     *  @throws Exception on error
     *  @see #createHttpService
     */
    private void stopHttpService(@Nonnull final String pid) throws Exception {
        if (pid != null) {
            JettyConfigurator.stopServer(pid);
        }
        if(_httpTracker != null) {
            _httpTracker.close();
        }
    }
}
