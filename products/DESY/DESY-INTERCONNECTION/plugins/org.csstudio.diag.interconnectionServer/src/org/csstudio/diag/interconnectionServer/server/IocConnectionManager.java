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
public class IocConnectionManager {
	/*
	 * TODO: This class currently has two responsibilities, keeping track of the
	 * connections and providing statistical information. Maybe these should
	 * be separated.
	 */

	private static IocConnectionManager statisticInstance = null;
	public Hashtable<String, IocConnection> connectionList = null; // accessed by BeaconWatchdog, InterconnectionServer
	int totalNumberOfIncomingMessages = 0; // accessed by IocConnection
	int totalNumberOfOutgoingMessages = 0; // accessed by IocConnection
	
	private final IIocDirectory iocDirectory;

	private IocConnectionManager(IIocDirectory iocDirectory) {
		connectionList = new Hashtable<String, IocConnection>();
		this.iocDirectory = iocDirectory;
	}

	/**
	 * Returns the singleton IocConnectionManager.
	 * 
	 * @param iocDirectory
	 *            the IOC directory that will be used by the manager.
	 */
	// TODO: should this really be a singleton?
	// XXX: This getInstance method is intended for testing only. But this is
	// a really bad design and its only purpose is to work around all the other
	// even worse design.
	static synchronized IocConnectionManager getInstance(IIocDirectory iocDirectory) {
		if (statisticInstance == null) {
			statisticInstance = new IocConnectionManager(iocDirectory);
		}
		return statisticInstance;
	}
	
	/**
	 * Returns the singleton IocConnectionManager.
	 */
	public static synchronized IocConnectionManager getInstance() {
		// XXX: It's obviously useless to create a new instance of
		// LdapIocDirectory here every single time, but that's currently the
		// only way to have this parameterized for tests. This class really
		// really should be redesigned!
		return getInstance(new LdapIocDirectory());
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
	 */
	// 2009-07-06 MCL
    // change internal ID from hostName to hostAddress        
    //
	synchronized public IocConnection getIocConnection(InetAddress iocInetAddress, int port) {
		String internalId = iocInetAddress.getHostAddress() + ":" + port;
		if (connectionList.containsKey(internalId)) {
			return connectionList.get(internalId);
		} else {
			IocConnection connection = new IocConnection(iocInetAddress, port, TimeUtil.systemClock(), iocDirectory);
			connectionList.put(internalId, connection);
			return connection;
		}
	}

	/**
	 * Returns the IOC connections managed by this manager.
	 * 
	 * @return the IOC connections managed by this manager.
	 */
	public Collection<IocConnection> getIocConnections() {
		return new ArrayList<IocConnection>(connectionList.values());
	}

	public String getStatisticAsString() {
		String result = "";
		result += "\nTotal incomin messages     	= "
				+ this.totalNumberOfIncomingMessages;
		result += "\nTotal outgoing messages     	= "
				+ this.totalNumberOfOutgoingMessages;
		result += "\n";

		Enumeration<IocConnection> connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = connections.nextElement();
			result += "\n---------- statistische Auswertung ---------------\n";
			StringBuilder buf = new StringBuilder();
			thisContent.appendStatisticInformationTo(buf);
			result += buf.toString();
		}
		return result;
	}

	public String getNodeNames() {
		String nodeNames = null;
		boolean first = true;

		Enumeration<IocConnection> connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = connections.nextElement();
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
		List<String> nodeNames = new ArrayList<String>();

		Enumeration<IocConnection> connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = connections.nextElement();
			nodeNames.add(thisContent.getHost());
		}
		return nodeNames.toArray(new String[0]);

	}
	
	public InetAddress[] getListOfIocInetAdresses() {
		List<InetAddress> nodeNames = new ArrayList<InetAddress>();

		Enumeration<IocConnection> connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = connections.nextElement();
			nodeNames.add(thisContent.getIocInetAddress());
		}
		return nodeNames.toArray(new InetAddress[0]);

	}
	
	public InetAddress getIocInetAdressByName( String iocName) {
		
		if (iocName == null) return null;

		Enumeration<IocConnection> connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = connections.nextElement();
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
	 */
	public void refreshIocNameDefinition(String iocHostname) {

		// XXX: Why does this method have its own logic for finding the
		// IocConnection object?
		Enumeration<IocConnection> connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = connections.nextElement();
			if ( thisContent.getHost().equals(iocHostname)) {
				/*
				 * in case that the IOC name was not properly stored in LDAP we need a way to reset the name
				 */
				String[] iocNames = iocDirectory.getLogicalIocName(
						thisContent.getIocInetAddress().getHostAddress(),
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
		List<String> nodeNames = new ArrayList<String>();

		// just in case no enum is possible
		Enumeration<IocConnection> connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = connections.nextElement();
			nodeNames.add(thisContent.getHost() + "|"
					+ thisContent.getLogicalIocName());
		}
		return nodeNames.toArray(new String[0]);

	}

	public String[] getNodeNameStatusArray() {
		List<String> nodeNames = new ArrayList<String>();

		// just in case no enum is possible
		Enumeration<IocConnection> connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = connections.nextElement();
			nodeNames.add(thisContent.getHost() + " | "
					+ thisContent.getLogicalIocName() + "  "
					+ thisContent.getCurrentConnectState() + "  "
					+ thisContent.getCurrentSelectState());
		}
		return nodeNames.toArray(new String[0]);

	}

}
