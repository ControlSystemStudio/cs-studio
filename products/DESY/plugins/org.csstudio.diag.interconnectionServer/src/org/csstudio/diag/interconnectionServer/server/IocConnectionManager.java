/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.diag.interconnectionServer.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.naming.NamingException;

import org.csstudio.diag.interconnectionServer.internal.IIocDirectory;
import org.csstudio.diag.interconnectionServer.internal.LdapIocDirectory;
import org.csstudio.diag.interconnectionServer.internal.time.TimeUtil;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Keeps track of the connections to the IOCs. Also provides statistical
 * information.
 *
 * @author Matthias Clausen, Joerg Rathlev
 */
public enum IocConnectionManager {

    INSTANCE;

    /*
	 * TODO: This class currently has two responsibilities, keeping track of the
	 * connections and providing statistical information. Maybe these should
	 * be separated.
	 */

	private static IocConnectionManager statisticInstance = null;

	public Hashtable<String, IocConnection> _connectionList = null; // accessed by BeaconWatchdog, InterconnectionServer
	int totalNumberOfIncomingMessages = 0; // accessed by IocConnection
	int totalNumberOfOutgoingMessages = 0; // accessed by IocConnection

	private final IIocDirectory _iocDirectory;


	private IocConnectionManager() {
		_connectionList = new Hashtable<String, IocConnection>();
		_iocDirectory = new LdapIocDirectory();
	}

	/**
	 * Returns the IOC connection object representing the connection to the IOC
	 * on the specified host and port.
	 *
	 * @param hostAddress
	 *            the host address of the IOC.
	 * @param port
	 *            the port from which messages from the IOC are received.
	 * @return the IOC connection.
	 * @throws NamingException
	 */
	// 2009-07-06 MCL
    // change internal ID from hostName to hostAddress
    //
	synchronized public IocConnection getIocConnection(final InetAddress iocInetAddress,
	                                                   final int port) throws NamingException {
		final String internalId = iocInetAddress.getHostAddress() + ":" + port;
		if (_connectionList.containsKey(internalId)) {
			return _connectionList.get(internalId);
		} else {
			final IocConnection connection = new IocConnection(iocInetAddress,
			                                                   port,
			                                                   TimeUtil.systemClock());
			_connectionList.put(internalId, connection);
			return connection;
		}
	}

	/**
	 * Returns the IOC connections managed by this manager.
	 *
	 * @return the IOC connections managed by this manager.
	 */
	public Collection<IocConnection> getIocConnections() {
		return new ArrayList<IocConnection>(_connectionList.values());
	}

	public String getStatisticAsString() {
		String result = "";
		result += "\nTotal incomin messages     	= "
				+ this.totalNumberOfIncomingMessages;
		result += "\nTotal outgoing messages     	= "
				+ this.totalNumberOfOutgoingMessages;
		result += "\n";

		final Enumeration<IocConnection> connections = this._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection thisContent = connections.nextElement();
			result += "\n---------- statistische Auswertung ---------------\n";
			final StringBuilder buf = new StringBuilder();
			thisContent.appendStatisticInformationTo(buf);
			result += buf.toString();
		}
		return result;
	}

	public String getNodeNames() {
		String nodeNames = null;
		boolean first = true;

		final Enumeration<IocConnection> connections = this._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection thisContent = connections.nextElement();
			if (first) {
				nodeNames = thisContent.getHost() + ",";
			} else {
				nodeNames += thisContent.getHost() + ",";
			}
			first = false;
		}
		return nodeNames;

	}


	public String[] getNodeNameArray() {
		final List<String> nodeNames = new ArrayList<String>();

		final Enumeration<IocConnection> connections = this._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection thisContent = connections.nextElement();
			nodeNames.add(thisContent.getHost());
		}
		return nodeNames.toArray(new String[0]);

	}

	public InetAddress[] getListOfIocInetAdresses() {
		final List<InetAddress> nodeNames = new ArrayList<InetAddress>();

		final Enumeration<IocConnection> connections = this._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection thisContent = connections.nextElement();
			nodeNames.add(thisContent.getIocInetAddress());
		}
		return nodeNames.toArray(new InetAddress[0]);

	}

	public InetAddress getIocInetAdressByName( final String iocName) {

		if (iocName == null) {
            return null;
        }

		final Enumeration<IocConnection> connections = this._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection thisContent = connections.nextElement();
			if ( thisContent.getHost().equals(iocName)) {
				return thisContent.getIocInetAddress();
			}
		}
		return null;
	}

	/**
	 * Refreshes the logical name of an IOC from the directory server.
	 *
	 * @param iocHostname
	 *            the hostname of the IOC.
	 * @throws NamingException
	 */
	public void refreshIocNameDefinition(final String iocHostname) throws NamingException {

		// XXX: Why does this method have its own logic for finding the
		// IocConnection object?
		final Enumeration<IocConnection> connections = this._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection thisContent = connections.nextElement();
			if ( thisContent.getHost().equals(iocHostname)) {
				/*
				 * in case that the IOC name was not properly stored in LDAP we need a way to reset the name
				 */
				final String[] iocNames =
				    LdapSupport.INSTANCE.getLogicalIocName(thisContent.getIocInetAddress(),
				                                           iocHostname);
				/*
				 * these methods are synchronized in the subclass IocNameDefinitions
				 */
				thisContent.setLogicalIocName(iocNames[0]);
				thisContent.setLdapIocName(iocNames[1]);
				CentralLogger.getInstance().info(this, "Logical name of IOC " +
						iocHostname + " refreshed, new name is: " + iocNames[0]);
			}
		}
	}

	public String[] getNodeNameArrayWithLogicalName() {
		final List<String> nodeNames = new ArrayList<String>();

		// just in case no enum is possible
		final Enumeration<IocConnection> connections = this._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection thisContent = connections.nextElement();
			nodeNames.add(thisContent.getHost() + "|"
					+ thisContent.getLogicalIocName());
		}
		return nodeNames.toArray(new String[0]);

	}

	public String[] getNodeNameStatusArray() {
		final List<String> nodeNames = new ArrayList<String>();

		// just in case no enum is possible
		final Enumeration<IocConnection> connections = this._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection thisContent = connections.nextElement();
			nodeNames.add(thisContent.getHost() + " | "
					+ thisContent.getLogicalIocName() + "  "
					+ thisContent.getCurrentConnectState() + "  "
					+ thisContent.getCurrentSelectState());
		}
		return nodeNames.toArray(new String[0]);

	}

}
