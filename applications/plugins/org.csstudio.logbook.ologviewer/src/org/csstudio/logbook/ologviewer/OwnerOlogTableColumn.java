package org.csstudio.logbook.ologviewer;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

import edu.msu.nscl.olog.api.Log;

public class OwnerOlogTableColumn implements OlogTableColumnDescriptor {

	@Override
	public TableViewerColumn getTableViewerColumn(TableViewer tableViewer, TableColumnLayout layout) {
		// create columns
		TableViewerColumn ologDateColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		ologDateColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((Log) cell.getElement()).getOwner());
			}
		});
		TableColumn tblclmnChannelName = ologDateColumn.getColumn();
		tblclmnChannelName.setAlignment(SWT.CENTER);
		tblclmnChannelName.setWidth(100);
		tblclmnChannelName.setText("Extended Column");
		layout.setColumnData(tblclmnChannelName, new ColumnWeightData(30));

		return ologDateColumn;
	}

}
