/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.platform.libs.xmpp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.dcf.messaging.ConnectionManager;
import org.csstudio.platform.libs.dcf.messaging.IMessageListener;
import org.csstudio.platform.libs.dcf.messaging.IMessageListenerFilter;
import org.csstudio.platform.libs.dcf.messaging.Message;
import org.csstudio.platform.libs.dcf.messaging.MessagingException;
import org.csstudio.platform.libs.xmpp.impl.XmppMessage;
import org.csstudio.platform.libs.xmpp.localization.Messages;
import org.csstudio.platform.libs.xmpp.preferences.PreferenceConstants;
import org.csstudio.platform.libs.xmpp.roster.XmppGroup;
import org.csstudio.platform.libs.xmpp.roster.XmppResource;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.securestore.SecureStore;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;
import org.csstudio.platform.util.LoginCallbackHandlerEnumerator;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;

/**
 * Implements a {@link ConnectionManager} using the XMPP protocol.
 *
 * @author Anze Vodovnik
 */
public class XmppConnectionManager extends ConnectionManager {

	private final CentralLogger log = CentralLogger.getInstance();

	private boolean _publicConnectionEnabled = false;
	private boolean _secureConnection = true;
	private String _hostName = "";
	private int	   _port	 = 5222;
	private final Map<IMessageListenerFilter, IMessageListener> _msgListeners =
		new ConcurrentHashMap<IMessageListenerFilter, IMessageListener>();
	private XMPPConnection _connection;
	private List<ContactElement> _childrenCache = null;

	/**
	 * Creates a new XMPP connection manager.
	 */
	public XmppConnectionManager() {
		log.debug(this, "Manager constructued.");
	}

	@Override
	public void addMessageListener(final IMessageListener listener,
			final IMessageListenerFilter filter) {
		if(!_msgListeners.containsKey(filter)) {
		    _msgListeners.put(filter, listener);
        }
	}

	@Override
	public void removeMessageListener(final IMessageListener listener) {
		_msgListeners.remove(listener);
	}

	@Override
	public String getId() {
		return "XmppConnectionManager";
	}

	@Override
	public Throwable sendMessage(final Message message) {
		try {
			// get the target
			if(message.getTarget() == null) {
                // XXX: Should throw a specific exception. In this particular
				// case, why can you create a message without a target in the
				// first place?
				throw new Exception("Target cannot be null");
            }

			log.debug(this, "Sending to: " + message.getTarget().toString());

			if(!message.getTarget().isAvaiable()) {
				// XXX: Should throw a specific exception.
				return new Exception("Target is not available!");
			}

			if(message.getTarget().getChildren() != null) {
				for(final ContactElement ce : message.getTarget().getChildren()) {
					sendMessage(message,ce);
				}
			}

			return sendMessage(message, message.getTarget());

		/* XXX: This is bad error handling. Note that this catches
	     * *everything*, including Errors like OutOfMemoryError or errors in
	     * the virtual machine.
	     */
		} catch (final Throwable e) {
			e.printStackTrace();
			return e;
		}
	}

	private Throwable sendMessage(final Message message, final ContactElement ce) {
		// XXX: Why is it silently ignored if the contact is not available?
		// The sendMessage method above returns an exception in that case.
		if(!ce.isAvaiable()) {
            return null;
        }

		//		 we create a new XMPP message
		final XmppMessage msg = new XmppMessage(message);
		msg.setTo(ce.toString());
		msg.setFrom(_connection.getUser().concat(getResource()));
		message.setOrigin(new XmppResource(msg.getFrom()));
		msg.setType(IQ.Type.GET);

		log.debug(this, "Sending packet to: " + message.getTarget().toString());

		_connection.sendPacket(msg);
		log.debug(this, "XMPP Packet sent." + this.toString());
		notifyMessageSent(msg);
		return null;
	}

