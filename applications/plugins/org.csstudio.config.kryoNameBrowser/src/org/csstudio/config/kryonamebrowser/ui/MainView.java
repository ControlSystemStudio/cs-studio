package org.csstudio.config.kryonamebrowser.ui;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.util.StringUtil;
import org.csstudio.config.kryonamebrowser.config.OracleSettings;
import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoPlantResolved;
import org.csstudio.config.kryonamebrowser.ui.filter.FilterComposite;
import org.csstudio.config.kryonamebrowser.ui.handler.DeleteCommand;
import org.csstudio.config.kryonamebrowser.ui.handler.EditCommand;
import org.csstudio.config.kryonamebrowser.ui.provider.KryoNameContentProvider;
import org.csstudio.config.kryonamebrowser.ui.provider.KryoNameLabelProvider;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import com.sun.org.apache.bcel.internal.generic.CPInstruction;

/**
 * View for the filter and table of names.
 * 
 * @author Alen Vrecko
 * 
 */
public class MainView extends ViewPart {

	public static final String ID = MainView.class.getName();

	private KryoNameBrowserLogic logic;

	private TableViewer viewer;

	private FilterComposite filter;

	public TableViewer getViewer() {
		return viewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		logic = new KryoNameBrowserLogic(new OracleSettings());

		try {
			logic.openConnection();
		} catch (SQLException e) {
			// TODO: maybe log?
			throw new RuntimeException(e);
		}

		// set layout
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		filter = new FilterComposite(parent, SWT.NONE);
		getFilter().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false));

		// add table at the bottom
		createTable(parent);

		// bind table viewer to filter
		getFilter().setViewer(viewer);
		// bind logic and dropdowns
		getFilter().setLogic(logic);
		initializeToolBar();

	}

	@Override
	public void dispose() {
		super.dispose();
		try {
			logic.closeConnection();
		} catch (SQLException e) {
			// TODO: maybe log? we can't do nothing
		}
	}

	private void createTable(Composite parent) {

		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		final Table table = new Table(parent, SWT.HIDE_SELECTION
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);

		table.setHeaderVisible(true);
		final GridData gd_table = new GridData(SWT.LEFT, SWT.FILL, true, true);
		table.setLayoutData(gd_table);

		final TableColumn newColumnTableColumn = new TableColumn(table,
				SWT.CENTER, 0);
		newColumnTableColumn.setWidth(250);
		newColumnTableColumn.setText("Kryo name");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table,
				SWT.CENTER, 1);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText("Plant");

		final TableColumn newColumnTableColumn_2 = new TableColumn(table,
				SWT.CENTER, 2);
		newColumnTableColumn_2.setWidth(60);
		newColumnTableColumn_2.setText("No");

		final TableColumn subPlant1TableColumn = new TableColumn(table,
				SWT.CENTER, 3);
		subPlant1TableColumn.setWidth(100);
		subPlant1TableColumn.setText("Sub plant 1");

		final TableColumn newColumnTableColumn_3 = new TableColumn(table,
				SWT.CENTER, 4);
		newColumnTableColumn_3.setWidth(60);
		newColumnTableColumn_3.setText("No");

		final TableColumn subPlant2TableColumn = new TableColumn(table,
				SWT.CENTER, 5);
		subPlant2TableColumn.setWidth(100);
		subPlant2TableColumn.setText("Sub plant 2");

		final TableColumn newColumnTableColumn_4 = new TableColumn(table,
				SWT.CENTER, 6);
		newColumnTableColumn_4.setWidth(60);
		newColumnTableColumn_4.setText("No");

		final TableColumn subPlant3TableColumn = new TableColumn(table,
				SWT.CENTER, 7);
		subPlant3TableColumn.setWidth(100);
		subPlant3TableColumn.setText("Sub plant 3");

		final TableColumn newColumnTableColumn_5 = new TableColumn(table,
				SWT.CENTER, 8);
		newColumnTableColumn_5.setWidth(60);
		newColumnTableColumn_5.setText("No");

		final TableColumn newColumnTableColumn_6 = new TableColumn(table,
				SWT.CENTER, 9);
		newColumnTableColumn_6.setWidth(100);
		newColumnTableColumn_6.setText("Object");

		final TableColumn newColumnTableColumn_7 = new TableColumn(table,
				SWT.CENTER, 10);
		newColumnTableColumn_7.setWidth(100);
		newColumnTableColumn_7.setText("Object Function");

		final TableColumn newColumnTableColumn_8 = new TableColumn(table,
				SWT.CENTER, 11);
		newColumnTableColumn_8.setWidth(100);
		newColumnTableColumn_8.setText("Object subfunction");

		final TableColumn newColumnTableColumn_9 = new TableColumn(table,
				SWT.CENTER, 12);
		newColumnTableColumn_9.setWidth(100);
		newColumnTableColumn_9.setText("Process Part");

		final TableColumn newColumnTableColumn_10 = new TableColumn(table,
				SWT.CENTER, 13);
		newColumnTableColumn_10.setWidth(30);
		newColumnTableColumn_10.setText("Seq No");

		final TableColumn newColumnTableColumn_11 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_11.setWidth(100);
		newColumnTableColumn_11.setText("Description");

		viewer = new TableViewer(table);

		viewer.setContentProvider(new KryoNameContentProvider());
		viewer.setLabelProvider(new KryoNameLabelProvider());

		viewer.setInput(null);

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTable());

		viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

		getSite().setSelectionProvider(viewer);

		final IHandlerService handlerService = (IHandlerService) getSite()
				.getService(IHandlerService.class);

		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.DEL) {

					try {
						handlerService.executeCommand(DeleteCommand.ID, null);
					} catch (Exception ex) {
						throw new RuntimeException("Delete command not found");
					}
				}
				if (table.getSelectionCount() == 1
						&& (e.character == SWT.CR || e.character == SWT.KEYPAD_CR)) {

					try {
						handlerService.executeCommand(EditCommand.ID, null);
					} catch (Exception ex) {
						throw new RuntimeException("Edit command not found");
					}
				}

			}
		});

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				try {
					handlerService.executeCommand(EditCommand.ID, null);
				} catch (Exception ex) {
					throw new RuntimeException("Edit command not found");
				}

			}

		});

		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TableColumn column = columns[i];
			final int colIndex = i;

			column.addSelectionListener(new SelectionAdapter() {

				boolean asc = true;

				@Override
				public void widgetSelected(SelectionEvent e) {
					viewer.setComparator(new MyViewComparator(colIndex, asc));
					asc = !asc;
				}
			});
		}

	}

	private static class MyViewComparator extends ViewerComparator {

		private final int column;
		private final int sortOrderMultiplyer;

		public MyViewComparator(int column, boolean asc) {
			super();
			this.column = column;
			if (asc) {
				sortOrderMultiplyer = 1;
			} else {
				sortOrderMultiplyer = -1;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {

			KryoNameResolved first = (KryoNameResolved) e1;
			KryoNameResolved second = (KryoNameResolved) e2;

			switch (column) {
			case 0:
				return sortOrderMultiplyer
						* first.getName().compareTo(second.getName());
			case 1:
				return sortOrderMultiplyer * comparePlants(first, second, 0);
			case 2:
				return sortOrderMultiplyer * comparePlantsNo(first, second, 0);
			case 3:
				return sortOrderMultiplyer * comparePlants(first, second, 1);
			case 4:
				return sortOrderMultiplyer * comparePlantsNo(first, second, 1);
			case 5:
				return sortOrderMultiplyer * comparePlants(first, second, 2);
			case 6:
				return sortOrderMultiplyer * comparePlantsNo(first, second, 2);
			case 7:
				return sortOrderMultiplyer * comparePlants(first, second, 3);
			case 8:
				return sortOrderMultiplyer * comparePlantsNo(first, second, 3);
			case 9:
				return sortOrderMultiplyer * compareObjects(first, second, 0);
			case 10:
				return sortOrderMultiplyer * compareObjects(first, second, 1);
			case 11:
				return sortOrderMultiplyer * compareObjects(first, second, 2);
			case 12:
				return sortOrderMultiplyer
						* first.getProcess().getName().compareTo(
								second.getProcess().getName());
			case 13:
				return sortOrderMultiplyer
						* Double.compare(first.getSeqKryoNumber(), second
								.getSeqKryoNumber());
			case 14:
				return sortOrderMultiplyer
						* String.CASE_INSENSITIVE_ORDER.compare(first
								.getLabel(), second.getLabel());
			default:
				break;
			}

			return 0;

		}

		private int compareObjects(KryoNameResolved first,
				KryoNameResolved second, int depth) {

			List<KryoObjectEntry> firstObjects = first.getObjects();
			List<KryoObjectEntry> secondObjects = second.getObjects();

			if (depth >= firstObjects.size()) {
				return -1;
			}
			if (depth >= secondObjects.size()) {
				return 1;
			}

			return firstObjects.get(depth).getName().compareTo(
					secondObjects.get(depth).getName());

		}

		private int comparePlants(KryoNameResolved first,
				KryoNameResolved second, int depth) {

			List<KryoPlantResolved> firstPlants = first.getPlants();
			List<KryoPlantResolved> secondPlants = second.getPlants();

			if (depth >= firstPlants.size()) {
				return -1;
			}
			if (depth >= secondPlants.size()) {
				return 1;
			}

			return firstPlants.get(depth).getName().compareTo(
					secondPlants.get(depth).getName());

		}

		private int comparePlantsNo(KryoNameResolved first,
				KryoNameResolved second, int depth) {

			List<KryoPlantResolved> firstPlants = first.getPlants();
			List<KryoPlantResolved> secondPlants = second.getPlants();

			if (depth >= firstPlants.size()) {
				return -1;
			}
			if (depth >= secondPlants.size()) {
				return 1;
			}

			return Double.compare(firstPlants.get(depth).getNumberOfPlants(),
					secondPlants.get(depth).getNumberOfPlants());

		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void initializeToolBar() {
		IToolBarManager toolBarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	public KryoNameBrowserLogic getLogic() {
		return logic;
	}

	public FilterComposite getFilter() {
		return filter;
	}

}
