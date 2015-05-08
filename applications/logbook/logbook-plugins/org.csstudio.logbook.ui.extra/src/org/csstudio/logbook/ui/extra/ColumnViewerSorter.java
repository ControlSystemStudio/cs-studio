/**
 *
 */
package org.csstudio.logbook.ui.extra;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author shroffk
 *
 */

public abstract class ColumnViewerSorter extends ViewerComparator {
    public static final int ASC = 1;

    public static final int NONE = 0;

    public static final int DESC = -1;

    private int direction = 0;

    private final GridViewerColumn column;

    private final ColumnViewer viewer;

    public ColumnViewerSorter(ColumnViewer viewer, GridViewerColumn column) {
    this.viewer = viewer;
    this.column = column;
    this.column.getColumn().addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        if (ColumnViewerSorter.this.viewer.getComparator() != null) {
            if (ColumnViewerSorter.this.viewer.getComparator() == ColumnViewerSorter.this) {
            int tdirection = ColumnViewerSorter.this.direction;

            if (tdirection == ASC) {
                setSorter(ColumnViewerSorter.this, DESC);
            } else if (tdirection == DESC) {
                setSorter(ColumnViewerSorter.this, NONE);
            }
            } else {
            setSorter(ColumnViewerSorter.this, ASC);
            }
        } else {
            setSorter(ColumnViewerSorter.this, ASC);
        }
        }
    });
    }

    protected void setSorter(ColumnViewerSorter sorter, int direction) {
    if (direction == NONE) {
        column.getColumn().setSort(SWT.NONE);
        viewer.setComparator(null);
    } else {
        sorter.direction = direction;

        if (direction == ASC) {
        column.getColumn().setSort(SWT.DOWN);
        } else {
        column.getColumn().setSort(SWT.UP);
        }

        if (viewer.getComparator() == sorter) {
        viewer.refresh();
        } else {
        viewer.setComparator(sorter);
        }
    }
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
    return direction * doCompare(viewer, e1, e2);
    }

    protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
}
