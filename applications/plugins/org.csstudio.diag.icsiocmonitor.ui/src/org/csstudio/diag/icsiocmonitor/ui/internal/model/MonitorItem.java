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

import java.util.HashMap;
import java.util.Map;

import org.csstudio.diag.icsiocmonitor.service.IocConnectionState;

/**
 * Contains information about an IOC and its connection state to the different
 * interconnection servers. Objects of this class are intended to be displayed
 * as items in the monitor view.
 * 
 * @author Joerg Rathlev
 */
public class MonitorItem {

	private final String _iocName;
	private final String _iocHostname;
	private final Map<String, IocConnectionState> _icsConnections;

	/**
	 * Creates a new IOC state object.
	 * 
	 * @param iocHostname
	 *            the hostname of the IOC.
	 * @param iocName
	 *            the logical name of the IOC.
	 */
	public MonitorItem(String iocHostname, String iocName) {
		_iocHostname = iocHostname;
		_iocName = iocName;
		_icsConnections = new HashMap<String, IocConnectionState>();
	}

	/**
	 * Returns the logical name of the IOC.
	 * 
	 * @return the logical name of the IOC.
	 */
	public String getIocName() {
		return _iocName;
	}
	
	/**
	 * Returns the hostname of the IOC.
	 * 
	 * @return the hostname of the IOC.
	 */
	public String getIocHostname() {
		return _iocHostname;
	}

	/**
	 * Sets the state of the connection to an interconnection server.
	 * 
	 * @param server
	 *            the interconnection server.
	 * @param state
	 *            the connection state.
	 */
	public void setIcsConnectionState(String server, IocConnectionState state) {
		_icsConnections.put(server, state);
	}

	/**
	 * Returns the state of the connection to an interconnection server.
	 * 
	 * @param server
	 *            the interconnection server.
	 * @return the connection state.
	 */
	public IocConnectionState getIcsConnectionState(String server) {
		IocConnectionState result = _icsConnections.get(server);
		return result != null ? result : IocConnectionState.DISCONNECTED;
	}

	/**
	 * Returns the selected interconnection server.
	 * 
	 * @return the selected interconnection server. Returns <code>null</code> if
	 *         no interconnection server is the selected server.
	 */
	public String getSelectedInterconnectionServer() {
		for (Map.Entry<String, IocConnectionState> entry : _icsConnections.entrySet()) {
			if (entry.getValue() == IocConnectionState.CONNECTED_SELECTED) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Returns whether an interconnection server is selected.
	 * 
	 * @return <code>true</code> if an interconnection server is selected,
	 *         <code>false</code> otherwise.
	 */
	public boolean isInterconnectionServerSelected() {
		return getSelectedInterconnectionServer() != null;
	}
}
