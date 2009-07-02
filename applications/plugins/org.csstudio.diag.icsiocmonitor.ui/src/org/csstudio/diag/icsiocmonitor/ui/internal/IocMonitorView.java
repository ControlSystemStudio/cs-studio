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

package org.csstudio.diag.icsiocmonitor.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.diag.icsiocmonitor.service.IocConnectionState;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.IReportListener;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.IocMonitor;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.IocMonitorFactory;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.MonitorItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * A view which displays the state of all IOCs as reported by the
 * Interconnection Servers.
 * 
 * @author Joerg Rathlev
 */
public class IocMonitorView extends ViewPart implements IReportListener {
	
	/**
	 * @author Joerg Rathlev
	 *
	 */
	private class IocMonitorLabelProvider extends BaseLabelProvider
			implements ITableLabelProvider {

		/**
		 * {@inheritDoc}
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof MonitorItem) {
				MonitorItem item = (MonitorItem) element;
				switch (columnIndex) {
				case 0:
					return item.getIocName() +
							" (" + item.getIocHostname() + ")";
				case 1:
					return item.getSelectedInterconnectionServer();
				default:
					String server = _columnIndexToIcs.get(columnIndex);
					if (server != null) {
						IocConnectionState connectionState =
							item.getIcsConnectionState(server);
						switch (connectionState) {
						case CONNECTED:
							return "connected";
						case CONNECTED_SELECTED:
							return "connected, selected";
						case DISCONNECTED:
							return "disconnected";
						default:
							return "?";
						}
					}
					return "ERROR no server associated with column";
				}
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	/**
	 * @author Joerg Rathlev
	 *
	 */
	private static class IocMonitorContentProvider implements
			IStructuredContentProvider {

		/**
		 * {@inheritDoc}
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof IocMonitor) {
				// XXX: This is a workaround until the report itself is used
				// as the input element for the viewer.
				return ((IocMonitor) inputElement).getReport().getItems().toArray();
			} else {
				return new Object[0];
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void dispose() {
		}

		/**
		 * {@inheritDoc}
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	static final String ID = "org.csstudio.diag.icsiocmonitor.ui.IocMonitorView";

	private TableViewer _tableViewer;
	private Table _table;
	private int _fixedColumnCount;
	private List<TableColumn> _dynamicTableColumns;
	private Map<Integer, String> _columnIndexToIcs;
	private IocMonitor _iocMonitor;
	
	/**
	 * {@inheritDoc}
	 */
	public void onReportUpdated() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setInput(_iocMonitor);
				_tableViewer.refresh();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		
		createTable(parent);
		_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		_tableViewer = new TableViewer(_table);
		_tableViewer.setContentProvider(new IocMonitorContentProvider());
		_tableViewer.setLabelProvider(new IocMonitorLabelProvider());
		
		// Run an initial update in a background Job
		Job initializer = new Job("Initializing IOC monitor") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Initializing IOC monitor", IProgressMonitor.UNKNOWN);
				_iocMonitor = IocMonitorFactory.createMonitor();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setInput(_iocMonitor);
					}
				});
				_iocMonitor.addListener(IocMonitorView.this);
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		final IWorkbenchSiteProgressService progressService =
			(IWorkbenchSiteProgressService) getSite().getAdapter(
					IWorkbenchSiteProgressService.class);
		if (progressService != null) {
			progressService.schedule(initializer);
		} else {
			initializer.schedule();
		}
	}

	/**
	 * Sets the input of the view.
	 * 
	 * @param iocMonitor
	 *            the IOC monitor which will be used as the input.
	 */
	private void setInput(IocMonitor iocMonitor) {
		List<String> ics = iocMonitor.getReport().getInterconnectionServers();
		updateDynamicColumns(ics);
		// TODO: use the report as the input element, not the monitor!
		_tableViewer.setInput(iocMonitor);
	}

	/**
	 * Creates the columns for the interconnection servers.
	 * 
	 * @param ics
	 *            list of interconnection servers.
	 */
	private void updateDynamicColumns(List<String> ics) {
		for (TableColumn col : _dynamicTableColumns) {
			col.dispose();
		}
		
		int i = 0;
		for (String server : ics) {
			TableColumn col = new TableColumn(_table, SWT.LEFT);
			col.setText(server);
			col.setWidth(200);
			_dynamicTableColumns.add(col);
			_columnIndexToIcs.put(_fixedColumnCount + i++, server);
		}
	}

	/**
	 * Creates the table control.
	 */
	private void createTable(Composite parent) {
		_table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		_table.setLinesVisible(true);
		_table.setHeaderVisible(true);
		TableColumn iocCol = new TableColumn(_table, SWT.LEFT);
		iocCol.setText("IOC");
		iocCol.setWidth(200);
		TableColumn selectedIcsCol = new TableColumn(_table, SWT.LEFT);
		selectedIcsCol.setText("Selected ICS");
		selectedIcsCol.setWidth(200);
		
		// IMPORTANT: Update this if the number of fixed columns changes!
		_fixedColumnCount = 2;
		
		_dynamicTableColumns = new ArrayList<TableColumn>();
		_columnIndexToIcs = new HashMap<Integer, String>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
	}
}
