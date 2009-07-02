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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.diag.icsiocmonitor.service.IocConnectionReport;
import org.csstudio.diag.icsiocmonitor.service.IIocConnectionReporter;
import org.csstudio.diag.icsiocmonitor.service.IocConnectionReportItem;
import org.csstudio.diag.icsiocmonitor.service.IocConnectionState;

/**
 * Aggregates the connection states reported by multiple interconnection servers
 * and converts the information into instances of {@link MonitorItem}.
 * 
 * @author Joerg Rathlev
 */
public class IocMonitor {
	
	private List<IIocConnectionReporter> _reporters;
	private List<String> _interconnectionServers;
	private List<MonitorItem> _iocs;
	
	/**
	 * Creates a new IOC monitor. 
	 */
	IocMonitor() {
		_interconnectionServers = new ArrayList<String>();
		_iocs = new ArrayList<MonitorItem>();
		_reporters = new ArrayList<IIocConnectionReporter>();
	}

	/**
	 * Returns the list of interconnection servers.
	 * 
	 * @return the list of interconnection servers. The returned list is
	 *         unmodifiable.
	 */
	public List<String> getInterconnectionServers() {
		return Collections.unmodifiableList(_interconnectionServers);
	}
	
	/**
	 * Returns the list of IOC states.
	 * 
	 * @return the list of IOC states. The returned list is unmodifiable.
	 */
	public List<MonitorItem> getIocStates() {
		return Collections.unmodifiableList(_iocs);
	}

	/**
	 * Adds an IOC state reporter service from which this monitor will receive
	 * reports.
	 * 
	 * @param service
	 *            the service.
	 */
	public void addReporterService(IIocConnectionReporter service) {
		_reporters.add(service);
		update();
	}

	/**
	 * Removes an IOC state reporter service from this monitor.
	 * 
	 * @param service
	 *            the service.
	 */
	public void removeReporterService(IIocConnectionReporter service) {
		_reporters.remove(service);
		update();
	}
	
	/**
	 * Updates this monitor. This monitor will send a request to the reporter
	 * services and then update its state based on the reports received.
	 */
	public void update() {
		_interconnectionServers.clear();
		_iocs.clear();
		
		List<IocConnectionReport> reports = new ArrayList<IocConnectionReport>();
		for (IIocConnectionReporter reporter : _reporters) {
			reports.add(reporter.getReport());
		}
		
		/*
		 * The reports each contain a map IOC -> State, but we want to have a
		 * list of IocState objects each of which aggregates for one IOC the
		 * information from all interconnection servers. The map below maps
		 * IOC -> IocState for the translation.
		 */
		Map<String, MonitorItem> iocStates = new HashMap<String, MonitorItem>();
		for (IocConnectionReport report : reports) {
			String server = report.getReportingServer();
			_interconnectionServers.add(server);
			List<IocConnectionReportItem> reportItems = report.getItems();
			for (IocConnectionReportItem item : reportItems) {
				String iocHostname = item.getIocHostname();
				String iocName = item.getIocName();
				IocConnectionState state = item.getConnectionState();
				MonitorItem iocState = iocStates.get(iocName);
				if (iocState == null) {
					iocState = new MonitorItem(iocHostname, iocName);
					iocStates.put(iocName, iocState);
				}
				iocState.setIcsConnectionState(server, state);
			}
		}
		_iocs.addAll(iocStates.values());
	}
}
