/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.remotercp.ecf.session;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Tracks local OSGi services that are marked as remote services and registers
 * them in a remote service container. To mark a service as a remote service, it
 * must have the service property
 * <code>org.csstudio.management.remoteservice</code> set to
 * {@link Boolean#TRUE}.
 * 
 * @author Joerg Rathlev
 */
class RemoteServicePublisher {
	
	/**
	 * The filter that is used to track the remote services.
	 */
	private static final String SERVICE_FILTER =
		"(org.csstudio.management.remoteservice=true)";
	
	private IRemoteServiceContainerAdapter _container;
	private Map<ServiceReference, IRemoteServiceRegistration> _registrations;
	private ServiceTracker _tracker;

	/**
	 * Creates a new remote service publisher.
	 * 
	 * @param context
	 *            the bundle context in which services will be tracked.
	 * @param container
	 *            the remote service container adapter in which the remote
	 *            services will be registered.
	 */
	RemoteServicePublisher(BundleContext context,
			IRemoteServiceContainerAdapter container) {
		Filter filter = null;
		try {
			filter = context.createFilter(SERVICE_FILTER);
		} catch (InvalidSyntaxException e) {
			// This cannot happen, the filter is fixed.
			throw new AssertionError(
					"RemoteServicePublisher contains an invalid filter string.");
		}
		_tracker = new ServiceTracker(context, filter, null) {
			@Override
			public Object addingService(ServiceReference reference) {
				Object service = super.addingService(reference);
				registerService(reference, service);
				return service;
			}
			
			@Override
			public void removedService(ServiceReference reference,
					Object service) {
				unregisterService(reference);
				super.removedService(reference, service);
			}
		};
		_container = container;
		_registrations = new HashMap<ServiceReference, IRemoteServiceRegistration>();
	}
	
	/**
	 * Starts this publisher.
	 */
	void start() {
		_tracker.open();
	}

	/**
	 * Registers a service in the remote services registry.
	 * 
	 * @param reference
	 *            the service reference.
	 * @param service
	 *            the service object.
	 */
	private void registerService(ServiceReference reference, Object service) {
		String[] names = (String[]) reference.getProperty(Constants.OBJECTCLASS);
		Dictionary<String,Object> properties = new Hashtable<String, Object>();
		/*
		 * The SERVICE_REGISTRATION_TARGETS property is set to an empty array,
		 * so the service registration is not actively advertised to any other
		 * users. (That wouldn't work reliably anyway, as not all other users
		 * are online all the time.) The ECF queries a remote container instance
		 * as needed if its services are not already known.
		 */
		properties.put(
				org.eclipse.ecf.remoteservice.Constants.SERVICE_REGISTRATION_TARGETS,
				new String[0]);
		IRemoteServiceRegistration remoteRegistration =
			_container.registerRemoteService(names, service, properties);
		_registrations.put(reference, remoteRegistration);
	}

	/**
	 * Unregisters the service referenced by the specified service reference
	 * from the remote services registry.
	 * 
	 * @param reference
	 *            the service reference.
	 */
	private void unregisterService(ServiceReference reference) {
		IRemoteServiceRegistration registration = _registrations.remove(reference);
		if (registration != null) {
			registration.unregister();
		}
	}

}
