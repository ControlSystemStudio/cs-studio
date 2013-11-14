package org.csstudio.shift.ui;

import org.csstudio.ui.util.AbstractSelectionProviderWrapper;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.TableViewerColumnSorter;
import org.csstudio.shift.Shift;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.util.Collection;


public class ShiftTable extends Composite implements ISelectionProvider {
    final int TEXT_MARGIN = 2;

    // Model
    Collection<Shift> shifts;
    Shift selectedShift;

    // GUI
    private Table shiftTable;
    private TableViewer shiftTableViewer;

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
            this);
    private Composite composite;
    private AbstractSelectionProviderWrapper selectionProvider;

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public ShiftTable(final Composite parent, final int style) {
        super(parent, style);
        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 2;
        gridLayout.verticalSpacing = 2;
        gridLayout.marginWidth = 2;
        gridLayout.horizontalSpacing = 2;
        setLayout(gridLayout);

        composite = new Composite(this, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        final TableColumnLayout tcl_composite = new TableColumnLayout();
        composite.setLayout(tcl_composite);

        shiftTableViewer = new TableViewer(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        shiftTable = shiftTableViewer.getTable();
	/*
	 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
	 * Therefore, it is critical for performance that these methods be as
	 * efficient as possible.
	 */
        shiftTable.addListener(SWT.MeasureItem, new Listener() {
            public void handleEvent(final Event event) {
                final TableItem item = (TableItem) event.item;
                final String text = item.getText(event.index);
                final Point size = event.gc.textExtent(text);
                event.width = size.x + 2 * TEXT_MARGIN;
                event.height = Math.max(event.height, size.y + TEXT_MARGIN);
            }
        });
        shiftTable.addListener(SWT.EraseItem, new Listener() {
            public void handleEvent(final Event event) {
                event.detail &= ~SWT.FOREGROUND;
            }
        });
        shiftTable.addListener(SWT.PaintItem, new Listener() {
            public void handleEvent(Event event) {
                final TableItem item = (TableItem) event.item;
                final String text = item.getText(event.index);
            	/* center column 1 vertically */
                int yOffset = 0;
                if (event.index == 1) {
                    final Point size = event.gc.textExtent(text);
                    yOffset = Math.max(0, (event.height - size.y) / 2);
                }
                event.gc.drawText(text, event.x + TEXT_MARGIN, event.y + yOffset, true);
            }
        });

        shiftTable.setHeaderVisible(true);
        shiftTable.setLinesVisible(true);

        shiftTableViewer.setContentProvider(new IStructuredContentProvider() {

            @Override
            public void inputChanged(final Viewer viewer,final Object oldInput, final Object newInput) {
                // TODO Auto-generated method stub

            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public Object[] getElements(final Object inputElement) {
                return (Object[]) inputElement;
            }
        });
        selectionProvider = new AbstractSelectionProviderWrapper(shiftTableViewer, this) {

            @Override
            protected ISelection transform(final IStructuredSelection selection) {
                return selection;
            }

        };
        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                switch (event.getPropertyName()) {
                    case "shift":
                        updateTable();
                        shiftTableViewer.setInput(shifts.toArray());
                        break;
                    case "selectedShiftEntry":

                        break;

                    default:
                        break;
                }
            }
        });
        updateTable();
    }

    private void updateTable() {
        // Dispose existing columns
        for (TableColumn column : shiftTableViewer.getTable().getColumns()) {
            column.dispose();
        }
        final TableColumnLayout shiftTablelayout = (TableColumnLayout) composite.getLayout();

        // First column is date and the default sort column
        final TableViewerColumn tableViewerColumnDate = new TableViewerColumn(shiftTableViewer, SWT.DOUBLE_BUFFERED);
        new TableViewerColumnSorter(tableViewerColumnDate) {

            @Override
            protected Object getValue(final Object o) {
                return ((Shift) o).getStartDate().getTime();
            }
        };
        tableViewerColumnDate.setLabelProvider(new ColumnLabelProvider() {

            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null || item.getStartDate() == null ? "" : DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.SHORT).format(item.getStartDate());
            }
        });
        final TableColumn tblclmnDate = tableViewerColumnDate.getColumn();
        tblclmnDate.setWidth(100);
        tblclmnDate.setText("Date");
        shiftTablelayout.setColumnData(tblclmnDate, new ColumnWeightData(15));

        final TableViewerColumn tableViewerColumnDescription;
        tableViewerColumnDescription = new TableViewerColumn(shiftTableViewer, SWT.DOUBLE_BUFFERED);
        tableViewerColumnDescription.setLabelProvider(new ColumnLabelProvider() {

            public String getText(final Object element) {
            	final Shift item = ((Shift) element);
                return item == null ? "" : item.getType();
            }
        });
        final TableColumn tblclmnDescription;
        tblclmnDescription = tableViewerColumnDescription.getColumn();
        tblclmnDescription.setWidth(500);
        tblclmnDescription.setText("Description");
        shiftTablelayout.setColumnData(tblclmnDescription, new ColumnWeightData(60));

        // Third column is the owner of the shift

        final TableViewerColumn tableViewerColumnOwner = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerColumnOwner.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null ? "" : item.getOwner();
            }
        });
        final TableColumn tblclmnOwner = tableViewerColumnOwner.getColumn();
        shiftTablelayout.setColumnData(tblclmnOwner, new ColumnWeightData(15));
        tblclmnOwner.setWidth(100);
        tblclmnOwner.setText("Owner");
        new TableViewerColumnSorter(tableViewerColumnOwner) {

            @Override
            protected Object getValue(final Object o) {
                return ((Shift) o).getOwner();
            }
        };
        // Now additional Columns are created based on the selected
        shiftTableViewer.getTable().layout();
    }

    public Collection<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(final Collection<Shift> shifts) {
        final Collection<Shift> oldValue = this.shifts;
        this.shifts = shifts;
        changeSupport.firePropertyChange("shifts", oldValue, this.shifts);
    }

    public Shift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(final Shift selectedShift) {
        final Shift oldValue = this.selectedShift;
        this.selectedShift = selectedShift;
        changeSupport.firePropertyChange("selectedShift", oldValue, this.selectedShift);
    }

    @Override
    public void addSelectionChangedListener(final ISelectionChangedListener listener) {
        selectionProvider.addSelectionChangedListener(listener);

    }

    @Override
    public ISelection getSelection() {
        return selectionProvider.getSelection();
    }

    @Override
    public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
        selectionProvider.removeSelectionChangedListener(listener);
    }

    @Override
    public void setSelection(final ISelection selection) {
        selectionProvider.setSelection(selection);
    }

    @Override
    public void setMenu(final Menu menu) {
        super.setMenu(menu);
        shiftTable.setMenu(menu);
    }

}