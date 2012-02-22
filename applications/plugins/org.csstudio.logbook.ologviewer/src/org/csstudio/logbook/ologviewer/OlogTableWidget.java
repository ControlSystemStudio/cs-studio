package org.csstudio.logbook.ologviewer;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.logbook.ologviewer.OlogQuery.Result;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import edu.msu.nscl.olog.api.Log;

public class OlogTableWidget extends Composite {

	private Collection<Log> logs = new ArrayList<Log>();

	private TableViewer tableViewer;
	private Collection<OlogTableColumnDescriptor> tableViewerColumnDescriptors;

	private ErrorBar errorBar;
	private Table table;

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
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.WRAP
				| SWT.DOUBLE_BUFFERED);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer.setContentProvider(new OlogContentProvider());

		table.addListener(SWT.MeasureItem, paintListener);
		table.addListener(SWT.PaintItem, paintListener);
		table.addListener(SWT.EraseItem, paintListener);
		updateTable();
	}

	/*
	 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
	 * Therefore, it is critical for performance that these methods be as
	 * efficient as possible.
	 */
	Listener paintListener = new Listener() {
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.MeasureItem: {
				TableItem item = (TableItem) event.item;
				String text = getText(item, event.index);
				Point size = event.gc.textExtent(text);
				event.width = size.x;
				event.height = Math.max(event.height, size.y);
				break;
			}
			case SWT.PaintItem: {
				TableItem item = (TableItem) event.item;
				String text = getText(item, event.index);
				Point size = event.gc.textExtent(text);
				int offset2 = event.index == 0 ? Math.max(0,
						(event.height - size.y) / 2) : 0;
				event.gc.drawText(text, event.x, event.y + offset2, true);
				break;
			}
			case SWT.EraseItem: {
				event.detail &= ~SWT.FOREGROUND;
				break;
			}
			}
		}

		String getText(TableItem item, int column) {
			return item.getText(column);
		}

	};
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
				SWT.NONE);
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
				ologTableColumnDescriptor.getTableViewerColumn(tableViewer,
						layout);
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
		this.logs = logs;
		updateTable();
	}

	public Collection<OlogTableColumnDescriptor> getTableViewerColumnDescriptors() {
		return tableViewerColumnDescriptors;
	}

	public void setTableViewerColumnDescriptors(
			Collection<OlogTableColumnDescriptor> tableViewerColumnDescriptors) {
		this.tableViewerColumnDescriptors = tableViewerColumnDescriptors;
		updateTable();
	}

	public void setTableViewerColumnDescriptors(
			OlogTableColumnDescriptor tableViewerColumnDescriptor) {
		this.tableViewerColumnDescriptors.add(tableViewerColumnDescriptor);
		updateTable();
	}

}
