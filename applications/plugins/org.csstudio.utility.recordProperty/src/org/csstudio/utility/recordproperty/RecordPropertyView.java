package org.csstudio.utility.recordproperty;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.recordproperty.rdb.data.RecordPropertyGetRDB;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * RecordPropertyView creates view for the plugin.
 * 
 * @author rpovsic
 */
public class RecordPropertyView extends ViewPart {
	
	public static final String ID = "org.csstudio.utility.recordproperty";
	
	/**
	 * The logger.
	 */
	private final CentralLogger _logger = CentralLogger.getInstance();
	
	static final int COL_PV = 0;
	static final int COL_RDB = 1;
	static final int COL_VAL = 2;
	static final int COL_RMI = 3;
	
	private TableViewer tableViewer;
	
	/**
	 * Prints name of a record.
	 */
	private Label label;
	
	/**
	 * Column names.
	 */
	static final String[] COLUMN_NAMES =
		new String[] { "pv", "rdb", "val", "rmi" };
	
	/**
	 * Data, that is filled in table.
	 */
	public RecordPropertyEntry[] entries;
	
	public RecordPropertyView() {
	}
	
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		
		Label label = new Label(parent, SWT.CENTER);
		label.setBounds(50, 100, 1000, 200);
		label.setText("PV name");
		
		createTableViewer(parent);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new RecordPropertyContentProvider());
		tableViewer.setLabelProvider(new RecordPropertyLabelProvider());
		
		startRecordPropertyRequest();
		
		getSite().setSelectionProvider(tableViewer);
	}
	
	private void startRecordPropertyRequest() {
		RecordPropertyGetRDB rdb = new RecordPropertyGetRDB();
		
		entries = rdb.getData();
		tableViewer.setInput(entries);
		
		/*
		Job job = new Job("") {
			
			protected IStatus run(IProgressMonitor monitor) {
				
				return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
			}
		};
		*/
	}
	
	/**
	 * Creates a table.
	 * @param parent
	 */
	private void createTableViewer(final Composite parent) {
		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setColumnProperties(COLUMN_NAMES);
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		addColumn(table, COL_PV,  Messages.RecordPropertyView_PV_COLUMN, 180);
		addColumn(table, COL_RDB, Messages.RecordPropertyView_RDB_COLUMN, 150);
		addColumn(table, COL_VAL, Messages.RecordPropertyView_VAL_COLUMN, 150);
		addColumn(table, COL_RMI, Messages.RecordPropertyView_RMI_COLUMN, 150);
		
	}
	
	/**
	 * Adds a column into a table.
	 * @param table a table
	 * @param index number of column
	 * @param text name of column
	 * @param width width of column
	 */
	private void addColumn(final Table table, final int index, final String text, final int width) {
		TableColumn column = new TableColumn(table, SWT.LEFT, index);
		column.setText(text);
		column.setWidth(width);
	}
		
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
}
