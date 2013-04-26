package org.remotercp.ecf.session.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresence.Type;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.Presence;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.ecf.connection.ECFConstants;
import org.remotercp.ecf.session.ISessionService;

public class SessionServiceImpl implements ISessionService {

	private IContainer container;

	private static final Logger logger = Logger
			.getLogger(SessionServiceImpl.class.getName());

	private List<XMPPID> targetIDs = Collections
			.synchronizedList(new ArrayList<XMPPID>());

	private String userName;

	/**
	 * Connects to an XMPP-Server with the provided credentials
	 * 
	 * @throws ECFException
	 */
	public void connect(String userName, String password, String server)
			throws URISyntaxException, ECFException {
		this.userName = userName;
		container = ContainerFactory.getDefault().createContainer(
				ECFConstants.XMPP);

		XMPPID xmppid = new XMPPID(container.getConnectNamespace(), userName
				+ "@" + server);
		xmppid.setResourceName("" + System.currentTimeMillis());

		IConnectContext connectContext = ConnectContextFactory
				.createUsernamePasswordConnectContext(userName, password);

		container.connect(xmppid, connectContext);

		// update existing clients
		IPresence presence = new Presence(IPresence.Type.AVAILABLE);
		getRosterManager().getPresenceSender().sendPresenceUpdate(xmppid,
				presence);

		registerRosterListener();
	}

	private IPresenceContainerAdapter getPresenceContainerAdapter() {
		IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) this.container
				.getAdapter(IPresenceContainerAdapter.class);
		assert adapter != null : "adapter != null";
		return adapter;
	}

	protected synchronized IRemoteServiceContainerAdapter getRemoteServiceContainerAdapter() {
		IRemoteServiceContainerAdapter adapter = (IRemoteServiceContainerAdapter) this.container
				.getAdapter(IRemoteServiceContainerAdapter.class);
		assert adapter != null : "adapter != null";

		adapter.addRemoteServiceListener(new IRemoteServiceListener() {

			public void handleServiceEvent(IRemoteServiceEvent event) {
				// logger.info("Remote service event occured: " + event);
			}

		});
		return adapter;
	}

	/**
	 * Returns a list of remote service proxies for a given service. The given
	 * service might me provided by several users though there might be more
	 * than one service available. Use filterIDs and filter to delimit the
	 * amount of services.
	 * 
	 * @param <T>
	 *            The service type
	 * @param service
	 *            The needed remote service name. (Use yourinterface.class)
	 * @param filterIDs
	 *            User IDs work as a filter though remote services will be
	 *            limited to the given user. May be null if the service should
	 *            be get for all users.
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filter. May be null if all services should be
	 *            found
	 * @return A list of remote service proxies
	 * @throws ECFException
	 * @throws InvalidSyntaxException
	 */
	public synchronized <T> List<T> getRemoteService(Class<T> service,
			ID[] filterIDs, String filter) throws ECFException,
			InvalidSyntaxException {
		List<T> remoteServices = new ArrayList<T>();

		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = getRemoteServiceContainerAdapter();

		/* 1. get available services */
		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(filterIDs, service.getName(),
						filter);
		assert refs != null : "Remote service references != null";

		/* 2. get the proxies for found service references */
		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = remoteServiceContainerAdapter
					.getRemoteService(refs[serviceNumber]);

			T castedService = service.cast(remoteService.getProxy());
			assert castedService != null : "castedService != null";
			remoteServices.add(castedService);
		}

		return remoteServices;
	}

	public IRosterManager getRosterManager() {
		IRosterManager rosterManager = this.getPresenceContainerAdapter()
				.getRosterManager();
		assert rosterManager != null : "rosterManager != null";
		return rosterManager;
	}

	public IRoster getRoster() {
		IRoster roster = getRosterManager().getRoster();
		assert roster != null : "roster != null";
		return roster;
	}

	public IChatManager getChatManager() {
		IChatManager chatManager = this.getPresenceContainerAdapter()
				.getChatManager();
		assert chatManager != null : "chatManager != null";
		return chatManager;
	}

	public IContainer getContainer() {
		assert container != null : "container != null";
		return this.container;
	}

	/**
	 * Registers a service as remote service for OSGi over ECF
	 * 
	 * @param classType
	 *            The service name
	 * @param impl
	 *            The service implementation
	 * @param targetIDs
	 *            Buddies, who are to receive service registration. If
	 *            <code>null</code> all buddies in roster will be taken
	 */
	public synchronized void registerRemoteService(String serviceName,
			Object impl, ID[] targetIDs) {

		Dictionary<String, ID[]> props = new Hashtable<String, ID[]>();
		if (targetIDs == null) {
			targetIDs = this.targetIDs.toArray(new XMPPID[0]);
		}
		props.put(Constants.SERVICE_REGISTRATION_TARGETS, targetIDs);

		// register ECF remote service
		getRemoteServiceContainerAdapter().registerRemoteService(
				new String[] { serviceName }, impl, props);

		logger.info(">>>> Service Registered: " + serviceName);
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
	public synchronized void ungetRemoteService(ID[] idFilter,
			String serviceName, String filter) throws ECFException,
			InvalidSyntaxException {

		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = (IRemoteServiceContainerAdapter) this.container
				.getAdapter(IRemoteServiceContainerAdapter.class);

		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(idFilter, serviceName, filter);

		if (refs != null) {
			// unget the remote service
			for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {
				remoteServiceContainerAdapter
						.ungetRemoteService(refs[serviceNumber]);
				logger.info("Unget service: " + serviceName);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		assert adapter != null : "adapter != null";
		return (T) getContainer().getAdapter(adapter);

	}

	public ID getContainerID() {
		return container.getID();
	}

	public ID getConnectedID() {
		assert container.getConnectedID() != null : "containter.getConnectedID() != null";
		return container.getConnectedID();
	}

	public void addMessageListener(IIMMessageListener listener) {
		assert listener != null : "listener != null";
		getChatManager().addMessageListener(listener);
	}

	public IChatMessageSender getChatMessageSender() {
		return getChatManager().getChatMessageSender();
	}

	public String getUserName() {
		return this.userName;
	}

	/**
	 * This listener will listen for user availability changes and update the
	 * internal user list
	 */
	private void registerRosterListener() {

		getRoster().getPresenceContainerAdapter().getRosterManager()
				.addPresenceListener(new IPresenceListener() {

					public void handlePresence(ID fromID, IPresence presence) {
						if (fromID instanceof XMPPID) {
							XMPPID sender = (XMPPID) fromID;
							System.out.println("roster: "
									+ sender.getUsernameAtHost());
						}

					}
				});

		getRosterManager().addPresenceListener(new IPresenceListener() {

			public void handlePresence(ID fromID, IPresence presence) {
				System.out.println("Presence: " + presence.getType());
				if (fromID instanceof XMPPID) {
					XMPPID xmppId = (XMPPID) fromID;

					if (presence.getType().equals(Type.AVAILABLE)) {
						if (!targetIDs.contains(xmppId)) {
							targetIDs.add(xmppId);
							logger.info("Target IDs changed: "
									+ targetIDs.size() + " new user: "
									+ xmppId.getUsernameAtHost());
						}
					}
					if (presence.getType().equals(Type.UNAVAILABLE)) {
						if (targetIDs.contains(xmppId)) {
							targetIDs.remove(xmppId);
							logger.info("Target IDs changed: "
									+ targetIDs.size() + " new user: "
									+ xmppId.getUsernameAtHost());
						}
					}
				} else {
					logger.warning("User is not of type XMPP user");
				}
			}
		});
	}
}
