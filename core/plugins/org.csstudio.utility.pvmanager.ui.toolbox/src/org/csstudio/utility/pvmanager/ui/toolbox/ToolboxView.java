package org.csstudio.utility.pvmanager.ui.toolbox;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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

public class ToolboxView extends ViewPart {

	public static final String ID = "org.csstudio.utility.pvmanager.ui.debug.ChannelListView"; //$NON-NLS-1$
	private Table table;

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
		
		TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(new DataSourceContentProvider());
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((ChannelHandler<?>) cell.getElement()).getChannelName());
			}
		});
		TableColumn tblclmnValue = tableViewerColumn_1.getColumn();
		tblclmnValue.setWidth(201);
		tblclmnValue.setText("Value");
		
		TableColumnLayout layout = new TableColumnLayout();
		container.setLayout(layout);
		layout.setColumnData(tableViewerColumn_1.getColumn(), new ColumnWeightData(10));
		
		tableViewer.setInput(((CompositeDataSource) PVManager.getDefaultDataSource()).getDataSources().get("sim"));

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
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
