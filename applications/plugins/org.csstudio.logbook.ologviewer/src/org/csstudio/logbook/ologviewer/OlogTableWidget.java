package org.csstudio.logbook.ologviewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.csstudio.logbook.ologviewer.OlogQuery.Result;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import edu.msu.nscl.olog.api.Log;

public class OlogTableWidget extends Composite implements ISelectionProvider {

	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	private Collection<Log> logs = new ArrayList<Log>();
	private Collection<OlogTableColumnDescriptor> tableViewerColumnDescriptors;
	private Log selectedLog;

	private TableViewer tableViewer;
	private ErrorBar errorBar;
	private Table table;

	private AbstractSelectionProviderWrapper selectionProvider;

	public OlogTableWidget(Composite parent, int style) {
		super(parent, style);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		tableViewer = new TableViewer(composite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.SINGLE | SWT.WRAP
				| SWT.DOUBLE_BUFFERED);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer.setContentProvider(new OlogContentProvider());

		addPropertyChangeListener(new PropertyChangeListener() {

			List<String> properties = Arrays.asList("logs",
					"tableViewerColumnDescriptors");

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (properties.contains(evt.getPropertyName())) {
					updateTable();
				}
			}

		});

		selectionProvider = new AbstractSelectionProviderWrapper(tableViewer,
				this) {
			@Override
			protected ISelection transform(IStructuredSelection selection) {
				if (selection != null && selection.size() == 1)
					return new StructuredSelection(
							(Log) selection.getFirstElement());
				else
					return new StructuredSelection();
			}
		};
	}

	private Composite composite;

	private OlogQuery ologQuery;

	private void updateTable() {
		TableColumn[] columns = table.getColumns();
		for (TableColumn tableColumn : columns) {
			tableColumn.dispose();
		}

		tableViewer.setInput(logs.toArray());
		tableViewer.setItemCount(logs.size());

		TableColumnLayout layout = new TableColumnLayout();
		composite.setLayout(layout);

		// create columns
		TableViewerColumn ologDateColumn = new TableViewerColumn(tableViewer,
				SWT.LEFT);
		ologDateColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((Log) cell.getElement()).getCreatedDate()
						.toString());
			}
		});
		TableColumn tblclmnChannelName = ologDateColumn.getColumn();
		tblclmnChannelName.setWidth(100);
		tblclmnChannelName.setText("Date");
		layout.setColumnData(tblclmnChannelName, new ColumnWeightData(30));

		TableViewerColumn ologDescriptionColumn = new TableViewerColumn(
				tableViewer, SWT.MULTI | SWT.WRAP);
		ologDescriptionColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((Log) cell.getElement()).getDescription());
			}
		});
		TableColumn tblclmnOlogDescriptionName = ologDescriptionColumn
				.getColumn();
		tblclmnOlogDescriptionName.setWidth(100);
		tblclmnOlogDescriptionName.setText("Description");
		layout.setColumnData(tblclmnOlogDescriptionName, new ColumnWeightData(
				70));

		// Add the additional columns
		if (tableViewerColumnDescriptors != null) {
			for (OlogTableColumnDescriptor ologTableColumnDescriptor : tableViewerColumnDescriptors) {
				TableViewerColumn ologColumn = new TableViewerColumn(
						tableViewer, SWT.CENTER);
				ologColumn.setLabelProvider(ologTableColumnDescriptor
						.getCellLabelProvider());
				TableColumn tblclmn = ologColumn.getColumn();
				tblclmn.setAlignment(SWT.CENTER);
				tblclmn.setWidth(100);
				tblclmn.setText(ologTableColumnDescriptor.getText());
				tblclmn.setToolTipText(ologTableColumnDescriptor.getTooltip());
				layout.setColumnData(tblclmn, new ColumnWeightData(
						ologTableColumnDescriptor.getWeight()));
			}
		}

		composite.layout();
		tableViewer.refresh();
	}

	public void setOlogQuery(OlogQuery ologQuery) {
		// If new query is the same, don't change -- you would re-trigger the
		// query for nothing
		if (getOlogQuery() != null && getOlogQuery().equals(ologQuery))
			return;
		if (getOlogQuery() == null && ologQuery == null)
			return;

		OlogQuery oldValue = getOlogQuery();
		if (oldValue != null) {
			oldValue.removeOlogQueryListener(queryListener);
		}

		queryCleared();

		if (ologQuery != null) {
			ologQuery.execute(queryListener);
		}

		this.ologQuery = ologQuery;
	}

	private OlogQuery getOlogQuery() {
		return ologQuery;
	}

	private void queryCleared() {
		errorBar.setException(null);
		setLogs(new ArrayList<Log>());
	}

	OlogQueryListener queryListener = new OlogQueryListener() {

		@Override
		public void queryExecuted(final Result result) {
			SWTUtil.swtThread().execute(new Runnable() {
				@Override
				public void run() {
					Exception e = result.exception;
					errorBar.setException(e);
					if (e == null) {
						setLogs(result.logs);
					}
				}
			});
		}
	};

	public Collection<Log> getLogs() {
		return logs;
	}

	void setLogs(Collection<Log> logs) {
		Collection<Log> oldValue = this.logs;
		this.logs = logs;
		changeSupport.firePropertyChange("logs", oldValue, this.logs);
	}

	public Collection<OlogTableColumnDescriptor> getTableViewerColumnDescriptors() {
		return tableViewerColumnDescriptors;
	}

	public void setTableViewerColumnDescriptors(
			Collection<OlogTableColumnDescriptor> tableViewerColumnDescriptors) {
		Collection<OlogTableColumnDescriptor> oldValue = this.tableViewerColumnDescriptors;
		this.tableViewerColumnDescriptors = tableViewerColumnDescriptors;
		changeSupport.firePropertyChange("tableViewerColumnDescriptors",
				oldValue, this.tableViewerColumnDescriptors);
	}

	public void setTableViewerColumnDescriptors(
			OlogTableColumnDescriptor tableViewerColumnDescriptor) {
		Collection<OlogTableColumnDescriptor> oldValue = this.tableViewerColumnDescriptors;
		this.tableViewerColumnDescriptors.add(tableViewerColumnDescriptor);
		changeSupport.firePropertyChange("tableViewerColumnDescriptors",
				oldValue, this.tableViewerColumnDescriptors);
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
		table.setMenu(menu);
	}


}
