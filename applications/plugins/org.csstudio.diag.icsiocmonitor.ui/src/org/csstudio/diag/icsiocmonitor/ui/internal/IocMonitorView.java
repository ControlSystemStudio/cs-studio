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
import java.util.List;

import org.csstudio.diag.icsiocmonitor.service.IocConnectionState;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.IReportListener;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.IocMonitor;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.IocMonitorFactory;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.MonitorItem;
import org.csstudio.diag.icsiocmonitor.ui.internal.model.MonitorReport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * A view which displays the state of all IOCs as reported by the
 * Interconnection Servers.
 * 
 * @author Joerg Rathlev
 */
public class IocMonitorView extends ViewPart implements IReportListener {
	
	/**
	 * Width of the column which displays the IOC name.
	 */
	private static final int IOC_COLUMN_WIDTH = 250;

	/**
	 * Width of the columns for the individual servers.
	 */
	private static final int SERVER_COLUMN_WIDTH = 120;

	/**
	 * Label provider for the IOC column. Outputs the IOC name and hostname as
	 * the label and color-codes the label based on the IOC state.
	 */
	private static final class IocColumnLabelProvider extends
			ColumnLabelProvider {
		
		@Override
		public String getText(Object element) {
			if (element instanceof MonitorItem) {
				MonitorItem i = (MonitorItem) element;
				return i.getIocName() + " (" +  i.getIocHostname() + ")";
			}
			return "ERROR: element is not of expected type MonitorItem";
		}

		@Override
		public Color getForeground(Object element) {
			if (element instanceof MonitorItem) {
				MonitorItem i = (MonitorItem) element;
				if (!i.isInterconnectionServerSelected()) {
					// no interconnection server is selected
					return systemColor(SWT.COLOR_RED);
				} else if (i.getIocName().startsWith("~")) {
					// the logical IOC name is not configured
					return systemColor(SWT.COLOR_DARK_YELLOW);
				} else {
					// everything is OK
					// FIXME: only if all ICS are connected!
//					return systemColor(SWT.COLOR_DARK_GREEN);
				}
			}
			return null;
		}

		/**
		 * @param id
		 * @return
		 */
		private Color systemColor(int id) {
			return Display.getCurrent().getSystemColor(id);
		}
	}

	/**
	 * Label provider for the Selected ICS column.
	 */
	private static final class SelectedIcsColumnLabelProvider extends
			ColumnLabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof MonitorItem) {
				return ((MonitorItem) element).getSelectedInterconnectionServer();
			}
			return null;
		}
	}
	
	/**
	 * Label provider for the interconnection server columns.
	 */
	private static final class IcsColumnLabelProvider extends
			ColumnLabelProvider {
		
		private final String _server;

		/**
		 * Creates a label provider.
		 * 
		 * @param server
		 *            the name of the interconnection server.
		 */
		IcsColumnLabelProvider(String server) {
			_server = server;
		}
		
		@Override
		public String getText(Object element) {
			if (element instanceof MonitorItem) {
				switch (((MonitorItem) element).getIcsConnectionState(_server)) {
				case CONNECTED:
					return "connected";
				case CONNECTED_SELECTED:
					return "connected, selected";
				case DISCONNECTED:
					return "disconnected";
				default:
					return "ERROR: unknown state";
				}
			}
			return null;
		}
		
		@Override
		public Color getForeground(Object element) {
			if (element instanceof MonitorItem) {
				if (((MonitorItem) element).getIcsConnectionState(_server)
						== IocConnectionState.DISCONNECTED) {
					return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				}
			}
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
			if (inputElement instanceof MonitorReport) {
				return ((MonitorReport) inputElement).getItems().toArray();
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
	
	/**
	 * Action which refreshes the contents of the IOC monitor view.
	 */
	private class RefreshAction extends Action {
		
		/**
		 * Creates the refresh action.
		 */
		RefreshAction() {
			super("Refresh", AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, "icons/refresh.gif"));
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			IocMonitorView.this._iocMonitor.update();
		}
	}

	static final String ID = "org.csstudio.diag.icsiocmonitor.ui.IocMonitorView";

	private TableViewer _tableViewer;
	private List<TableViewerColumn> _dynamicTableColumns;
	private IocMonitor _iocMonitor;
	
	/**
	 * {@inheritDoc}
	 */
	public void onReportUpdated() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setInput(_iocMonitor.getReport());
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
		
		_tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		Table table = _tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		TableViewerColumn col = new TableViewerColumn(_tableViewer, SWT.NONE);
		col.getColumn().setText("IOC");
		col.getColumn().setWidth(IOC_COLUMN_WIDTH);
		col.setLabelProvider(new IocColumnLabelProvider());
		col = new TableViewerColumn(_tableViewer, SWT.NONE);
		col.getColumn().setText("Selected ICS");
		col.getColumn().setWidth(SERVER_COLUMN_WIDTH);
		col.setLabelProvider(new SelectedIcsColumnLabelProvider());
		_dynamicTableColumns = new ArrayList<TableViewerColumn>();
		
		_tableViewer.setContentProvider(new IocMonitorContentProvider());
		initializeIocMonitor();

		getViewSite().getActionBars().getToolBarManager().add(new RefreshAction());
	}

	/**
	 * Runs an initial update of the IOC monitor as a background job. Adds this
	 * object as a listener to the IOC monitor, so that the
	 * {@link #onReportUpdated()} method will be called when the report is
	 * available or updated.
	 */
	private void initializeIocMonitor() {
		Job initializer = new Job("Initializing IOC monitor") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Initializing IOC monitor", IProgressMonitor.UNKNOWN);
				_iocMonitor = IocMonitorFactory.createMonitor();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setInput(_iocMonitor.getReport());
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
	 * @param monitorReport
	 *            the report that will be used as the input.
	 */
	private void setInput(MonitorReport monitorReport) {
		List<String> ics = monitorReport.getInterconnectionServers();
		updateDynamicColumns(ics);
		_tableViewer.setInput(monitorReport);
	}

	/**
	 * Creates the columns for the interconnection servers.
	 * 
	 * @param ics
	 *            list of interconnection servers.
	 */
	private void updateDynamicColumns(List<String> ics) {
		for (TableViewerColumn viewerCol : _dynamicTableColumns) {
			viewerCol.getColumn().dispose();
		}
		
		for (String server : ics) {
			TableViewerColumn viewerCol = new TableViewerColumn(_tableViewer, SWT.NONE);
			viewerCol.getColumn().setText(server);
			viewerCol.getColumn().setWidth(SERVER_COLUMN_WIDTH);
			viewerCol.setLabelProvider(new IcsColumnLabelProvider(server));
			_dynamicTableColumns.add(viewerCol);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
	}
}
