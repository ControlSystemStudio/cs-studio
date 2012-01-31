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
 */
package org.csstudio.servicelocator;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Implements a configurable service locator.
 *
 * Usage: The service provider registers services directly with their implementation (for tests) or
 * dynamically using a service tracker (which is more appropriate in an OSGi environment).
 *
 * The client gets the service by its interface type, it does not know how the service is provided.
 * Therefore the client uses the service locator as a singleton, so we have simple client code.
 *
 * @author jpenning
 * @since 20.01.2012
 */
public final class ServiceLocator {

    /**
     * Provides the accessor function for the service implementation
     * A function is necessary because we may register a service tracker to dynamically retrieve the service implementation.
     * It is also possible to close the service provider. This is used to close the service trackers.
     */
    private interface IServiceProvider<T> {
        @CheckForNull
        T getService();

        void close();
    }

    // map from interface type to the accessor function for the service implementation
    private static Map<Class<?>, IServiceProvider<?>> TYPE2IMPL = new HashMap<Class<?>, IServiceProvider<?>>();

    // flag to detect close
    private static boolean IS_CLOSED = false;

    private ServiceLocator() {
        // used internally only
    }

    /**
     * Clears all registered services.
     * CAREFUL: Use only for tests. Therefore this method is package-scoped.
     */
    static void reset() {
        TYPE2IMPL = new HashMap<Class<?>, IServiceProvider<?>>();
        IS_CLOSED = false;
    }

    static void close() {
        for (final IServiceProvider<?> serviceProvider : TYPE2IMPL.values()) {
            serviceProvider.close();
        }
        IS_CLOSED = true;
    }

    /**
     * Register a service implementation for the given type.
     * Typically this is called from the activator of a plugin for production services
     * or from a test for mocks.
     *
     * @param <T> type of the service (its interface)
     * @param <I> type of the implementation of the service
     * @param service
     * @param impl
     */
    public static <T, I extends T> void registerService(@Nonnull final Class<T> service,
                                                 @Nonnull final I impl) {
        final IServiceProvider<T> serviceProvider = createServiceProvider(impl);
        // If called more than once, the last one will survive.
        // This simplifies tests, where more than one call to register a service will usually be
        // made from within the setup method.
        TYPE2IMPL.put(service, serviceProvider);
    }

    /**
     * Register a service tracker giving dynamic access to the implementation for the given type.
     * Typically this is called from the activator of a plugin for production services.
     *
     * @param <T> type of the service (its interface)
     * @param <I> type of the implementation of the service
     * @param service
     * @param serviceTracker
     */
    public static <T, I extends T> void registerServiceTracker(@Nonnull final Class<T> service,
                                                        @Nonnull final ServiceTracker serviceTracker) {
        final IServiceProvider<T> serviceProvider = createServiceProviderFromTracker(serviceTracker);
        TYPE2IMPL.put(service, serviceProvider);
    }

    /**
     * Get the implementation of the service with the given type
     * Because of the dynamic nature of OSGi there may be no service present, in that case <code>null</code> is returned.
     *
     * @param typeOfService
     * @return service implementation or null
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    public static <T> T getService(@Nonnull final Class<T> typeOfService) {
        // guard: only when not closed
        if (IS_CLOSED) {
            throw new IllegalStateException("Trying to get s service of type " + typeOfService +
            		" but service locator has already been closed.");
        }

        final IServiceProvider<?> serviceProvider = TYPE2IMPL.get(typeOfService);

        if (serviceProvider == null) {
            throw new IllegalStateException("Request for service type " + typeOfService + " which has not been registered.");
        }
        return (T) serviceProvider.getService();
    }

    @Nonnull
    private static <T, I extends T> IServiceProvider<T> createServiceProvider(@Nonnull final I service) {
        return new IServiceProvider<T>() {

            @Override
            @CheckForNull
            public T getService() {
                return service;
            }

            @Override
            public void close() {
                // nothing to do
            }
        };
    }

    @Nonnull
    private static <T> IServiceProvider<T> createServiceProviderFromTracker(@Nonnull final ServiceTracker serviceTracker) {
        return new ServiceProviderBasedOnServiceTracker<T>(serviceTracker);
    }

    /**
     * Encapsulates the service tracker-based access to the service implementation.
     */
    private static class ServiceProviderBasedOnServiceTracker<T> implements IServiceProvider<T> {

        private final ServiceTracker _serviceTracker;

        public ServiceProviderBasedOnServiceTracker(@Nonnull final ServiceTracker serviceTracker) {
            _serviceTracker = serviceTracker;
        }

        @SuppressWarnings("unchecked")
        @Override
        @Nonnull
        public T getService() {
            final T service = (T) _serviceTracker.getService();
            if (service == null) {
                Thread.dumpStack();
                throw new IllegalStateException("Service unavailabe from tracker: "
                        + _serviceTracker.toString());
            }

            return service;
        }

        @Override
        public void close() {
            _serviceTracker.close();
        }

    }
}
