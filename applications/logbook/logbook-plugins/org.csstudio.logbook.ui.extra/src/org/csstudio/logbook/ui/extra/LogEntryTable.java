/**
 *
 */
package org.csstudio.logbook.ui.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.csstudio.java.time.TimestampFormats;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.Tag;
import org.csstudio.ui.util.AbstractSelectionProviderWrapper;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

/**
 * @author shroffk
 *
 */
public class LogEntryTable extends Composite implements ISelectionProvider {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    final static private DateTimeFormatter DATE_FORMAT =  TimestampFormats.DATETIMESHORT_FORMAT;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    private AbstractSelectionProviderWrapper selectionProvider;
    private boolean expanded = false;
    // The number of lines to show in the compact mode
    private int rowSize = 1;

    private ErrorBar errorBar;
    private GridTableViewer gridTableViewer;
    private List<LogEntry> logEntries = Collections.emptyList();
    private Grid grid;

    public LogEntryTable(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                switch (event.getPropertyName()) {
                case "logEntries":
                    gridTableViewer.setSelection(null, true);
                    gridTableViewer.setInput(logEntries.toArray(new LogEntry[logEntries.size()]));
                    break;
                case "expanded":
                    // TODO shroffk fix the refresh
                    FontMetrics fm = new GC(Display.getCurrent()).getFontMetrics();
                    grid.setItemHeight(fm.getHeight() * rowSize);
                    gridTableViewer.getGrid().setAutoHeight(expanded);
                    gridTableViewer.setInput(logEntries.toArray(new LogEntry[logEntries.size()]));
                    break;
                default:
                    break;
                }
            }
        });

        errorBar = new ErrorBar(this, SWT.NONE);
        errorBar.setMarginBottom(5);

        gridTableViewer = new GridTableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.DOUBLE_BUFFERED);
        selectionProvider = new AbstractSelectionProviderWrapper(gridTableViewer, this) {

            @Override
            protected ISelection transform(IStructuredSelection selection) {
                return selection;
            }

            @Override
            protected ISelection reverseTransform(IStructuredSelection selection) {
                return selection;
            }

        };

        grid = gridTableViewer.getGrid();
        grid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        FontMetrics fm = new GC(Display.getCurrent()).getFontMetrics();
        grid.setItemHeight(fm.getHeight() * rowSize);
        grid.setAutoHeight(expanded);
        grid.setRowsResizeable(true);
        grid.setHeaderVisible(true);
        gridTableViewer.getGrid().setLinesVisible(true);
        gridTableViewer.setContentProvider(new IStructuredContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            }

            @Override
            public void dispose() {

            }

            @Override
            public Object[] getElements(Object inputElement) {
                return (Object[]) inputElement;
            }
        });

        ColumnViewerToolTipSupport.enableFor(gridTableViewer, ToolTip.NO_RECREATE);

        // First Columns displays the Date
        GridViewerColumn column = new GridViewerColumn(gridTableViewer, SWT.NONE);
        GridColumn gridColumn = column.getColumn();
        gridColumn.setMoveable(true);
        column.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                LogEntry item = ((LogEntry) cell.getElement());
                String date = item == null || item.getCreateDate() == null ? "No Data"
                        : DATE_FORMAT.format(item.getCreateDate().toInstant());
                cell.setText(date);
            }
        });
        column.getColumn().setText("Date");
        column.getColumn().setWordWrap(true);
        new TableColumnViewerLayout(gridTableViewer, column, 15, 115);
        new ColumnViewerSorter(gridTableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return Long.compare(((LogEntry) e1).getCreateDate().getTime(),
                        ((LogEntry) e2).getCreateDate().getTime());
            }
        };

        // Second column is the first line of the logEntry

        GridViewerColumn gridViewerColumnDescription = new GridViewerColumn(gridTableViewer, SWT.DOUBLE_BUFFERED);
        gridViewerColumnDescription.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                LogEntry item = ((LogEntry) element);
                return item == null ? "" : item.getText();
            }
        });
        GridColumn tblclmnDescription = gridViewerColumnDescription.getColumn();
        tblclmnDescription.setWordWrap(true);
        tblclmnDescription.setText("Description");
        new TableColumnViewerLayout(gridTableViewer, gridViewerColumnDescription, 50, 250);

        // Third column is the owner of the logEntry

        GridViewerColumn gridViewerColumnOwner = new GridViewerColumn(gridTableViewer,
                SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        gridViewerColumnOwner.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                LogEntry item = ((LogEntry) element);
                return item == null ? "" : item.getOwner();
            }
        });

        // gridViewerColumnOwner.getColumn().setSort(SWT.UP);
        GridColumn tblclmnOwner = gridViewerColumnOwner.getColumn();
        tblclmnOwner.setText("Owner");
        new TableColumnViewerLayout(gridTableViewer, gridViewerColumnOwner, 10, 75);
        new ColumnViewerSorter(gridTableViewer, gridViewerColumnOwner) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return ((LogEntry) e1).getOwner().compareTo(((LogEntry) e2).getOwner());
            }
        };

        // Forth column lists the logbooks
        GridViewerColumn gridViewerColumnLogbooks = new GridViewerColumn(gridTableViewer,
                SWT.MULTI | SWT.DOUBLE_BUFFERED);
        gridViewerColumnLogbooks.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                LogEntry item = ((LogEntry) element);
                if (item == null) {
                    return "";
                } else {
                    return item.getLogbooks().stream().map(Logbook::getName).sorted()
                            .collect(Collectors.joining(System.getProperty("line.separator")));
                }
            }
        });
        GridColumn tblclmnLogbooks = gridViewerColumnLogbooks.getColumn();
        tblclmnLogbooks.setWordWrap(true);
        tblclmnLogbooks.setText("Logbooks");
        new TableColumnViewerLayout(gridTableViewer, gridViewerColumnLogbooks, 10, 75);

        // column lists the tags
        GridViewerColumn gridViewerColumnTags = new GridViewerColumn(gridTableViewer, SWT.DOUBLE_BUFFERED);
        gridViewerColumnTags.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                LogEntry item = ((LogEntry) element);
                if (item == null) {
                    return "";
                } else {
                    return item.getTags().stream().map(Tag::getName).sorted()
                            .collect(Collectors.joining(System.getProperty("line.separator")));
                }
            }
        });
        GridColumn tblclmnTags = gridViewerColumnTags.getColumn();
        tblclmnTags.setWordWrap(true);
        tblclmnTags.setText("Tags");
        new TableColumnViewerLayout(gridTableViewer, gridViewerColumnTags, 10, 75);

        // Attachments
        GridViewerColumn gridViewerColumnAttachments = new GridViewerColumn(gridTableViewer, SWT.DOUBLE_BUFFERED);
        gridViewerColumnAttachments.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                LogEntry item = ((LogEntry) element);
                return String.valueOf(item.getAttachment().size());
            }
        });
        GridColumn tblclmnAttachment = gridViewerColumnAttachments.getColumn();
        tblclmnAttachment.setText("Attachments");
        new TableColumnViewerLayout(gridTableViewer, gridViewerColumnAttachments, 5, 30);

        new ColumnViewerSorter(gridTableViewer, gridViewerColumnAttachments) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return Integer.compare(((LogEntry) e1).getAttachment().size(), ((LogEntry) e2).getAttachment().size());
            }
        };
        gridTableViewer.refresh();
    }

    public Collection<LogEntry> getlogEntries() {
        return logEntries;
    }

    public void setLogs(List<LogEntry> logEntries) {
        Collection<LogEntry> oldValue = this.logEntries;
        this.logEntries = logEntries;
        changeSupport.firePropertyChange("logEntries", oldValue, this.logEntries);
    }

    @Override
    public void addMouseListener(MouseListener listener) {
        gridTableViewer.getGrid().addMouseListener(listener);
    };

    @Override
    public void removeMouseListener(MouseListener listener) {
        gridTableViewer.getGrid().removeMouseListener(listener);
    };

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionProvider.addSelectionChangedListener(listener);
    }

    @Override
    public ISelection getSelection() {
        return selectionProvider.getSelection();
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionProvider.removeSelectionChangedListener(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        selectionProvider.setSelection(selection);
    }

    @Override
    public void setMenu(Menu menu) {
        super.setMenu(menu);
        gridTableViewer.getGrid().setMenu(menu);
    }

    /**
     * @return the expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * @param expanded
     *            the expanded to set
     */
    public void setExpanded(boolean expanded) {
        boolean oldValue = this.expanded;
        this.expanded = expanded;
        changeSupport.firePropertyChange("expanded", oldValue, this.expanded);
    }

    /**
     *
     * @return
     */
    public int getRowSize() {
        return rowSize;
    }

    /**
     *
     * @param rowSize
     */
    public void setRowSize(int rowSize) {
        int oldValue = this.rowSize;
        this.rowSize = rowSize;
        changeSupport.firePropertyChange("expanded", oldValue, this.rowSize);
    }
}
