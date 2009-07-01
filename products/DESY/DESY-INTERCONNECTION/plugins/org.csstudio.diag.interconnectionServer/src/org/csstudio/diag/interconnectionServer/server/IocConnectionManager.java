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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.csstudio.diag.interconnectionServer.internal.time.TimeUtil;

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
	public Hashtable<String, IocConnection> connectionList = null; // accessed by BeaconWatchdog, InterconnectionServer, ScheduleDowntime
	int totalNumberOfIncomingMessages = 0; // accessed by IocConnection
	int totalNumberOfOutgoingMessages = 0; // accessed by IocConnection

	private IocConnectionManager() {
		connectionList = new Hashtable<String, IocConnection>();
	}

	// TODO: should this really be a singleton?
	public static synchronized IocConnectionManager getInstance() {
		if (statisticInstance == null) {
			statisticInstance = new IocConnectionManager();
		}
		return statisticInstance;
	}

	/**
	 * Returns the IOC connection object representing the connection to the IOC
	 * on the specified host and port.
	 * 
	 * @param host
	 *            the hostname of the IOC.
	 * @param port
	 *            the port from which messages from the IOC are received.
	 * @return the IOC connection.
	 */
	public IocConnection getIocConnection(String host, int port) {
		String internalId = host + ":" + port;
		if (connectionList.containsKey(internalId)) {
			return connectionList.get(internalId);
		} else {
			IocConnection connection = new IocConnection(host, port, TimeUtil.systemClock());
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

		Enumeration connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = (IocConnection) connections
					.nextElement();
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

		Enumeration connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = (IocConnection) connections
					.nextElement();
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
		boolean first = true;

		Enumeration connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = (IocConnection) connections
					.nextElement();
			nodeNames.add(thisContent.getHost());
		}
		return nodeNames.toArray(new String[0]);

	}

	public String[] getNodeNameArrayWithLogicalName() {
		List<String> nodeNames = new ArrayList<String>();
		boolean first = true;

		// just in case no enum is possible
		Enumeration connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = (IocConnection) connections
					.nextElement();
			nodeNames.add(thisContent.getHost() + "|"
					+ thisContent.getLogicalIocName());
		}
		return nodeNames.toArray(new String[0]);

	}

	public String[] getNodeNameStatusArray() {
		List<String> nodeNames = new ArrayList<String>();
		boolean first = true;

		// just in case no enum is possible
		Enumeration connections = this.connectionList.elements();
		while (connections.hasMoreElements()) {
			IocConnection thisContent = (IocConnection) connections
					.nextElement();
			nodeNames.add(thisContent.getHost() + " | "
					+ thisContent.getLogicalIocName() + "  "
					+ thisContent.getCurrentConnectState() + "  "
					+ thisContent.getCurrentSelectState());
		}
		return nodeNames.toArray(new String[0]);

	}

}
