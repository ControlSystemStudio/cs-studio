package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.PVManager;

public class ToolboxView extends ViewPart {

	public static final String ID = "org.csstudio.utility.pvmanager.ui.debug.ChannelListView"; //$NON-NLS-1$
	private Table table;
	private Action selectDataSourceAction;
	private TableViewer tableViewer;
	private Action refreshAction;
	private Table summaryTable;
	private TableViewer summaryTableViewer;

	public ToolboxView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		Composite container = new Composite(parent, SWT.NONE);
		FormData fd_container = new FormData();
		fd_container.bottom = new FormAttachment(100, -10);
		fd_container.left = new FormAttachment(0, 10);
		container.setLayoutData(fd_container);
		//container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(new DataSourceContentProvider());
		
		// Connected column
		
		TableViewerColumn connectedViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		connectedViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			Image connected = ResourceManager.getPluginImageDescriptor("org.csstudio.utility.pvmanager.ui.toolbox", "icons/connected.png").createImage();
			Image disconnected = ResourceManager.getPluginImageDescriptor("org.csstudio.utility.pvmanager.ui.toolbox", "icons/disconnected.png").createImage();
			
			@Override
			public void update(ViewerCell cell) {
				if (((DataSourceChannel) cell.getElement()).isConnected()) {
					cell.setImage(connected);
				} else {
					cell.setImage(disconnected);
				}
			}
			
