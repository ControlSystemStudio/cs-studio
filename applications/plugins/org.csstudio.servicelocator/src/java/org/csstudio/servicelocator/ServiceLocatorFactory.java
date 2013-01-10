/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.servicelocator;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.annotation.Nonnull;

import org.csstudio.servicelocator.ServiceLocator.IServiceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support for the service locator. Helps registering services with service trackers.
 * 
 * @author jpenning
 * @since 25.01.2012
 */
public final class ServiceLocatorFactory {
    
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLocatorFactory.class);
    
    private ServiceLocatorFactory() {
        // utility class, no objects will be created
    }
    
    /**
     * Creates a service tracker for the given service type at the given bundle context
     * 
     * @param <T> the type of the service
     * @param context the bundle context of the calling activator
     * @param service the interface type of the service
     * @return the newly created service tracker
     */
    @Nonnull
    public static <T> ServiceTracker<T, Object> createServiceTracker(@Nonnull final BundleContext context,
                                                                     @Nonnull final Class<T> service) {
        ServiceTracker<T, Object> serviceTracker = new ServiceTracker<T, Object>(context,
                                                                                 service,
                                                                                 null);
        serviceTracker.open();
        return serviceTracker;
    }
    
    /**
     * Register the given service (consists of the interface type and the implementation)
     * at the osgi service registry and also at the service locator for singleton-style easy access.
     * 
     * @param <T> type of the service interface
     * @param <I> implementation of the service
     * @param description only a descriptive string useful in the osgi console
     * @param context the bundle context, where the service is registered
     * @param service the service interface
     * @param impl the implementation
     */
    public static <T, I extends T> void registerServiceWithTracker(@Nonnull final String description,
                                                                   @Nonnull final BundleContext context,
                                                                   @Nonnull final Class<T> service,
                                                                   @Nonnull final I impl) {
        LOG.info("Registering OSGi service: " + description);
        
        final Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("service.vendor", "DESY");
        properties.put("service.description", description);
        
        context.registerService(service, impl, properties);
        ServiceLocator.registerServiceTracker(service, ServiceLocatorFactory
                .createServiceTracker(context, service));
    }
    
    public static <T, I extends T> void registerRemoteService(@Nonnull final String description,
                                                              @Nonnull final String rmiServer,
                                                              final int rmiPort,
                                                              @Nonnull final Class<T> service) throws RemoteException,
                                                                                              NotBoundException {
        LOG.info("Registering remote service: " + description);
        
        IServiceProvider<T> serviceProvider = new ServiceLocator.ServiceProviderForRemote<T, I>(rmiServer, rmiPort, service);
        ServiceLocator.registerServiceProvider(service, serviceProvider);
    }
    
}
