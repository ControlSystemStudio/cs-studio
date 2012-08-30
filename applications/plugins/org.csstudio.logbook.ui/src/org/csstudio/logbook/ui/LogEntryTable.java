/**
 * 
 */
package org.csstudio.logbook.ui;

import java.text.DateFormat;
import java.util.Collection;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.TableViewerColumnSorter;

/**
 * @author shroffk
 * 
 */
public class LogEntryTable extends Composite {

	// Model
	Collection<LogEntry> logs;
	LogEntry selectedLogEntry;

	// GUI
	private Table logTable;
	private TableViewer logTableViewer;

	public LogEntryTable(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 2;
		gridLayout.horizontalSpacing = 2;
		setLayout(gridLayout);

		logTableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		logTable = logTableViewer.getTable();
		logTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		logTableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}
		});

		updateTable();
	}

	private void updateTable() {
		// Dispose existing columns
		for (TableColumn column : logTableViewer.getTable().getColumns()) {
			column.dispose();
		}
		// First column is date and the default sort column
		TableColumnLayout channelTablelayout = new TableColumnLayout();
		logTableViewer.getTable().getParent().setLayout(channelTablelayout);

		TableViewerColumn tableViewerColumnDate = new TableViewerColumn(
				logTableViewer, SWT.DOUBLE_BUFFERED);
		new TableViewerColumnSorter(tableViewerColumnDate) {

			@Override
			protected Object getValue(Object o) {
				return ((LogEntry) o).getCreateDate().getTime();
			}
		};
		tableViewerColumnDate.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				LogEntry item = ((LogEntry) element);
				return item == null ? "" : DateFormat.getDateInstance().format(
						item.getCreateDate());
			}
		});
		TableColumn tblclmnDate = tableViewerColumnDate.getColumn();
		tblclmnDate.setWidth(100);
		tblclmnDate.setText("Date");
		channelTablelayout.setColumnData(tblclmnDate, new ColumnWeightData(15));
		// Second column is the first line of the logEntry

		TableViewerColumn tableViewerColumnDescription;
		tableViewerColumnDescription = new TableViewerColumn(logTableViewer,
				SWT.DOUBLE_BUFFERED);
		tableViewerColumnDescription
				.setLabelProvider(new ColumnLabelProvider() {

					public String getText(Object element) {
						LogEntry item = ((LogEntry) element);
						return item == null ? "" : item.getText();
					}
				});
		TableColumn tblclmnDescription;
		tblclmnDescription = tableViewerColumnDescription.getColumn();
		tblclmnDescription.setWidth(500);
		tblclmnDescription.setText("Description");
		channelTablelayout.setColumnData(tblclmnDescription,
				new ColumnWeightData(60));

		// Third column is the owner of the logEntry

		TableViewerColumn tableViewerColumnOwner;
		tableViewerColumnOwner = new TableViewerColumn(logTableViewer,
				SWT.DOUBLE_BUFFERED);
		tableViewerColumnOwner.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				LogEntry item = ((LogEntry) element);
				return item == null ? "" : item.getOwner();
			}
		});
		TableColumn tblclmnOwner;
		tblclmnOwner = tableViewerColumnDescription.getColumn();
		tblclmnOwner.setWidth(100);
		tblclmnOwner.setText("Owner");
		channelTablelayout
				.setColumnData(tblclmnOwner, new ColumnWeightData(15));

		// Forth column lists the logbooks
		TableViewerColumn tableViewerColumnLogbooks;
		tableViewerColumnLogbooks = new TableViewerColumn(logTableViewer,
				SWT.DOUBLE_BUFFERED);
		tableViewerColumnLogbooks.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				LogEntry item = ((LogEntry) element);
				StringBuilder logbooks = new StringBuilder();
				for (Logbook logbook : item.getLogbooks()) {
					logbooks.append(logbook.getName() + "\n");
				}
				return item == null ? "" : logbooks.toString();
			}
		});
		TableColumn tblclmnLogbooks;
		tblclmnLogbooks = tableViewerColumnDescription.getColumn();
		tblclmnLogbooks.setWidth(100);
		tblclmnLogbooks.setText("Logbooks");
		channelTablelayout.setColumnData(tblclmnLogbooks, new ColumnWeightData(
				15));
		// Now additional Columns are created based on the selected
		// tags/properties the users wishes to view
	}
}
