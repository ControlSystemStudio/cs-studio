package org.remotercp.ecf.session;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.ecf.ECFActivator;
import org.remotercp.ecf.ECFConnector;

public class SessionServiceImpl implements ISessionService {

	private ConnectionDetails connectionDetails;

	private ECFConnector containter;

	private static final Logger logger = Logger
			.getLogger(SessionServiceImpl.class.getName());
	
	private RemoteServicePublisher _publisher;

	public ConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}

	public void setConnectionDetails(ConnectionDetails connectionDetails) {
		this.connectionDetails = connectionDetails;
	}

	public void setContainer(ECFConnector container) {
		this.containter = container;
		BundleContext context = ECFActivator.getBundleContext();
		_publisher = new RemoteServicePublisher(context, getRemoteServiceContainerAdapter());
		_publisher.start();
	}

	private IPresenceContainerAdapter getPresenceContainerAdapter() {
		IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) this.containter
				.getAdapter(IPresenceContainerAdapter.class);
		
		Assert.isNotNull(adapter);
		return adapter;
	}

	protected IRemoteServiceContainerAdapter getRemoteServiceContainerAdapter() {
		final IRemoteServiceContainerAdapter adapter =
			(IRemoteServiceContainerAdapter) this.containter
				.getAdapter(IRemoteServiceContainerAdapter.class);
		Assert.isNotNull(adapter);
		return adapter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public synchronized <T> List<T> getRemoteService(Class<T> clazz,
			ID[] filterIDs, String filter) throws ECFException,
			InvalidSyntaxException {
		return getRemoteServiceProxies(clazz, filterIDs, filter);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> getRemoteServiceProxies(Class<T> clazz, ID[] filterIDs,
			String filter) throws InvalidSyntaxException {
		List<T> proxies = new ArrayList<T>();
		List<IRemoteService> remoteServices =
			getRemoteServices(clazz, filterIDs, filter);
		for (IRemoteService remoteService : remoteServices) {
			try {
				T serviceProxy = clazz.cast(remoteService.getProxy());
				proxies.add(serviceProxy);
			} catch (ECFException e) {
				/* This exception is thrown if the proxy cannot be created
				 * because there is no connection to the remote service. In that
				 * case, simply do nothing. We want to return a list of only
				 * those services which are currently connected.
				 */
			}
		}
		return proxies;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> getRemoteServiceProxies(Class<T> clazz, ID[] filterIDs) {
		try {
			return getRemoteServiceProxies(clazz, filterIDs, null);
		} catch (InvalidSyntaxException e) {
			// This cannot happen because this method didn't specify a filter.
			throw new AssertionError();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public IRemoteService[] getRemoteServiceReference(Class<?> clazz,
			ID[] filterIDs, String filter) throws InvalidSyntaxException {
		List<IRemoteService> remoteServices =
			getRemoteServices(clazz, filterIDs, filter);
		return remoteServices.toArray(new IRemoteService[remoteServices.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRemoteService> getRemoteServices(Class<?> clazz,
			ID[] filterIDs, String filter) throws InvalidSyntaxException {
		List<IRemoteService> remoteServices = new ArrayList<IRemoteService>();
		IRemoteServiceContainerAdapter container =
			getRemoteServiceContainerAdapter();
		
		IRemoteServiceReference[] refs = container
				.getRemoteServiceReferences(filterIDs, clazz.getName(), filter);
		
		// If no service references are found, return an empty list.
		if (refs == null) {
			return remoteServices;
		}
		
		// For each service reference, try to get the IRemoteService interface
		// to the service and add it to the result list.
		for (IRemoteServiceReference ref : refs) {
			IRemoteService service = container.getRemoteService(ref);
			if (service != null) {
				remoteServices.add(service);
			}
		}
		return remoteServices;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<IRemoteService> getRemoteServices(Class<?> clazz,
			ID[] filterIDs) {
		try {
			return getRemoteServices(clazz, filterIDs, null);
		} catch (InvalidSyntaxException e) {
			// This cannot happen because this method didn't specify a filter.
			throw new AssertionError();
		}
	}

	public IRosterManager getRosterManager() {
		IRosterManager rosterManager = this.getPresenceContainerAdapter()
				.getRosterManager();
		Assert.isNotNull(rosterManager);
		return rosterManager;
	}

	public IRoster getRoster() {
		IRoster roster = getRosterManager().getRoster();
		Assert.isNotNull(roster);
		return roster;
	}

	public IChatManager getChatManager() {
		IChatManager chatManager = this.getPresenceContainerAdapter()
				.getChatManager();
		Assert.isNotNull(chatManager);
		return chatManager;
	}

	public IContainer getContainer() {
		Assert.isNotNull(containter);
		return this.containter;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean hasContainer() {
		return this.containter != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public synchronized void registerRemoteService(String serviceName,
			Object impl) {
		Assert.isNotNull(this.containter);

		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(Constants.SERVICE_REGISTRATION_TARGETS, new ID[0]);

		// register ECF remote service
		getRemoteServiceContainerAdapter().registerRemoteService(
				new String[] { serviceName }, impl, props);

		logger.info("Service Registered: " + serviceName);
	}

	/**
	 * Unget a remote service. This operation should actually be called if a
	 * client disconnects. Ask ECF devs if this happens. If yes, delete this
	 * method.
	 * 
	 * @param idFilter
	 *            The user id array for which the service should be unget
	 * @param service
	 *            The service interface class
	 * @param filter
	 * @throws ECFException
	 * @throws InvalidSyntaxException
	 */
	public void ungetRemoteService(ID[] idFilter, String serviceName,
			String filter) throws ECFException, InvalidSyntaxException {
		// TODO: check if this method should be called and what should be done
		// in this method if it is required.
	}
}
