package org.csstudio.logbook.ologviewer;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

public interface OlogTableColumnDescriptor {

	TableViewerColumn getTableViewerColumn(TableViewer tableViewer,
			TableColumnLayout layout);

}
