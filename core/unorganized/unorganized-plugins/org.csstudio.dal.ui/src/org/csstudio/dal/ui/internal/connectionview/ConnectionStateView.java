/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.dal.ui.internal.connectionview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.csstudio.dal.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.dal.ui.util.LayoutUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IConnector;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

/**
 * A view that displays the state of all channel connections that were opened
 * using the {@link IProcessVariableConnectionService} that has been created via
 * {@link ProcessVariableConnectionServiceFactory#getDefault()}.
 *
 * @author Sven Wende
 *
 */
public final class ConnectionStateView extends ViewPart {
	/**
	 * The view´s ID.
	 */
	public static final String VIEW_ID = "org.csstudio.platform.ui.ConnectionStateView";

	/**
	 * The viewer.
	 */
	private TableViewer _viewer;

	/**
	 * A UI job used to refresh the view at a fixed delay.
	 */
	private UIJob _updateJob;

	private SortDirection _sortDirection = SortDirection.BY_NAME;

	class FilterByConnectionStateMouseListener extends MouseAdapter {
		private final Set<ConnectionState> _connectionStates;

		public FilterByConnectionStateMouseListener(
				final Set<ConnectionState> connectionStates) {
			assert connectionStates != null;
			_connectionStates = connectionStates;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseUp(final MouseEvent e) {
			final ViewerFilter filter = new ViewerFilter() {
				@Override
				public boolean select(final Viewer viewer, final Object parentElement,
						final Object element) {
					final IConnector s = (IConnector) element;
					return _connectionStates.contains(s
							.getLatestConnectionState());
				}
			};

			_viewer.setFilters(new ViewerFilter[] { filter });
		}
	}

	// private Button createFilterButton(Composite parent, String label,
	// ConnectionState... connectionStates) {
	//
	// Button button = new Button(parent, SWT.NONE);
	// button.setText(label);
	// button
	// .addMouseListener(new FilterByConnectionStateMouseListener(
	// states));
	//
	// return button;
	// }

	class FilterButton {
		private final Button _swtButton;
		private final Set<ConnectionState> _connectionStates;
		private final String _baseLabel;

		public FilterButton(final Composite parent, final int style, final String label,
				final ConnectionState... connectionStates) {
			_swtButton = new Button(parent, style);
			assert connectionStates != null;
			assert label != null;

			// remember label
			_baseLabel = label;

			// remember connection states
			_connectionStates = new HashSet<ConnectionState>();
			for (final ConnectionState s : connectionStates) {
				_connectionStates.add(s);
			}

			// add action listener
			_swtButton.addMouseListener(new FilterByConnectionStateMouseListener(
					_connectionStates));

			// initialize label
			refreshLabel(Collections.EMPTY_LIST);

		}

		public void refreshLabel(final List<IConnector> statistics) {
			assert statistics != null;

			int count = 0;

			if (statistics != null) {
				for (final IConnector s : statistics) {
					if (_connectionStates
							.contains(s.getLatestConnectionState())) {
						count++;
					}
				}
			}
			_swtButton.setText(_baseLabel + " (" + count + ")");
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		// layout
		parent.setLayout(new GridLayout());

		// create button panel
		final Composite buttons = new Composite(parent, SWT.NONE);
		buttons.setLayout(new FillLayout());
		buttons.setLayoutData(LayoutUtil
				.createGridDataForHorizontalFillingCell());

		final FilterButton buttonAll = new FilterButton(buttons, SWT.FLAT, "all",
				ConnectionState.values());
		final FilterButton buttonConnected = new FilterButton(buttons, SWT.FLAT,
				"connected", ConnectionState.CONNECTED);
		final FilterButton buttonNotConnected = new FilterButton(buttons, SWT.FLAT,
				"not connected", ConnectionState.CONNECTION_LOST,
				ConnectionState.INITIAL, ConnectionState.CONNECTION_FAILED,
				ConnectionState.DISCONNECTED, ConnectionState.UNKNOWN);

		// create the viewer and ...
		_viewer = createChannelTable(parent);

		// .. initialize layout
		_viewer.getControl().setLayoutData(
				LayoutUtil.createGridDataForFillingCell());

		// .. initialize content provider
		_viewer.setContentProvider(new IStructuredContentProvider(){
			/**
			 * {@inheritDoc}
			 */
			@Override
            public void inputChanged(final Viewer viewer, final Object oldInput,
					final Object newInput) {

			}

			/**
			 * {@inheritDoc}
			 */
			@Override
            public Object[] getElements(final Object parent) {
				final List<IConnector> statistics =((IProcessVariableConnectionService) parent)
						.getConnectors();

				// refresh the filter button states
				buttonAll.refreshLabel(statistics);
				buttonConnected.refreshLabel(statistics);
				buttonNotConnected.refreshLabel(statistics);

				return statistics.toArray();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
            public void dispose() {

			}

		});

		// ... initialize label provider
		_viewer.setLabelProvider(new LabelProvider());
		_viewer.setInput(ProcessVariableConnectionServiceFactory.getDefault()
				.getProcessVariableConnectionService());

		// ... initialize comparators (important for sorting)
		_viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				return _sortDirection.getComparator().compare(
						(IConnector) e1, (IConnector) e2);
			}
		});