			@Override
			public void dispose() {
				connected.dispose();
				super.dispose();
			}
		});
		TableColumn connectedColumn = connectedViewerColumn.getColumn();
		connectedColumn.setText("C");
		connectedColumn.setToolTipText("Connected or disconnected");
		
		// PV Name column
		TableViewerColumn pvNameViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		pvNameViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((DataSourceChannel) cell.getElement()).getFullChannelName());
			}
		});
		TableColumn pvNameColumn = pvNameViewerColumn.getColumn();
		pvNameColumn.setText("Channel name");
		
		// Total usage count column
		TableViewerColumn totalUsageViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		totalUsageViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(Integer.toString(((DataSourceChannel) cell.getElement()).getTotalUsageCounter()));
			}
		});
		TableColumn totalUsageColumn = totalUsageViewerColumn.getColumn();
		totalUsageColumn.setText("T");
		totalUsageColumn.setToolTipText("Readers + Writers");
		
		// Read usage count column
		TableViewerColumn readUsageViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		readUsageViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(Integer.toString(((DataSourceChannel) cell.getElement()).getReadUsageCounter()));
			}
		});
		TableColumn readUsageColumn = readUsageViewerColumn.getColumn();
		readUsageColumn.setText("R");
		readUsageColumn.setToolTipText("Readers");
		
		// Write usage count column
		TableViewerColumn writeUsageViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		writeUsageViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(Integer.toString(((DataSourceChannel) cell.getElement()).getWriteUsageCounter()));
			}
		});
		TableColumn writeUsageColumn = writeUsageViewerColumn.getColumn();
		writeUsageColumn.setText("W");
		writeUsageColumn.setToolTipText("Writers");
		
		// Layout
		
		TableColumnLayout layout = new TableColumnLayout();
		container.setLayout(layout);
		layout.setColumnData(connectedViewerColumn.getColumn(), new ColumnWeightData(0, 24));
		layout.setColumnData(pvNameViewerColumn.getColumn(), new ColumnWeightData(10, 200));
		layout.setColumnData(totalUsageViewerColumn.getColumn(), new ColumnWeightData(0, 30));
		layout.setColumnData(readUsageViewerColumn.getColumn(), new ColumnWeightData(0, 30));
		layout.setColumnData(writeUsageViewerColumn.getColumn(), new ColumnWeightData(0, 30));
		
		Composite summaryContainer = new Composite(parent, SWT.NONE);
		fd_container.right = new FormAttachment(summaryContainer, 0, SWT.RIGHT);
		fd_container.top = new FormAttachment(summaryContainer, 6);
		FormData fd_summaryContainer = new FormData();
		fd_summaryContainer.bottom = new FormAttachment(0, 153);
		fd_summaryContainer.right = new FormAttachment(100, -10);
		fd_summaryContainer.left = new FormAttachment(0, 10);
		fd_summaryContainer.top = new FormAttachment(0, 10);
		summaryContainer.setLayoutData(fd_summaryContainer);
		summaryTableViewer = new TableViewer(summaryContainer, SWT.BORDER | SWT.FULL_SELECTION);
		summaryTable = summaryTableViewer.getTable();
		summaryTable.setHeaderVisible(true);
		summaryTable.setLinesVisible(true);
		summaryTableViewer.setContentProvider(new DataSourceSummaryContentProvider());

		
		// DataSource Name column
		TableViewerColumn dataSourceNameViewerColumn = new TableViewerColumn(
				summaryTableViewer, SWT.NONE);
		dataSourceNameViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				DataSource source = (DataSource) cell.getElement();
				CompositeDataSource composite = (CompositeDataSource) PVManager.getDefaultDataSource();
				String name = "";
				for (Map.Entry<String, DataSource> entry : composite.getDataSources().entrySet()) {
					if (entry.getValue() == source)
						name = entry.getKey();
				}
				cell.setText(name);
			}
		});
		TableColumn dataSourceNameColumn = dataSourceNameViewerColumn.getColumn();
		dataSourceNameColumn.setText("Data Source");
		
		// Total usage count column
		TableViewerColumn totalChannelsViewerColumn = new TableViewerColumn(
				summaryTableViewer, SWT.NONE);
		totalChannelsViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				DataSource source = (DataSource) cell.getElement();
				cell.setText(Integer.toString(source.getChannels().size()));
			}
		});
		TableColumn totalChannelsUsageColumn = totalChannelsViewerColumn.getColumn();
		totalChannelsUsageColumn.setText("TC");
		totalChannelsUsageColumn.setToolTipText("Connected + Disconnected channels");
		
		// Open channels column
		TableViewerColumn connectedChannelsViewerColumn = new TableViewerColumn(
				summaryTableViewer, SWT.NONE);
		connectedChannelsViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				DataSource source = (DataSource) cell.getElement();
				int count = 0;
				for (ChannelHandler channel : source.getChannels().values()) {
					if (channel.isConnected())
						count++;
				}
				cell.setText(Integer.toString(count));
			}
		});
		TableColumn connectedChannelsUsageColumn = connectedChannelsViewerColumn.getColumn();
		connectedChannelsUsageColumn.setText("CC");
		connectedChannelsUsageColumn.setToolTipText("Connected channels");
		
		// Total usage column
		TableViewerColumn totalViewerColumn = new TableViewerColumn(
				summaryTableViewer, SWT.NONE);
		totalViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				DataSource source = (DataSource) cell.getElement();
				int count = 0;
				for (ChannelHandler channel : source.getChannels().values()) {
					count += channel.getUsageCounter();
				}
				cell.setText(Integer.toString(count));
			}
		});
		TableColumn totalColumn = totalViewerColumn.getColumn();
		totalColumn.setText("T");
		totalColumn.setToolTipText("Readers + Writers");
		
		// Readers column
		TableViewerColumn readersViewerColumn = new TableViewerColumn(
				summaryTableViewer, SWT.NONE);
		readersViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				DataSource source = (DataSource) cell.getElement();
				int count = 0;
				for (ChannelHandler channel : source.getChannels().values()) {
					count += channel.getReadUsageCounter();
				}
				cell.setText(Integer.toString(count));
			}
		});
		TableColumn readersColumn = readersViewerColumn.getColumn();
		readersColumn.setText("R");
		readersColumn.setToolTipText("Readers");
		
		// Writers column
		TableViewerColumn writersViewerColumn = new TableViewerColumn(
				summaryTableViewer, SWT.NONE);
		writersViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				DataSource source = (DataSource) cell.getElement();
				int count = 0;
				for (ChannelHandler channel : source.getChannels().values()) {
					count += channel.getWriteUsageCounter();
				}
				cell.setText(Integer.toString(count));
			}
		});
		TableColumn writersColumn = writersViewerColumn.getColumn();
		writersColumn.setText("W");
		writersColumn.setToolTipText("Writers");
		
		TableColumnLayout summaryLayout = new TableColumnLayout();
		summaryContainer.setLayout(summaryLayout);
		summaryLayout.setColumnData(dataSourceNameViewerColumn.getColumn(), new ColumnWeightData(10, 120));
		summaryLayout.setColumnData(totalChannelsViewerColumn.getColumn(), new ColumnWeightData(0, 60));
		summaryLayout.setColumnData(connectedChannelsViewerColumn.getColumn(), new ColumnWeightData(0, 60));
		summaryLayout.setColumnData(totalViewerColumn.getColumn(), new ColumnWeightData(0, 60));
		summaryLayout.setColumnData(readersViewerColumn.getColumn(), new ColumnWeightData(0, 60));
		summaryLayout.setColumnData(writersViewerColumn.getColumn(), new ColumnWeightData(0, 60));
		
		tableViewer.setInput(DataSourceContentProvider.ALL);
		summaryTableViewer.setInput(DataSourceContentProvider.ALL);

		createActions();
		initializeToolBar();
		initializeMenu();
	}
	
	private MenuItem createDataSourceMenuItem(Menu parent, final Object input) {
		MenuItem item = new MenuItem(parent, SWT.RADIO);
		item.setText(input.toString());
		item.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setInput(input);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		return item;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			// Drop down menu to select data source
			// First selection for All and then each datasource in alphabetical order
			final Menu datasourceSelectionMenu = new Menu(table);
			MenuItem allItem = createDataSourceMenuItem(datasourceSelectionMenu, DataSourceContentProvider.ALL);
			allItem.setSelection(true);
			List<String> dataSourceNames = new ArrayList<String>(((CompositeDataSource) PVManager.getDefaultDataSource()).getDataSources().keySet());
			Collections.sort(dataSourceNames);
			
			for (String dataSourceName : dataSourceNames) {
				MenuItem dataSourceItem = createDataSourceMenuItem(datasourceSelectionMenu, dataSourceName);
			}
			
			selectDataSourceAction = new Action("Select Data Source", SWT.DROP_DOWN) {
				@Override
				public void runWithEvent(Event event) {
					//Point point = event.
					ToolItem toolItem = (ToolItem) event.widget;
					Point point = toolItem.getParent().toDisplay(new Point(toolItem.getBounds().x, toolItem.getBounds().y + toolItem.getBounds().height));
					datasourceSelectionMenu.setLocation(point.x, point.y); // waiting
					datasourceSelectionMenu.setVisible(true);
				}
			};
			selectDataSourceAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.csstudio.utility.pvmanager.ui.toolbox", "icons/source.png"));
			selectDataSourceAction.setToolTipText("Select Data Source");
			selectDataSourceAction.setMenuCreator(new IMenuCreator() {
				
				
				
				@Override
				public Menu getMenu(Menu parent) {
					return datasourceSelectionMenu;
				}
				
				@Override
				public Menu getMenu(Control parent) {
					// TODO Auto-generated method stub
					return datasourceSelectionMenu;
				}
				
				@Override
				public void dispose() {
					datasourceSelectionMenu.dispose();
				}
			});
		}
		{
			refreshAction = new Action("Refresh data") {				@Override
				public void run() {
					tableViewer.refresh();
					summaryTableViewer.refresh();
				}
			};
			refreshAction.setToolTipText("Refresh data");
			refreshAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.csstudio.utility.pvmanager.ui.toolbox", "icons/refresh.png"));
		}
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(refreshAction);
		toolbarManager.add(selectDataSourceAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
