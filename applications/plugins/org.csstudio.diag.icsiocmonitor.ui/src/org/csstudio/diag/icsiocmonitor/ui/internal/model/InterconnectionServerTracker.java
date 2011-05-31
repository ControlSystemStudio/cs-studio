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

package org.csstudio.diag.icsiocmonitor.ui.internal.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.diag.icsiocmonitor.service.IIocConnectionReporter;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.remotercp.service.connection.session.ISessionService;

/**
 * Tracks the available interconnection servers via XMPP, retrieves their
 * {@link IIocConnectionReporter} services and gives them to an
 * {@link IocMonitor}.
 * 
 * @author Joerg Rathlev
 */
class InterconnectionServerTracker implements IPresenceListener {

	/**
	 * The XMPP user name of XMPP servers. When this name is found in the user
	 * ID of an XMPP user, that user is recognized as an interconnection server
	 * and tracked by this tracker.
	 */
	private static final String ICSERVER_NAME = "icserver-alarm";

	/**
	 * The delay in milliseconds before the reporter service is requested from
	 * an interconnection server which went online.
	 */
	private static final long GET_REPORTER_SERVICE_DELAY = 500;
	
	private final IocMonitor _iocMonitor;
	private ISessionService _sessionService;
	private final Map<ID, IIocConnectionReporter> _interconnectionServers;
	private final CentralLogger _log = CentralLogger.getInstance();

	/**
	 * Creates a new tracker.
	 * 
	 * @param monitor
	 *            the IOC monitor on which this tracker will set the services.
	 */
	InterconnectionServerTracker(IocMonitor monitor) {
		_iocMonitor = monitor;
		_interconnectionServers = new HashMap<ID, IIocConnectionReporter>();
	}

	/**
	 * Binds this tracker to the session service.
	 * 
	 * @param sessionService
	 *            the session service.
	 */
	void bindService(ISessionService sessionService) {
		_sessionService = sessionService;
		IRosterManager rosterManager = _sessionService.getRosterManager();
		if (rosterManager != null) {
			_log.debug(this, "ISessionService set, initializing");
			rosterManager.addPresenceListener(this);
			initializeFromRoster();
		}
	}

	/**
	 * Unbinds the session service from this tracker.
	 * 
	 * @param sessionService
	 *            the session service.
	 */
	void unbindService(ISessionService sessionService) {
		if (_sessionService == sessionService) {
			_log.debug(this, "ISessionService removed");
			try {
				IRosterManager rosterManager = _sessionService.getRosterManager();
				if (rosterManager != null) {
					rosterManager.removePresenceListener(this);
				}
			} finally {
				_sessionService = null;
			}
		}
	}
	
	/**
	 * Traverses the roster to find interconnection servers that are already
	 * available and adds them to the list of tracked servers.
	 */
	private void initializeFromRoster() {
		_log.debug(this, "Searching roster for servers which are already online");
		IRoster roster = _sessionService.getRoster();
		if (roster != null) {
			@SuppressWarnings("unchecked")
			Collection<IRosterItem> items = roster.getItems();
			for (IRosterItem rosterItem : items) {
				if (rosterItem instanceof IRosterGroup) {
					@SuppressWarnings("unchecked")
					Collection<IRosterEntry> entries =
						((IRosterGroup) rosterItem).getEntries();
					for (IRosterEntry entry : entries) {
						initializeFromRosterEntry(entry);
					}
				} else if (rosterItem instanceof IRosterEntry) {
					initializeFromRosterEntry((IRosterEntry) rosterItem);
				}
			}
		}
	}

	/**
	 * Checks if the given entry is an interconnection server and is available.
	 * If yes, adds it to the list of tracked servers.
	 * 
	 * @param entry
	 *            the entry.
	 */
	private void initializeFromRosterEntry(IRosterEntry entry) {
		ID id = entry.getUser().getID();
		IPresence presence = entry.getPresence();
		if (isInterconnectionServerID(id)
				&& presence != null
				&& presence.getMode() == IPresence.Mode.AVAILABLE) {
			handleInterconnectionServerAvailable(id);
		}
	}

	/**
	 * Checks if the given ID is the user ID of an interconnection server.
	 * 
	 * @param id
	 *            the ID.
	 * @return <code>true</code> if the ID is the user ID of an interconnection
	 *         server, <code>false</code> otherwise.
	 */
	private boolean isInterconnectionServerID(ID id) {
		return id.getName().indexOf(ICSERVER_NAME) != -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public void handlePresence(ID fromID, IPresence presence) {
		_log.debug(this, "Received presence event from: " + fromID + ", presence: " + presence);
		if (isInterconnectionServerID(fromID)) {
			if (presence.getType() == IPresence.Type.AVAILABLE) {
				handleInterconnectionServerAvailable(fromID);
			} else {
				handleInterconnectionServerUnavailable(fromID);
			}
		}
	}

	/**
	 * Called when an interconnection server becomes available.
	 * 
	 * @param id
	 *            the ID of the server.
	 */
	private void handleInterconnectionServerAvailable(ID id) {
		synchronized (_interconnectionServers) {
			if (!_interconnectionServers.containsKey(id)) {
				_log.debug(this, "Server is now available: " + id);
				try {
					Thread.sleep(GET_REPORTER_SERVICE_DELAY);
				} catch (InterruptedException e) {
					// reassert interrupt status
					Thread.currentThread().interrupt();
				}
				IIocConnectionReporter service = getConnectionReporterService(id);
				if (service != null) {
					_interconnectionServers.put(id, service);
					_iocMonitor.setReporterServices(_interconnectionServers.values());
				}
			}
		}
	}

	/**
	 * Gets the {@link IIocConnectionReporter} service from the interconnection
	 * server with the given ID. Returns <code>null</code> if the
	 * interconnection server does not offer that service.
	 * 
	 * @param id
	 *            the ID of the interconnection server.
	 * @return the service, or <code>null</code> if the service is not
	 *         available.
	 */
	private IIocConnectionReporter getConnectionReporterService(ID id) {
		List<IIocConnectionReporter> remoteServiceProxies =
			_sessionService.getRemoteServiceProxies(
					IIocConnectionReporter.class,
					new ID[] {id});
		if (remoteServiceProxies.size() > 0) {
			_log.debug(this, "Found IIocConnectionReporter service offered by: " + id);
			return remoteServiceProxies.get(0);
		} else {
			_log.info(this, "ICS does not offer IIocConnectionReporter service: " + id);
			return null;
		}
	}

	/**
	 * Called when an interconnection server is no longer available.
	 * 
	 * @param id
	 *            the ID of the server.
	 */
	private void handleInterconnectionServerUnavailable(ID id) {
		synchronized (_interconnectionServers) {
			IIocConnectionReporter service = _interconnectionServers.remove(id);
			if (service != null) {
				_log.debug(this, "Server no longer available: " + id);
				_iocMonitor.setReporterServices(_interconnectionServers.values());
			}
		}
	}
}
