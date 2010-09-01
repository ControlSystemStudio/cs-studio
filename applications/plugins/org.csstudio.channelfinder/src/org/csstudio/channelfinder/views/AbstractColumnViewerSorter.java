package org.csstudio.channelfinder.views;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


abstract class AbstractColumnViewerSorter extends ViewerComparator {
	public static final int ASC = 1;
	
	public static final int NONE = 0;
	
	public static final int DESC = -1;
	
	private int direction = 0;
	
	private TableViewerColumn column;
	
	private ColumnViewer viewer;
	
	public AbstractColumnViewerSorter(ColumnViewer viewer, TableViewerColumn column) {
		this.column = column;
		this.viewer = viewer;
		this.column.getColumn().addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if( AbstractColumnViewerSorter.this.viewer.getComparator() != null ) {
					if( AbstractColumnViewerSorter.this.viewer.getComparator() == AbstractColumnViewerSorter.this ) {
						int tdirection = AbstractColumnViewerSorter.this.direction;
						
						if( tdirection == ASC ) {
							setSorter(AbstractColumnViewerSorter.this, DESC);
						} else if( tdirection == DESC ) {
							setSorter(AbstractColumnViewerSorter.this, NONE);
						}
					} else {
						setSorter(AbstractColumnViewerSorter.this, ASC);
					}
				} else {
					setSorter(AbstractColumnViewerSorter.this, ASC);
				}
			}
		});
	}
	
	public void setSorter(AbstractColumnViewerSorter sorter, int direction) {
		if( direction == NONE ) {
			column.getColumn().getParent().setSortColumn(null);
			column.getColumn().getParent().setSortDirection(SWT.NONE);
			viewer.setComparator(null);
		} else {
			column.getColumn().getParent().setSortColumn(column.getColumn());
			sorter.direction = direction;
			
			if( direction == ASC ) {
				column.getColumn().getParent().setSortDirection(SWT.DOWN);
			} else {
				column.getColumn().getParent().setSortDirection(SWT.UP);
			}
			
			if( viewer.getComparator() == sorter ) {
				viewer.refresh();
			} else {
				viewer.setComparator(sorter);
			}
			
		}
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		return direction * doCompare(viewer, e1, e2);
	}
	
	protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
	
	// code from Eclipse bug reports
	public void sort(final Viewer viewer, final Object[] elements) {
			final boolean[] flag = new boolean[1];
			flag[0] = true;

			Thread t = new Thread() {

				public void run() {
					callSuperSort(viewer, elements);
					flag[0] = false;
				}

			};
			t.start();

			// Here's the trick stop here but hold the GUI responsive
			Shell shell = viewer.getControl().getShell();
			Display display = viewer.getControl().getDisplay();

			while (flag[0] && !shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}						
		}

		private void callSuperSort(final Viewer viewer, final Object[] elements) {
			super.sort(viewer, elements);
		}
}