package org.csstudio.utility.toolbox.framework;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

public interface ColumnCreator {

	TableViewerColumn create(TableViewer tableViewer, int colNumber);
}
