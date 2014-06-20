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
import org.eclipse.swt.events.MouseListener;
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
import gov.bnl.shiftClient.Shift;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;


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

        private TableColumn tblclmnDate;

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
                    case "shifts":
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


        // First column is the status of the shift

        final TableViewerColumn tableViewerColumnStatus = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerColumnStatus.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);               
                return item == null ? "" : item.getStatus();
            }
        });
        final TableColumn tblclmnStatus = tableViewerColumnStatus.getColumn();
        shiftTablelayout.setColumnData(tblclmnStatus, new ColumnWeightData(15, 50));
        tblclmnStatus.setWidth(50);
        tblclmnStatus.setText("Status");
        new TableViewerColumnSorter(tableViewerColumnStatus) {

            @Override
            protected Object getValue(final Object o) {
                return ((Shift) o).getStatus();
            }
        };
        
        // column is date and the default sort column
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
        tblclmnDate = tableViewerColumnDate.getColumn();
        tblclmnDate.setWidth(100);
        tblclmnDate.setText("Start Date");
        shiftTablelayout.setColumnData(tblclmnDate, new ColumnWeightData(15, 50));
        
        //column
        final TableViewerColumn tableViewerColumnEndDate = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerColumnEndDate.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null || item.getEndDate() == null ? "" : DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.SHORT).format(item.getEndDate());            
            }
        });
        final TableColumn tblclmnEndDate = tableViewerColumnEndDate.getColumn();
        tblclmnEndDate.setWidth(100);
        shiftTablelayout.setColumnData(tblclmnEndDate, new ColumnWeightData(15, 50));
        tblclmnEndDate.setText("End Date");
        new TableViewerColumnSorter(tableViewerColumnEndDate) {

            @Override
            protected Object getValue(final Object o) {
            	final Shift shift = ((Shift) o);
                return shift.getEndDate() == null ? new Date().getTime() : shift.getEndDate().getTime();
            }
        };
       
        final TableViewerColumn tableViewerColumnId = new TableViewerColumn(shiftTableViewer, SWT.DOUBLE_BUFFERED);
        tableViewerColumnId.setLabelProvider(new ColumnLabelProvider() {

            public String getText(final Object element) {
                    final Shift item = ((Shift) element);
                return item == null || item.getId() == null ? "" : item.getId().toString();
            }
        });
        final TableColumn tblclmnId = tableViewerColumnId.getColumn();
        tblclmnId.setWidth(50);
        tblclmnId.setText("Id");
        shiftTablelayout.setColumnData(tblclmnId, new ColumnWeightData(15, 40));
        new TableViewerColumnSorter(tableViewerColumnId) {

            @Override
            protected Object getValue(final Object o) {
                    final Shift item = ((Shift) o);
                return item == null || item.getId() == null ? "" : item.getId();
            }
        };
        
        final TableViewerColumn tableViewerColumnDescription = new TableViewerColumn(shiftTableViewer, SWT.DOUBLE_BUFFERED);
        tableViewerColumnDescription.setLabelProvider(new ColumnLabelProvider() {

            public String getText(final Object element) {
                    final Shift item = ((Shift) element);
                return item == null || item.getDescription() == null ? "" : item.getDescription();
            }
        });
        final TableColumn tblclmnDescription = tableViewerColumnDescription.getColumn();
        tblclmnDescription.setWidth(250);
        tblclmnDescription.setText("Description");
        shiftTablelayout.setColumnData(tblclmnDescription, new ColumnWeightData(40, 200));
        new TableViewerColumnSorter(tableViewerColumnDescription) {

            @Override
            protected Object getValue(final Object o) {
                    final Shift item = ((Shift) o);
                return item == null || item.getDescription() == null ? "" : item.getDescription();
            }
        };
        
        final TableViewerColumn tableViewerColumnType = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerColumnType.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null ? "" : item.getType().getName();
            }
        });
        final TableColumn tblclmnType = tableViewerColumnType.getColumn();
        shiftTablelayout.setColumnData(tblclmnType, new ColumnWeightData(15, 50));
        tblclmnType.setWidth(100);
        tblclmnType.setText("Type");
        new TableViewerColumnSorter(tableViewerColumnType) {

            @Override
            protected Object getValue(final Object o) {
                return ((Shift) o).getType().getName();
            }
        };
        
        final TableViewerColumn tableViewerColumnOwner = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerColumnOwner.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null || item.getOwner() == null ? "" : item.getOwner();           
            }
        });
        final TableColumn tblclmnOwner = tableViewerColumnOwner.getColumn();
        shiftTablelayout.setColumnData(tblclmnOwner, new ColumnWeightData(15, 50));
        tblclmnOwner.setWidth(100);
        tblclmnOwner.setText("Owner");
        new TableViewerColumnSorter(tableViewerColumnOwner) {

            @Override
            protected Object getValue(final Object o) {
                    final Shift item = ((Shift) o);
                return item == null || item.getOwner() == null ? "" : item.getOwner();           
            }
        };
        
        final TableViewerColumn tableViewerColumnLeadOperator = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerColumnLeadOperator.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null || item.getLeadOperator() == null ? "" : item.getLeadOperator();
            }
        });
        final TableColumn tblclmnLeadOperator = tableViewerColumnLeadOperator.getColumn();
        shiftTablelayout.setColumnData(tblclmnLeadOperator, new ColumnWeightData(15, 50));
        tblclmnLeadOperator.setWidth(100);
        tblclmnLeadOperator.setText("Lead Operator");
        new TableViewerColumnSorter(tableViewerColumnLeadOperator) {

            @Override
            protected Object getValue(final Object o) {
                final Shift item = ((Shift) o);
                return item == null || item.getLeadOperator() == null ? "" : item.getLeadOperator();
            }
        };
        
        
        final TableViewerColumn tableViewerColumnOnShiftPersonal = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerColumnOnShiftPersonal.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null || item.getOnShiftPersonal() == null ? "" : item.getOnShiftPersonal();
            }
        });
        final TableColumn tblclmnOnShiftPersonal = tableViewerColumnOnShiftPersonal.getColumn();
        shiftTablelayout.setColumnData(tblclmnOnShiftPersonal, new ColumnWeightData(15, 50));
        tblclmnOnShiftPersonal.setWidth(100);
        tblclmnOnShiftPersonal.setText("On Shift Personal");
        new TableViewerColumnSorter(tableViewerColumnOnShiftPersonal) {

            @Override
            protected Object getValue(final Object o) {
                final Shift item = ((Shift) o);
                return item == null || item.getOnShiftPersonal() == null ? "" : item.getOnShiftPersonal();
            }
        };
        
        final TableViewerColumn tableViewerColumnReport = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerColumnReport.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null || item.getReport() == null ? "" : item.getReport();
            }
        });
        final TableColumn tblclmnOnShiftReport = tableViewerColumnReport.getColumn();
        shiftTablelayout.setColumnData(tblclmnOnShiftReport, new ColumnWeightData(15, 50));
        tblclmnOnShiftReport.setWidth(100);
        tblclmnOnShiftReport.setText("Report");
        new TableViewerColumnSorter(tableViewerColumnReport) {

            @Override
            protected Object getValue(final Object o) {
                    final Shift item = ((Shift) o);
                return item == null || item.getReport() == null ? "" : item.getReport();
            }
        };
        
        final TableViewerColumn tableViewerCloseUser = new TableViewerColumn(shiftTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
        tableViewerCloseUser.setLabelProvider(new ColumnLabelProvider() {
            public String getText(final Object element) {
                final Shift item = ((Shift) element);
                return item == null || item.getCloseShiftUser() == null ? "" : item.getCloseShiftUser();
            }
        });
        final TableColumn tblclmnCloseUser = tableViewerCloseUser.getColumn();
        shiftTablelayout.setColumnData(tblclmnCloseUser, new ColumnWeightData(10, 45));
        tblclmnCloseUser.setWidth(100);
        tblclmnCloseUser.setText("Close User");
        new TableViewerColumnSorter(tableViewerCloseUser) {

            @Override
            protected Object getValue(final Object o) {
                final Shift item = ((Shift) o);
                return item == null || item.getCloseShiftUser() == null ? "" : item.getCloseShiftUser();
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
    public void addMouseListener(MouseListener listener) {
            shiftTable.addMouseListener(listener);
    };

    @Override
    public void removeMouseListener(MouseListener listener) {
            shiftTable.removeMouseListener(listener);
    };

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