		// ... initialize tooltip support
		ColumnViewerToolTipSupport.enableFor(_viewer, ToolTip.NO_RECREATE);

		// ... initialize context menu
		final MenuManager menuManager = new MenuManager();
		// menuManager.addMenuListener(new IMenuListener() {
		//
		// public void menuAboutToShow(final IMenuManager manager) {
		// manager.add(new GroupMarker(
		// IWorkbenchActionConstants.MB_ADDITIONS));
		// }
		// });

		menuManager
				.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		_viewer.getControl().setMenu(
				menuManager.createContextMenu(_viewer.getControl()));

		getViewSite().registerContextMenu(menuManager, _viewer);
		getViewSite().setSelectionProvider(_viewer);

		// initialize the update job
		_updateJob = new UIJob("Update Connector State View") {
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				_viewer.refresh();
				if (!monitor.isCanceled()) {
					_updateJob.schedule(2000);
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			}
		};

		_updateJob.schedule(5000);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		_updateJob.cancel();
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}

	private TableViewer createChannelTable(final Composite parent) {
		// define column names
		final String[] columnNames = new String[] {
				"PROP_DESCRIPTION", "PROP_NAME", "PROP_VALUE" }; //$NON-NLS-1$ //$NON-NLS-2$

		// create table
		final Table table = new Table(parent, SWT.FULL_SELECTION | SWT.MULTI
				| SWT.HIDE_SELECTION | SWT.DOUBLE_BUFFERED | SWT.SCROLL_PAGE);

		table.setLinesVisible(true);
		table.setLayoutData(LayoutUtil.createGridDataForFillingCell());
		table.setHeaderVisible(true);

		// create viewer
		final TableViewer viewer = new TableViewer(table);

		TableViewerColumn tvColumn;
		tvColumn = new TableViewerColumn(viewer, SWT.NONE);
		tvColumn.getColumn().setText("Channel");
		tvColumn.getColumn().setMoveable(false);
		tvColumn.getColumn().setWidth(300);

		tvColumn = new TableViewerColumn(viewer, SWT.NONE);
		tvColumn.getColumn().setText("Connection State");
		tvColumn.getColumn().setMoveable(false);
		tvColumn.getColumn().setWidth(200);

		tvColumn = new TableViewerColumn(viewer, SWT.NONE);
		tvColumn.getColumn().setText("Latest Value");
		tvColumn.getColumn().setMoveable(false);
		tvColumn.getColumn().setWidth(200);

		viewer.setUseHashlookup(true);

		// define column properties
		viewer.setColumnProperties(columnNames);

		// DnD
		ProcessVariableExchangeUtil.addProcessVariableAdressDragSupport(viewer
				.getControl(), DND.DROP_MOVE | DND.DROP_COPY,
				new IProcessVariableAdressProvider() {
					@Override
                    public List<IProcessVariableAddress> getProcessVariableAdresses() {
						final List<IProcessVariableAddress> result = new ArrayList<IProcessVariableAddress>();

						final IStructuredSelection sel = (IStructuredSelection) viewer
								.getSelection();
						final Iterator<IProcessVariableAdressProvider> it = sel
								.iterator();

						while (it.hasNext()) {
							result.add(it.next().getPVAdress());
						}

						return result;
					}

					@Override
                    public IProcessVariableAddress getPVAdress() {
						final IStructuredSelection sel = (IStructuredSelection) viewer
								.getSelection();
						final IProcessVariableAdressProvider pvProvider = (IProcessVariableAdressProvider) sel
								.getFirstElement();
						return pvProvider != null ? pvProvider.getPVAdress()
								: null;
					}

				});

		return viewer;
	}

	class ChangeSortDirectionListener implements SelectionListener {
		private final SortDirection _sortDirection;

		public ChangeSortDirectionListener(final SortDirection sortDirection) {
			assert sortDirection != null;
			_sortDirection = sortDirection;
		}

		@Override
        public void widgetDefaultSelected(final SelectionEvent e) {

		}

		@Override
        public void widgetSelected(final SelectionEvent e) {
			ConnectionStateView.this._sortDirection = this._sortDirection;
		}
	}

}
