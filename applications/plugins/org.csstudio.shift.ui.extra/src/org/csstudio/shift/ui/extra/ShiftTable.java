/**
 * 
 */
package org.csstudio.shift.ui.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.shift.Shift;
import org.csstudio.ui.util.AbstractSelectionProviderWrapper;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;


public class ShiftTable extends Composite implements ISelectionProvider {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	changeSupport.removePropertyChangeListener(listener);
    }

    private final String eol = System.getProperty("line.separator");
    private AbstractSelectionProviderWrapper selectionProvider;

    private GridTableViewer gridTableViewer;
    private List<Shift> shifts = Collections.emptyList();
    private Grid grid;

    public ShiftTable(Composite parent, int style) {
    	super(parent, style);
    	setLayout(new GridLayout(1, false));

    	addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
			switch (event.getPropertyName()) {
			case "shifts":
			    gridTableViewer.setSelection(null, true);
			    gridTableViewer.setInput(shifts
				    .toArray(new Shift[shifts.size()]));
			    break;
			default:
			    break;
			}
	    }
    	});

		gridTableViewer = new GridTableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
		selectionProvider = new AbstractSelectionProviderWrapper(gridTableViewer, this) {

		    @Override
		    protected ISelection transform(IStructuredSelection selection) {
		    	return selection;
		    }
		};

		grid = gridTableViewer.getGrid();
		grid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grid.setAutoHeight(true);
		grid.setRowsResizeable(true);
		grid.setHeaderVisible(true);
		gridTableViewer.getGrid().setLinesVisible(true);
		gridTableViewer.setContentProvider(new IStructuredContentProvider() {

		    @Override
		    public void inputChanged(Viewer viewer, Object oldInput,
			    Object newInput) {
	
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
		final GridViewerColumn column = new GridViewerColumn(gridTableViewer, SWT.NONE);
		final GridColumn gridColumn = column.getColumn();
		gridColumn.setMoveable(true);
		column.setLabelProvider(new CellLabelProvider() {
	
		    @Override
		    public void update(ViewerCell cell) {
		    	final Shift item = ((Shift) cell.getElement());
		    	final String date = item == null || item.getStartDate() == null ? "No Data"
					: DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(item.getStartDate());
				cell.setText(date);
		    }
		});
		column.getColumn().setText("Start Date");
		column.getColumn().setWordWrap(true);
		// new ColumnViewerSimpleLayout(gridTableViewer, column, 15, 100);
		new ColumnViewerWeightedLayout(gridTableViewer, column, 15, 100);
		new ColumnViewerSorter(gridTableViewer, column) {
		    @Override
		    protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
		    	return Long.compare(((Shift) e1).getStartDate().getTime(),((Shift) e2).getStartDate().getTime());
		    }
		};
		// Second column is the first line of the shift
		
			final GridViewerColumn gridViewerColumnId = new GridViewerColumn(gridTableViewer, SWT.DOUBLE_BUFFERED);
			gridViewerColumnId.setLabelProvider(new ColumnLabelProvider() {
		
			    public String getText(final Object element) {
			    	final Shift item = ((Shift) element);
			    	return item == null ? "" : item.getId().toString();
			    }
			});
			final GridColumn tblclmnId = gridViewerColumnId.getColumn();
			tblclmnId.setWordWrap(true);
			tblclmnId.setText("Id");
			new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnId, 15, 100);
			
		// Second column is the first line of the shift
	
		final GridViewerColumn gridViewerColumnDescription = new GridViewerColumn(gridTableViewer, SWT.DOUBLE_BUFFERED);
		gridViewerColumnDescription.setLabelProvider(new ColumnLabelProvider() {
	
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null ? "" : item.getDescription();
		    }
		});
		final GridColumn tblclmnDescription = gridViewerColumnDescription.getColumn();
		tblclmnDescription.setWordWrap(true);
		tblclmnDescription.setText("Description");
		new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnDescription, 15, 100);
	
		// Third column is the owner of the shift
	
		final GridViewerColumn gridViewerColumnOwner = new GridViewerColumn(gridTableViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
		gridViewerColumnOwner.setLabelProvider(new ColumnLabelProvider() {
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null ? "" : item.getOwner();
		    }
		});
	
		gridViewerColumnOwner.getColumn().setSort(SWT.UP);
		final GridColumn tblclmnOwner = gridViewerColumnOwner.getColumn();
		tblclmnOwner.setText("Owner");
		new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnOwner, 15, 100);
		new ColumnViewerSorter(gridTableViewer, gridViewerColumnOwner) {
		    @Override
		    protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
		    	return ((Shift) e1).getOwner().compareTo(((Shift) e2).getOwner());
		    }
		};
	
		// Forth column lists the shifts
		final GridViewerColumn gridViewerColumnShifts = new GridViewerColumn(gridTableViewer, SWT.MULTI | SWT.DOUBLE_BUFFERED);
		gridViewerColumnShifts.setLabelProvider(new ColumnLabelProvider() {
	
		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	final StringBuilder shifts = new StringBuilder();
		    	shifts.append(item.getType() + eol);
		    	return item == null ? "" : shifts.toString();
		    }
		});
		final GridColumn tblclmnShifts = gridViewerColumnShifts.getColumn();
		tblclmnShifts.setWordWrap(true);
		tblclmnShifts.setText("type");
		new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnShifts, 15, 100);
		new ColumnViewerSorter(gridTableViewer, gridViewerColumnShifts) {
		    @Override
		    protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
			return ((Shift) e1).getType().compareTo(((Shift) e2).getType());
		    }
		};
		
		final GridViewerColumn gridViewerColumnEndDate = new GridViewerColumn(gridTableViewer, SWT.MULTI | SWT.DOUBLE_BUFFERED);
		gridViewerColumnEndDate.setLabelProvider(new ColumnLabelProvider() {
	
		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null ? "" : DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(item.getEndDate());
		    }
		});
		final GridColumn tblclmnEndDate = gridViewerColumnEndDate.getColumn();
		tblclmnEndDate.setWordWrap(true);
		tblclmnEndDate.setText("End Date");
		new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnEndDate, 15, 100);
		new ColumnViewerSorter(gridTableViewer, gridViewerColumnEndDate) {
		    @Override
		    protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
		    	return ((Shift) e1).getEndDate().compareTo(((Shift) e2).getEndDate());
		    }
		};
	
		final GridViewerColumn gridViewerColumnOnShiftPersonal = new GridViewerColumn(gridTableViewer, SWT.MULTI | SWT.DOUBLE_BUFFERED);
		gridViewerColumnOnShiftPersonal.setLabelProvider(new ColumnLabelProvider() {
	
		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null || item.getOnShiftPersonal() == null ? "" : item.getOnShiftPersonal();
		    }
		});
		final GridColumn tblclmnOnShiftPersonal = gridViewerColumnOnShiftPersonal.getColumn();
		tblclmnOnShiftPersonal.setWordWrap(true);
		tblclmnOnShiftPersonal.setText("On Shift Personal");
		new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnOnShiftPersonal, 15, 100);
		new ColumnViewerSorter(gridTableViewer, gridViewerColumnOnShiftPersonal) {
		    @Override
		    protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
		    	return ((Shift) e1).getOnShiftPersonal().compareTo(((Shift) e2).getOnShiftPersonal());
		    }
		};
		final GridViewerColumn gridViewerColumnCloseUser = new GridViewerColumn(gridTableViewer, SWT.MULTI | SWT.DOUBLE_BUFFERED);
		gridViewerColumnCloseUser.setLabelProvider(new ColumnLabelProvider() {

		    @Override
		    public String getText(Object element) {
		    	Shift item = ((Shift) element);
		    	return item == null || item.getCloseShiftUser() == null ? "" : item.getCloseShiftUser();
		    }
		});
		final GridColumn tblclmnCloseUser = gridViewerColumnCloseUser.getColumn();
		tblclmnCloseUser.setWordWrap(true);
		tblclmnCloseUser.setText("Close User");
		new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnCloseUser, 15, 100);
		new ColumnViewerSorter(gridTableViewer, gridViewerColumnCloseUser) {
		    @Override
		    protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
		    	return ((Shift) e1).getCloseShiftUser().compareTo(((Shift) e2).getCloseShiftUser());
		    }
		};
		final GridViewerColumn gridViewerColumnLeadOperator = new GridViewerColumn(gridTableViewer, SWT.MULTI | SWT.DOUBLE_BUFFERED);
		gridViewerColumnLeadOperator.setLabelProvider(new ColumnLabelProvider() {

		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null || item.getLeadOperator() == null ? "" : item.getLeadOperator();
		    }
		});
		final GridColumn tblclmnLeadOperator = gridViewerColumnLeadOperator.getColumn();
		tblclmnLeadOperator.setWordWrap(true);
		tblclmnLeadOperator.setText("Lead Operator");
		new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnLeadOperator, 15, 100);
		new ColumnViewerSorter(gridTableViewer, gridViewerColumnLeadOperator) {
		    @Override
		    protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
		    	return ((Shift) e1).getLeadOperator().compareTo(((Shift) e2).getLeadOperator());
		    }
		};
		
		final GridViewerColumn gridViewerColumnReport = new GridViewerColumn(gridTableViewer, SWT.MULTI | SWT.DOUBLE_BUFFERED);
		gridViewerColumnReport.setLabelProvider(new ColumnLabelProvider() {

		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null || item.getReport() == null ? "" : item.getReport();
		    }
		});
		final GridColumn tblclmnReport = gridViewerColumnReport.getColumn();
		tblclmnReport.setWordWrap(true);
		tblclmnReport.setText("Report");
		new ColumnViewerWeightedLayout(gridTableViewer, gridViewerColumnReport, 15, 100);
		new ColumnViewerSorter(gridTableViewer, gridViewerColumnReport) {
		    @Override
		    protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
		    	return ((Shift) e1).getReport().compareTo(((Shift) e2).getReport());
		    }
		};
		gridTableViewer.refresh();
    }

    public Collection<Shift> getShifts() {
    	return shifts;
    }

    public void setShifts(List<Shift> shifts) {
    	final Collection<Shift> oldValue = this.shifts;
		this.shifts = shifts;
		changeSupport.firePropertyChange("shifts", oldValue, this.shifts);
    }

    @Override
    public void addMouseListener(final MouseListener listener) {
    	gridTableViewer.getGrid().addMouseListener(listener);
    };

    @Override
    public void removeMouseListener(final MouseListener listener) {
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
		gridTableViewer.getGrid().setMenu(menu);
    }
}
