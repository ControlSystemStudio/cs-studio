/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.util.Collection;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.TableViewerColumnSorter;

/**
 * @author shroffk
 * 
 */
public class LogEntryTable extends Composite implements ISelectionProvider {

	// Model
	Collection<LogEntry> logs;
	LogEntry selectedLogEntry;

	// GUI
	private Table logTable;
	private TableViewer logTableViewer;

	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	private Composite composite;
	private org.csstudio.logbook.ui.AbstractSelectionProviderWrapper selectionProvider;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public LogEntryTable(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 2;
		gridLayout.horizontalSpacing = 2;
		setLayout(gridLayout);

		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);

		logTableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		logTable = logTableViewer.getTable();
		logTable.setHeaderVisible(true);
		logTable.setLinesVisible(true);

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
		selectionProvider = new AbstractSelectionProviderWrapper(
				logTableViewer, this) {

			@Override
			protected ISelection transform(IStructuredSelection selection) {
				return selection;
			}

		};
		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("logs".equals(event.getPropertyName())) {
					updateTable();
					logTableViewer.setInput(logs.toArray());
				} else if ("logs".equals(event.getPropertyName())) {
				}
			}
		});
		updateTable();
	}

	private void updateTable() {
		// Dispose existing columns
		for (TableColumn column : logTableViewer.getTable().getColumns()) {
			column.dispose();
		}
		TableColumnLayout channelTablelayout = (TableColumnLayout) composite
				.getLayout();

		// First column is date and the default sort column
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

		TableViewerColumn tableViewerColumnOwner = new TableViewerColumn(
				logTableViewer, SWT.DOUBLE_BUFFERED);
		tableViewerColumnOwner.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				LogEntry item = ((LogEntry) element);
				return item == null ? "" : item.getOwner();
			}
		});
		TableColumn tblclmnOwner = tableViewerColumnOwner.getColumn();
		channelTablelayout
				.setColumnData(tblclmnOwner, new ColumnWeightData(15));
		tblclmnOwner.setWidth(100);
		tblclmnOwner.setText("Owner");

		// Forth column lists the logbooks
		TableViewerColumn tableViewerColumnLogbooks = new TableViewerColumn(
				logTableViewer, SWT.DOUBLE_BUFFERED);
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
		TableColumn tblclmnLogbooks = tableViewerColumnLogbooks.getColumn();
		channelTablelayout.setColumnData(tblclmnLogbooks, new ColumnWeightData(
				15));
		tblclmnLogbooks.setWidth(100);
		tblclmnLogbooks.setText("Logbooks");

		// Now additional Columns are created based on the selected
		// tags/properties the users wishes to view
	}

	public Collection<LogEntry> getLogs() {
		return logs;
	}

	public void setLogs(Collection<LogEntry> logs) {
		Collection<LogEntry> oldValue = this.logs;
		this.logs = logs;
		changeSupport.firePropertyChange("logs", oldValue, this.logs);
	}

	public LogEntry getSelectedLogEntry() {
		return selectedLogEntry;
	}

	public void setSelectedLogEntry(LogEntry selectedLogEntry) {
		LogEntry oldValue = this.selectedLogEntry;
		this.selectedLogEntry = selectedLogEntry;
		changeSupport.firePropertyChange("selectedLogEntry", oldValue,
				this.selectedLogEntry);
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionProvider.addSelectionChangedListener(listener);

	}

	@Override
	public ISelection getSelection() {
		return selectionProvider.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionProvider.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		selectionProvider.setSelection(selection);
	}
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		logTable.setMenu(menu);
	}

}
