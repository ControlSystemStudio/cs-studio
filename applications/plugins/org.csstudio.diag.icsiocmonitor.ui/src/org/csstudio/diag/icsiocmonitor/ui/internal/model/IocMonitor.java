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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.diag.icsiocmonitor.service.IIocConnectionReporter;
import org.csstudio.diag.icsiocmonitor.service.IocConnectionReport;
import org.csstudio.diag.icsiocmonitor.service.IocConnectionReportItem;
import org.csstudio.diag.icsiocmonitor.service.IocConnectionState;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Aggregates the connection states reported by multiple interconnection servers
 * and converts the information into instances of {@link MonitorItem}.
 * 
 * @author Joerg Rathlev
 */
public class IocMonitor {
	
	private List<IIocConnectionReporter> _reporters;
	private final List<IReportListener> _reportListeners;
	private MonitorReport _report;
	
	/**
	 * Creates a new IOC monitor. 
	 */
	IocMonitor() {
		_reporters = Collections.emptyList();
		_reportListeners = new CopyOnWriteArrayList<IReportListener>();
		
		// initial report is simply an empty report
		_report = new MonitorReport(Collections.<String>emptyList(),
				Collections.<MonitorItem>emptyList());
	}
	
	/**
	 * Returns the current report.
	 * 
	 * @return the current report.
	 */
	public MonitorReport getReport() {
		return _report;
	}

	/**
	 * Adds a listener to this monitor.
	 * 
	 * @param listener
	 *            the listener.
	 */
	public void addListener(IReportListener listener) {
		_reportListeners.add(listener);
	}

	/**
	 * Removes a listener from this monitor.
	 * 
	 * @param listener
	 *            the listener.
	 */
	public void removeListener(IReportListener listener) {
		_reportListeners.remove(listener);
	}
	
	/**
	 * Notifies the listeners of this monitor that the report was updated.
	 */
	private void fireReportUpdatedEvent() {
		for (IReportListener listener : _reportListeners) {
			listener.onReportUpdated();
		}
	}

	/**
	 * Sets the reporter services that this monitor will use.
	 * 
	 * @param reporters
	 *            the reporters.
	 */
	public void setReporterServices(Collection<IIocConnectionReporter> reporters) {
		/*
		 * Implementation note: This class previously had two methods to add and
		 * remove services, but that didn't work because the reporters are proxy
		 * objects and caused an exception when trying to remove them from the
		 * list, probably because they could not be compared for equality (the
		 * actual error was caused by an InvocationTargetException on toString). 
		 */
		
		_reporters = new ArrayList<IIocConnectionReporter>(reporters);
		update();
	}

	/**
	 * Updates this monitor. This monitor will send a request to the reporter
	 * services and then update its state based on the reports received.
	 */
	public void update() {
		List<String> interconnectionServers = new ArrayList<String>();
		List<MonitorItem> items = new ArrayList<MonitorItem>();
		
		List<IocConnectionReport> reports = new ArrayList<IocConnectionReport>();
		for (IIocConnectionReporter reporter : _reporters) {
			try {
				reports.add(reporter.getReport());
			} catch (RuntimeException e) {
				/*
				 * Unfortunately, the reporter instance is a Proxy object and
				 * calling its toString() method here raises an Exception. Also,
				 * the proxy's InvocationHandler does not provide any public
				 * information about the remote object it calls to. This means
				 * that here, we can only display that an error occurred, but
				 * we don't know on which server.
				 */
				CentralLogger.getInstance().error(this,
						"Could not retrieve report from one of the " +
						"interconnection servers.", e);
			}
		}

		/*
		 * The code below aggregates the reports from the different
		 * interconnection servers and saves the information in instances of
		 * type MonitorItem, which contain the information grouped by IOC (one
		 * MonitorItem per IOC).
		 */
		Map<String, MonitorItem> iocStates = new HashMap<String, MonitorItem>();
		for (IocConnectionReport report : reports) {
			String server = report.getReportingServer();
			interconnectionServers.add(server);
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
		items.addAll(iocStates.values());
		_report = new MonitorReport(interconnectionServers, items);
		fireReportUpdatedEvent();
	}
}
