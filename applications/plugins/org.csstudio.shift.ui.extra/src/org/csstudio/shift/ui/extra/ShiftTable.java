/**
 * 
 */
package org.csstudio.shift.ui.extra;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.shift.Shift;
import org.csstudio.ui.util.AbstractSelectionProviderWrapper;
import org.eclipse.core.resources.mapping.ModelProvider;
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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;


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

    private TableViewer tableViewer;
    private List<Shift> shifts = Collections.emptyList();

    public ShiftTable(final Composite parent, final int style) {
    	super(parent, style);
    	setLayout(new GridLayout(1, false));
    	   	
    	addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
			switch (event.getPropertyName()) {
			case "shifts":
			    tableViewer.setSelection(null, true);
			    tableViewer.setInput(shifts
				    .toArray(new Shift[shifts.size()]));
			    break;
			default:
			    break;
			}
	    }
    	});

		tableViewer = new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
		selectionProvider = new AbstractSelectionProviderWrapper(tableViewer, this) {

		    @Override
		    protected ISelection transform(IStructuredSelection selection) {
		    	return selection;
		    }
		};
		tableViewer.setContentProvider(new IStructuredContentProvider() {

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

		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
	
		// First Columns displays the Date
		final TableViewerColumn column = createTableViewerColumn("Start Date", 9, 0);
		column.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null || item.getStartDate() == null ? "No Data"
					: DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(item.getStartDate());
		    }
		});
		// Second column is the first line of the shift
		
			final TableViewerColumn columnId = createTableViewerColumn("Id", 9, 1);;
			columnId.setLabelProvider(new ColumnLabelProvider() {
		
			    public String getText(final Object element) {
			    	final Shift item = ((Shift) element);
			    	return item == null ? "" : item.getId().toString();
			    }
			});
			
		// third column is the first line of the shift
	
		final TableViewerColumn columnDescription = createTableViewerColumn("Description", 19, 2);;
		columnDescription.setLabelProvider(new ColumnLabelProvider() {
	
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null ? "" : item.getDescription();
		    }
		});
	
		// Forth column is the owner of the shift
	
		final TableViewerColumn columnOwner = createTableViewerColumn("Owner", 9, 3);;
		columnOwner.setLabelProvider(new ColumnLabelProvider() {
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null ? "" : item.getOwner();
		    }
		});
		
		// Fifth column lists the shifts
		final TableViewerColumn columnType = createTableViewerColumn("Type", 9, 4);
		columnType.setLabelProvider(new ColumnLabelProvider() {
	
		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	final StringBuilder shifts = new StringBuilder();
		    	shifts.append(item.getType() + eol);
		    	return item == null ? "" : shifts.toString();
		    }
		});
			
		final TableViewerColumn columnEndDate = createTableViewerColumn("End Date", 9, 5);
		columnEndDate.setLabelProvider(new ColumnLabelProvider() {
	
		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null ||  item.getEndDate() == null ? "" : DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(item.getEndDate());
		    }
		});
	
		final TableViewerColumn columnOnShiftPersonal = createTableViewerColumn("On Shift Personal", 9, 6);
		columnOnShiftPersonal.setLabelProvider(new ColumnLabelProvider() {
	
		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null || item.getOnShiftPersonal() == null ? "" : item.getOnShiftPersonal();
		    }
		});
		
		final TableViewerColumn columnCloseUser = createTableViewerColumn("Close User", 9, 7);
		columnCloseUser.setLabelProvider(new ColumnLabelProvider() {

		    @Override
		    public String getText(Object element) {
		    	Shift item = ((Shift) element);
		    	return item == null || item.getCloseShiftUser() == null ? "" : item.getCloseShiftUser();
		    }
		});
		final TableViewerColumn columnLeadOperator = createTableViewerColumn("Lead Operator", 9, 8);
		columnLeadOperator.setLabelProvider(new ColumnLabelProvider() {

		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null || item.getLeadOperator() == null ? "" : item.getLeadOperator();
		    }
		});
		
		final TableViewerColumn columnReport = createTableViewerColumn("Report", 9, 9);;
		columnReport.setLabelProvider(new ColumnLabelProvider() {

		    @Override
		    public String getText(final Object element) {
		    	final Shift item = ((Shift) element);
		    	return item == null || item.getReport() == null ? "" : item.getReport();
		    }
		});
		final Table table = tableViewer.getTable();
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    // define layout for the viewer
	    GridData gridData = new GridData();
	    gridData.verticalAlignment = GridData.FILL;
	    gridData.horizontalSpan = 2;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    tableViewer.getControl().setLayoutData(gridData);	
	    tableViewer.refresh();
	 	addControlListener(new ControlAdapter() {
    	    public void controlResized(ControlEvent e) {
    	      Rectangle area = parent.getClientArea();
    	      Point preferredSize = tableViewer.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
    	      int width = area.width - 2 * tableViewer.getTable().getBorderWidth();
    	      if (preferredSize.y > area.height + tableViewer.getTable().getHeaderHeight()) {
    	        // Subtract the scrollbar width from the total column width
    	        // if a vertical scrollbar will be required
    	        Point vBarSize = tableViewer.getTable().getVerticalBar().getSize();
    	        width -= vBarSize.x;
    	      }
    	      Point oldSize = tableViewer.getTable().getSize();
    	      if (oldSize.x > area.width) {
    	        // table is getting smaller so make the columns 
    	        // smaller first and then resize the table to
    	        // match the client area width
    	        column.getColumn().setWidth(width/11);
    	        columnId.getColumn().setWidth(width/11);
    	        columnDescription.getColumn().setWidth(2* width/11);
    	        columnOwner.getColumn().setWidth(width/11);
    	        columnType.getColumn().setWidth(width/11);
    	        columnEndDate.getColumn().setWidth(width/11);
    	        columnOnShiftPersonal.getColumn().setWidth(width/11);
    	        columnCloseUser.getColumn().setWidth(width/11);
    	        columnLeadOperator.getColumn().setWidth(width/11);
    	        columnReport.getColumn().setWidth(width/11);

    	        table.setSize(area.width, area.height);
    	      } else {
    	        // table is getting bigger so make the table 
    	        // bigger first and then make the columns wider
    	        // to match the client area width
    	        table.setSize(area.width, area.height);
    	        column.getColumn().setWidth(width/11);
    	        columnId.getColumn().setWidth(width/11);
    	        columnDescription.getColumn().setWidth(2* width/11);
    	        columnOwner.getColumn().setWidth(width/11);
    	        columnType.getColumn().setWidth(width/11);
    	        columnEndDate.getColumn().setWidth(width/11);
    	        columnOnShiftPersonal.getColumn().setWidth(width/11);
    	        columnCloseUser.getColumn().setWidth(width/11);
    	        columnLeadOperator.getColumn().setWidth(width/11);
    	        columnReport.getColumn().setWidth(width/11);
    	      }
    	    }
    	  });
    }

    public Collection<Shift> getShifts() {
    	return shifts;
    }

    public void setShifts(List<Shift> shifts) {
    	final Collection<Shift> oldValue = this.shifts;
		this.shifts = shifts;
		changeSupport.firePropertyChange("shifts", oldValue, this.shifts);
    }
    
    private TableViewerColumn createTableViewerColumn(final String title, final int size, final int colNumber) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(10);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
      }


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
    }
}
