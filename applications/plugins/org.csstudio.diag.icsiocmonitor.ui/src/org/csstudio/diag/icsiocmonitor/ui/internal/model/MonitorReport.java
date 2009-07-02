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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Consolidated report of IOC connection states. Instances of this class contain
 * a consolidated report that contains information received from mulitple
 * interconnection servers and groups the information by IOCs.
 * 
 * @author Joerg Rathlev
 */
public class MonitorReport {
	
	private final List<String> _interconnectionServers;
	private final List<MonitorItem> _items;

	/**
	 * Creates a new report.
	 * 
	 * @param servers
	 *            the interconnection servers.
	 * @param items
	 *            the items of this report.
	 */
	MonitorReport(Collection<String> servers, Collection<MonitorItem> items) {
		_interconnectionServers = new ArrayList<String>(servers);
		_items = new ArrayList<MonitorItem>(items);
	}

	/**
	 * Returns an unmodifiable view of the interconnection servers from which
	 * information is included in this report.
	 * 
	 * @return an unmodifiable list of interconnection servers.
	 */
	public List<String> getInterconnectionServers() {
		return Collections.unmodifiableList(_interconnectionServers);
	}
	
	/**
	 * Returns an unmodifiable view of the items contained in this report.
	 * 
	 * @return an unmodifiable list of items.
	 */
	public List<MonitorItem> getItems() {
		return Collections.unmodifiableList(_items);
	}
	
}