	/**
	 * Initializes the XMPP connection by logging in at the XMPP server. The
	 * user account that is used for the login is queried from the login handler
	 * registered at the {@code loginHandlers} extension point. If login does
	 * not succeed using the credentials returned from the login handler, a
	 * default user account set up in the preferences is used.
	 * 2008-03-26 MCL implement while loop to make sure that the initManager
	 * will always finish with an existing connection. Otherwise it will try forever
	 * (max loop time: 5 minutes) to connect. During startup of CSS this will allow changes
	 * in the preferences to be taken into account during startup.
	 */
	@Override
	public void initManager() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		_publicConnectionEnabled = prefs.getBoolean(Activator.PLUGIN_ID,
				PreferenceConstants.P_ENABLED, true, null);
		final String anonUsername = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.P_ANON_USER, "", null);
		final String anonPassword = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.P_ANON_PASS, "", null);

		ConnectionConfiguration cc = null;
		long sleepLoop = 0;

		// close an existing XMPP connection (if present)
		if(_connection != null) {
			_connection.disconnect();
		}

//		ConnectionConfiguration cc = createConnectionConfiguration();

		/*
		 * try to connect as long as it was not successful
		 */
		while ( _connection == null) {
			/*
			 * get fresh configuration
			 * this allows the user to change the preferences e.g. to change the port or the host name
			 */
			cc = createConnectionConfiguration();
			_connection = new XMPPConnection(cc);

			try {
				_connection.connect();
				log.getLogger(this).debug("XmppConnectiocManager: successfully connected ");

			} catch (final XMPPException exception) {

				if (sleepLoop == 1) {
					// log the error to the console also
					Activator.logError(Messages.getString("XmppManager.ConnectionError"), exception);
					System.err.println(exception.getStackTrace());
				}

				if( (_connection != null) && ( _connection.isConnected())) {
					_connection.disconnect();
				}
				_connection = null; // in any case ...
				/*
				 * wait before a retry
				 */
				if ( sleepLoop < 4*5) {	// less then 4*5*15sec == 5 minutes timeout max
					sleepLoop++;
				}

				log.getLogger(this).debug("XmppConnectiocManager: reconnect timeout: " + sleepLoop*15 + " sec");
//				log.debug( this, "XmppConnectiocManager: reconnect timeout: " + sleepLoop*15 + " sec");

				try {
					Thread.sleep( sleepLoop*15*1000); // 15,30,45 sec

				} catch (final InterruptedException e) {
					// TODO: handle exception
				}

			} catch ( final Exception e) {
				if( (_connection != null) && ( _connection.isConnected())) {
					_connection.disconnect();
				}
				_connection = null;	// in any case ...
				System.err.println("XmppConnectiocManager: nested exception");
				}
		}


		try {

			if(_publicConnectionEnabled) {
				// get the first login handler
				// XXX: this will fail if there are no login handlers
				final ILoginCallbackHandler lch = LoginCallbackHandlerEnumerator.getProxies(
						"org.csstudio.platform.libs.xmpp.loginHandlers", "loginHandler")[0];

				boolean loggedIn = false;
				final SecureStore store = SecureStore.getInstance();

				// first, try to get the username and password from the
				// secure store
				String username = (String) store.getObject("xmpp.username");
				String password = (String) store.getObject("xmpp.password");
				if ((username != null) && (password != null)) {
					loggedIn = login(username, password);

					// workaround because connection cannot be reused or
					// disconnected after failed login attempt
					if (!loggedIn) {
						_connection = new XMPPConnection(cc);
						_connection.connect();
					}
				}

				// if using the secure store did not work, use callback to get
				// username and password
				while (!loggedIn) {
					final Credentials cred = lch.getCredentials();
					if(cred == null) {
						// No credentials returned, try to log in anonymously
						login(anonUsername, anonPassword);

						// We set loggedIn to true even if the anonymous login
						// failed. Otherwise we would be stuck in the loop
						// forever if no credentials are known.
						// TODO: display warning if anonymous login failed?
						loggedIn = true;
					} else {
						// Try to log in using the credentials returned from
						// the login handler, or try anonymous login if the
						// no username/password given.
						// TODO: prevent empty username in the login dialog
						username = cred.getUsername();
						password = cred.getPassword();
						if ((username == null) || "".equals(username)) {
							username = anonUsername;
							password = anonPassword;
						}
						loggedIn = login(username, password);

						if (!loggedIn) {
							lch.signalFailedLoginAttempt();

							// _connection.disconnect() causes a NPE if called here!
							// XXX: This is a workaround because reusing the
							// connection for a second login attempt does not work.
							_connection = new XMPPConnection(cc);
							_connection.connect();
						} else {
							// successfully logged in -- store username and
							// password in the secure store
							// TODO: make this optional (based on user selection)
							store.setObject("xmpp.username", username);
							store.setObject("xmpp.password", password);
						}
					}
				}
			} else {
				login(anonUsername, anonPassword);
			}

			// add a packet listener
			_connection.addPacketListener(new PacketListener() {

				public void processPacket(final Packet packet) {
					final XMPPError error = packet.getError();
					if(error != null) {
						System.err.println("Error Message: " + error.getMessage());
						System.err.println("Error code: " + error.getCode());
						System.err.println("Condition: " + error.getCondition());
						System.err.println("Origin: " + packet.getFrom());
						Activator.logError("XMPPError" + error.getCode(), null);
						notifyErrorRecieved(packet);
						return;
					}

					// if packet is a message
					if(packet instanceof XmppMessage) {
						// TODO: rewrite the origin inside the encapsulated message
						//((XmppMessage)packet).getEncapsulatedMessage().setOrigin(new XmppUser(packet.getFrom()));
						notifyMessageRecieved((XmppMessage)packet);
					}
				}

			}, new PacketFilter() {
				public boolean accept(final Packet packet) {
					return true;
				}
			});

			// register for roster changes
			_connection.getRoster().addRosterListener(new RosterListener() {

				public void entriesAdded(final Collection<String> addresses) {
					log.debug(this, "Entries changed.");
					notifyDirectoryChangeListener(true);
				}

				public void entriesDeleted(final Collection<String> addresses) {
					log.debug(this, "Entries changed.");
					notifyDirectoryChangeListener(true);
				}

				public void entriesUpdated(final Collection<String> addresses) {
					log.debug(this, "Entries changed.");
					notifyDirectoryChangeListener(false);
				}

				public void presenceChanged(final Presence presence) {
					log.debug(this, "Presence changed.");
					notifyDirectoryChangeListener(false);
				}

			});

			// iterates through all the account attributes and prints them
			// to the system log (routed to the CSS console)
			final Iterator<String> i = _connection.getAccountManager().getAccountAttributes().iterator();
			String value;
			while(i.hasNext()) {
				value = i.next();
				System.out.println(value.concat(": ").concat(_connection.getAccountManager().getAccountAttribute(value)));
			}

			_connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);

		} catch (final XMPPException exception) {
			// log the error to the console also
			Activator.logError(Messages.getString("XmppManager.ConnectionError"), exception);
			System.err.println(exception.getStackTrace());
		}
	}

	/**
	 * Creates a connection configuration. The host name, port and security
	 * settings are read from the preferences service.
	 *
	 * @return the connection configuration.
	 */
	private ConnectionConfiguration createConnectionConfiguration() {
		final IPreferencesService ps = Platform.getPreferencesService();
		_hostName = ps.getString(Activator.PLUGIN_ID,
				PreferenceConstants.P_HOST, "krykxmpp.desy.de", null);
		_port = ps.getInt(Activator.PLUGIN_ID,
				PreferenceConstants.P_PORT, 5222, null);
		_secureConnection = ps.getBoolean(Activator.PLUGIN_ID,
				PreferenceConstants.P_SECURE, false, null);

		final ConnectionConfiguration cc =
			new ConnectionConfiguration(_hostName, _port);
		if(_secureConnection) {
			cc.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
		}

		return cc;
	}

	/**
	 * Performs a login on the current connection.
	 *
	 * @param username The username.
	 * @param password The password.
	 * @return {@code true} if the user was successfully logged in,
	 * {@code false} otherwise.
	 */
	private boolean login(final String username, final String password) {
		// TODO: check behavior of smack lib with empty username/password (throws
		// IllegalStateException if both are empty, this seems to be a bug)
		try {
		    final String user = _connection.getUser();
            System.out.println("XMPP login as "+ (StringUtil.isBlank(user) ? "<empty>" : user));
			_connection.login(username, password, getResource());
			return true;
		} catch (final IllegalStateException state) {
			Activator.logError(Messages.getString("XmppManager.IllegalStateError"), state);
			// we can just print the message to the error output
			// because we know that the error is in the state
			System.err.println(Messages.getString("XmppManager.IllegalStateError"));
			return false;
		} catch (final XMPPException e) {
			System.err.println("Login failed: " + e.getMessage());
			return false;
		}
	}

	protected void notifyMessageRecieved(final XmppMessage message) {
		final Message msg = message.getEncapsulatedMessage();
		// cannot notify on empty message
		if(msg != null) {
            msg.setOrigin(new XmppResource(message.getFrom()));
        }

		for(final IMessageListenerFilter filter : _msgListeners.keySet()) {
			if(filter.match(msg)) {
				_msgListeners.get(filter).notifyMessageRecieved(msg);
			}
		}
	}

	protected void notifyErrorRecieved(final Packet packet) {
		// wrap an exception to the message, and notify the listeners
		final Message msg = new Message(new MessagingException(packet.getError().getMessage(),
				packet.getError().getCondition()), "", null);

		msg.setOrigin(new XmppResource(packet.getFrom()));
		msg.setTarget(new XmppResource(packet.getTo()));

		for(final IMessageListenerFilter filter : _msgListeners.keySet()) {
			if(filter.match(msg)) {
				_msgListeners.get(filter).notifyMessageRecieved(msg);
			}
		}
	}

	protected void notifyMessageSent(final XmppMessage message) {
		final Message msg = message.getEncapsulatedMessage();
		for(final IMessageListenerFilter filter : _msgListeners.keySet()) {
			if(filter.match(msg)) {
				_msgListeners.get(filter).notifyMessageSent(msg);
			}
		}
	}

	@Override
	public ContactElement[] getDirectory() {
//		if(_connection == null
//				|| !_connection.isConnected()) return null;
//		if(_directory == null) {
//			_directory = new XmppDirectory(_connection.getRoster());
//		}
//		return _directory;
		try {
			final List<ContactElement> lst = getRosterChildren();
			return lst.toArray(new ContactElement[0]);
		} catch (final Exception e) {
			return new ContactElement[0];
		}
	}

	private List<ContactElement> getRosterChildren() {
		try {
			if(_childrenCache == null) {
				_childrenCache = new ArrayList<ContactElement>();
				// refill the cache
				// get the groups first, and let the groups get the children
				for(final RosterGroup group : _connection.getRoster().getGroups()) {
					_childrenCache.add(new XmppGroup(group, _connection.getRoster()));
				}
			}

			return _childrenCache;
		} catch (final Exception e) {
			return new ArrayList<ContactElement>();
		}
	}


	private String _resource = "";
	private String getResource() {
		if(_resource.equals("")) {
			// hostname#user#id
			String hostname;
			try {
				final InetAddress addr = InetAddress.getLocalHost();

				// Get hostname
				hostname = addr.getHostName();
			} catch (final UnknownHostException e) {
				// ignore it!
				hostname = "invalid";
			}
			final String username = System.getProperty("user.name");
			final Random rnd = new Random();

			_resource = hostname + "#" + username + "#" + Integer.toString(rnd.nextInt());

		}

		return _resource;
	}

	public Roster getRoster() {
		return _connection.getRoster();
	}

}
