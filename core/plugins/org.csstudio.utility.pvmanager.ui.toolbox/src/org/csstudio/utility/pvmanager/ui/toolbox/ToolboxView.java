package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.layout.TableColumnLayout;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.data.SimpleValueFormat;
import org.epics.pvmanager.data.ValueFormat;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.action.Action;
import org.eclipse.wb.swt.ResourceManager;

public class ToolboxView extends ViewPart {

	public static final String ID = "org.csstudio.utility.pvmanager.ui.debug.ChannelListView"; //$NON-NLS-1$
	private Table table;
	private Action selectDataSourceAction;
	TableViewer tableViewer;
	private Action refreshAction;

	public ToolboxView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
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
		layout.setColumnData(pvNameViewerColumn.getColumn(), new ColumnWeightData(10));
		layout.setColumnData(totalUsageViewerColumn.getColumn(), new ColumnWeightData(0, 30));
		layout.setColumnData(readUsageViewerColumn.getColumn(), new ColumnWeightData(0, 30));
		layout.setColumnData(writeUsageViewerColumn.getColumn(), new ColumnWeightData(0, 30));
		
		tableViewer.setInput(DataSourceContentProvider.ALL);

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
