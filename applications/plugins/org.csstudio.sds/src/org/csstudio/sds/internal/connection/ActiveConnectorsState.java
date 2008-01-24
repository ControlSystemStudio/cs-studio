/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.sds.internal.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.connection.dal.SystemConnector;

/**
 * A bean class that encapsulates information about the connectors that are
 * registered for certain process variables.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ActiveConnectorsState {

	/**
	 * The state information of all registered channel references.
	 */
	private HashMap<IProcessVariableAddress, List<SystemConnector>> _connectors;

	/**
	 * Constructor.
	 */
	public ActiveConnectorsState() {
		_connectors = new HashMap<IProcessVariableAddress, List<SystemConnector>>();
	}

	/**
	 * Stores information for a dynamic value listener, which is connected to
	 * the specified DAL property.
	 * 
	 * @param reference
	 *            a DAL property reference
	 * @param connector
	 *            the listener
	 */
	public void addConnector(final IProcessVariableAddress reference,
			final SystemConnector connector) {
		
		List<SystemConnector> listeners = _connectors.get(reference);
		
		if(listeners==null) {
			listeners = new ArrayList<SystemConnector>();
			_connectors.put(reference, listeners);
		}
		
		listeners.add(connector);
	}

	/**
	 * Removes the state information for the specified connector.
	 * 
	 * @param reference
	 *            a DAL property reference
	 * @param connector
	 *            a listener, which was connected to the specified property
	 */
	public void removeConnector(final IProcessVariableAddress reference,
			final SystemConnector connector) {
		if (_connectors.containsKey(reference)) {
			List<SystemConnector> connectors = _connectors.get(reference);
			if (connectors.contains(connector)) {
				connectors.remove(connector);
			}

			if (connectors.size() <= 0) {
				_connectors.remove(reference);
			}
		}
	}

	/**
	 * Return the connectors for the given channel reference.
	 * 
	 * @param channel
	 *            a channel reference.
	 * 
	 * @return the connectors for the given channel reference.
	 */
	public List<SystemConnector> getConnectors(final IProcessVariableAddress channel) {
		return _connectors.get(channel);
	}

	/**
	 * Return the state information of all registered channel references.
	 * 
	 * @return the state information of all registered channel references.
	 */
	public HashMap<IProcessVariableAddress, List<SystemConnector>> getConnectors() {
		HashMap<IProcessVariableAddress, List<SystemConnector>> result = new HashMap<IProcessVariableAddress, List<SystemConnector>>();

		for (IProcessVariableAddress reference : _connectors.keySet()) {
			List<SystemConnector> list = new ArrayList<SystemConnector>();
			list.addAll(_connectors.get(reference));

			result.put(reference, list);
		}

		return result;
	}

}
