package org.remotercp.ecf.session;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
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

/**
 * Session service implementation
 *
 * @author bknerr
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 10.09.2010
 */
public class SessionServiceImpl implements ISessionService {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(SessionServiceImpl.class);

	private ConnectionDetails _connectionDetails;

	private ECFConnector _container;

	private RemoteServicePublisher _publisher;

	/**
	 * Constructor.
	 */
	public SessionServiceImpl() {
        System.out.println("Session Service Constructor.");
    }

	/**
	 * {@inheritDoc}
	 */
	public ConnectionDetails getConnectionDetails() {
		return _connectionDetails;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setConnectionDetails(final ConnectionDetails connectionDetails) {
		this._connectionDetails = connectionDetails;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setContainer(final ECFConnector container) {
		this._container = container;
		final BundleContext context = ECFActivator.getBundleContext();
		_publisher = new RemoteServicePublisher(context, getRemoteServiceContainerAdapter());
		_publisher.start();
	}

	private IPresenceContainerAdapter getPresenceContainerAdapter() {
		final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) this._container
				.getAdapter(IPresenceContainerAdapter.class);

		Assert.isNotNull(adapter);
		return adapter;
	}

	protected IRemoteServiceContainerAdapter getRemoteServiceContainerAdapter() {
		final IRemoteServiceContainerAdapter adapter =
			(IRemoteServiceContainerAdapter) this._container
				.getAdapter(IRemoteServiceContainerAdapter.class);
		Assert.isNotNull(adapter);
		return adapter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public synchronized <T> List<T> getRemoteService(final Class<T> clazz,
			final ID[] filterIDs, final String filter) throws ECFException,
			InvalidSyntaxException {
		return getRemoteServiceProxies(clazz, filterIDs, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> getRemoteServiceProxies(final Class<T> clazz, final ID[] filterIDs,
			final String filter) throws InvalidSyntaxException {
		final List<T> proxies = new ArrayList<T>();
		final List<IRemoteService> remoteServices =
			getRemoteServices(clazz, filterIDs, filter);
		for (final IRemoteService remoteService : remoteServices) {
			try {
				final T serviceProxy = clazz.cast(remoteService.getProxy());
				proxies.add(serviceProxy);
			} catch (final ECFException e) {
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
	public <T> List<T> getRemoteServiceProxies(final Class<T> clazz, final ID[] filterIDs) {
		try {
			return getRemoteServiceProxies(clazz, filterIDs, null);
		} catch (final InvalidSyntaxException e) {
			// This cannot happen because this method didn't specify a filter.
			throw new AssertionError();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public IRemoteService[] getRemoteServiceReference(final Class<?> clazz,
			final ID[] filterIDs, final String filter) throws InvalidSyntaxException {
		final List<IRemoteService> remoteServices =
			getRemoteServices(clazz, filterIDs, filter);
		return remoteServices.toArray(new IRemoteService[remoteServices.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRemoteService> getRemoteServices(final Class<?> clazz,
			final ID[] filterIDs, final String filter) throws InvalidSyntaxException {
		final List<IRemoteService> remoteServices = new ArrayList<IRemoteService>();
		final IRemoteServiceContainerAdapter container =
			getRemoteServiceContainerAdapter();

		final IRemoteServiceReference[] refs = container
				.getRemoteServiceReferences(filterIDs, clazz.getName(), filter);

		// If no service references are found, return an empty list.
		if (refs == null) {
			return remoteServices;
		}

		// For each service reference, try to get the IRemoteService interface
		// to the service and add it to the result list.
		for (final IRemoteServiceReference ref : refs) {
			final IRemoteService service = container.getRemoteService(ref);
			if (service != null) {
				remoteServices.add(service);
			}
		}
		return remoteServices;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRemoteService> getRemoteServices(final Class<?> clazz,
			final ID[] filterIDs) {
		try {
			return getRemoteServices(clazz, filterIDs, null);
		} catch (final InvalidSyntaxException e) {
			// This cannot happen because this method didn't specify a filter.
			throw new AssertionError();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRosterManager getRosterManager() {
		final IRosterManager rosterManager = this.getPresenceContainerAdapter()
				.getRosterManager();
		Assert.isNotNull(rosterManager);
		return rosterManager;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRoster getRoster() {
		final IRoster roster = getRosterManager().getRoster();
		Assert.isNotNull(roster);
		return roster;
	}

	/**
	 * {@inheritDoc}
	 */
	public IChatManager getChatManager() {
		final IChatManager chatManager = this.getPresenceContainerAdapter()
				.getChatManager();
		Assert.isNotNull(chatManager);
		return chatManager;
	}

	/**
	 * {@inheritDoc}
	 */
	public IContainer getContainer() {
		Assert.isNotNull(_container);
		return this._container;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasContainer() {
		return this._container != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public synchronized void registerRemoteService(final String serviceName,
			final Object impl) {
		Assert.isNotNull(this._container);

		final Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(Constants.SERVICE_REGISTRATION_TARGETS, new ID[0]);

		// register ECF remote service
		getRemoteServiceContainerAdapter().registerRemoteService(
				new String[] { serviceName }, impl, props);

		LOG.info("Service Registered: " + serviceName);
	}


	/**
	 * {@inheritDoc}
	 */
	public void ungetRemoteService(final ID[] idFilter,
	                               final String serviceName,
	                               final String filter) throws ECFException, InvalidSyntaxException {
		// TODO: check if this method should be called and what should be done
		// in this method if it is required.
	}
}
