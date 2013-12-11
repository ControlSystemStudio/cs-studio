/**
 * 
 */
package org.csstudio.logbook.ui.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author shroffk
 * 
 */
public class LogEntryTree extends Composite implements ISelectionProvider {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
	    this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.removePropertyChangeListener(listener);
    }

    private final String eol = System.getProperty("line.separator");
    private AbstractSelectionProviderWrapper selectionProvider;
    private int logEntryOrder = SWT.DOWN;
    private boolean expanded = true;   

    private ErrorBar errorBar;
    private GridTreeViewer gridTreeViewer;
    private List<LogEntry> logEntries = Collections.emptyList();
    private Grid grid;

    public LogEntryTree(Composite parent, int style) {
	super(parent, style);
	setLayout(new GridLayout(1, false));

	addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
		switch (event.getPropertyName()) {
		case "logEntries":
		    gridTreeViewer.setSelection(null, true);
		    gridTreeViewer.setInput(createModel(logEntries));
		    break;
		case "logEntryOrder":
		    gridTreeViewer.setInput(createModel(logEntries));
		    break;
		case "expanded":
		    //TODO shroffk fix the refresh
		    gridTreeViewer.getGrid().setAutoHeight(expanded);
		    gridTreeViewer.setInput(createModel(logEntries));
		    break;
		default:
		    break;
		}
	    }
	});

	errorBar = new ErrorBar(this, SWT.NONE);
	errorBar.setMarginBottom(5);

	gridTreeViewer = new GridTreeViewer(this, SWT.BORDER | SWT.V_SCROLL
		| SWT.H_SCROLL | SWT.DOUBLE_BUFFERED | SWT.MULTI | SWT.VIRTUAL);
	selectionProvider = new AbstractSelectionProviderWrapper(gridTreeViewer, this) {

	    @Override
	    protected ISelection transform(IStructuredSelection selection) {
		if (selection.size() == 1) {
		    LogEntryTreeModel element = (LogEntryTreeModel) selection
			    .getFirstElement();
		    return new StructuredSelection(element.logEntry);
		} else if (!selection.isEmpty()) {
		    List<LogEntry> selectedEntries = new ArrayList<LogEntry>();
		    for (Iterator<LogEntryTreeModel> iterator = selection.iterator(); iterator.hasNext();) {
			LogEntryTreeModel domain = (LogEntryTreeModel) iterator.next();
			selectedEntries.add(domain.logEntry);
		    }
		    return new StructuredSelection(selectedEntries);
		} else {
		    return selection;
		}
	    }

	};

	grid = gridTreeViewer.getGrid();
	grid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	grid.setItemHeight(25);
	grid.setAutoHeight(expanded);
	grid.setRowsResizeable(true);
	grid.setHeaderVisible(true);
	grid.setLinesVisible(true);
	gridTreeViewer.setContentProvider(new LogEntryTreeContentProvider());
	ColumnViewerToolTipSupport.enableFor(gridTreeViewer, ToolTip.NO_RECREATE);

	// First Columns displays the Date
	GridViewerColumn column = new GridViewerColumn(gridTreeViewer, SWT.NONE);
	GridColumn gridColumn = column.getColumn();
	gridColumn.setMoveable(true);
	column.setLabelProvider(new CellLabelProvider() {

	    @Override
	    public void update(ViewerCell cell) {
		LogEntryTreeModel item = ((LogEntryTreeModel) cell.getElement());
		StringBuffer date = new StringBuffer();
		if (item != null) {
		    date.append(item.logEntry.getCreateDate() == null ? "No Data"
			    : DateFormat.getDateTimeInstance(DateFormat.SHORT,
				    DateFormat.SHORT).format(
				    item.logEntry.getCreateDate()));
		    if (item.logEntry.getModifiedDate() != null &&
			    item.logEntry.getCreateDate().getTime()/1000 != item.logEntry.getModifiedDate().getTime() /1000) {
			date.append(System.getProperty("line.separator"));
			date.append("modified at:");
			date.append(System.getProperty("line.separator"));
			date.append(DateFormat
					.getDateTimeInstance(DateFormat.SHORT,
						DateFormat.SHORT)
					.format(item.logEntry.getModifiedDate()));
		    }
		    cell.setText(date.toString());
		}
	    }
	});
	column.getColumn().setTree(true);
	column.getColumn().setText("Date");
	column.getColumn().setWordWrap(true);
	new TreeColumnViewerLayout(gridTreeViewer, column, 15, 100);
	new ColumnViewerSorter(gridTreeViewer, column) {
	    @Override
	    protected int doCompare(Viewer viewer, Object e1, Object e2) {
		return ((LogEntryTreeModel) e1).logEntry.getCreateDate()
			.compareTo(
				((LogEntryTreeModel) e2).logEntry
					.getCreateDate());
	    }
	};
	// Second column is the first line of the logEntry

	GridViewerColumn gridViewerColumnDescription = new GridViewerColumn(
		gridTreeViewer, SWT.DOUBLE_BUFFERED);
	gridViewerColumnDescription.setLabelProvider(new ColumnLabelProvider() {

	    public String getText(Object element) {
		LogEntry item = ((LogEntryTreeModel) element).logEntry;
		return item == null ? "" : item.getText();
	    }
	});
	GridColumn tblclmnDescription = gridViewerColumnDescription.getColumn();
	tblclmnDescription.setWordWrap(true);
	tblclmnDescription.setText("Description");
	new TreeColumnViewerLayout(gridTreeViewer, gridViewerColumnDescription,
		50, 250);

	// Third column is the owner of the logEntry

	GridViewerColumn gridViewerColumnOwner = new GridViewerColumn(
		gridTreeViewer, SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED);
	gridViewerColumnOwner.setLabelProvider(new ColumnLabelProvider() {
	    public String getText(Object element) {
		LogEntryTreeModel item = ((LogEntryTreeModel) element);
		StringBuffer owner = new StringBuffer();
		if (item != null && item.logEntry != null){		    
		    owner.append(item.creater);
		    if (item.logEntry.getModifiedDate() != null &&
			    item.logEntry.getCreateDate().getTime()/1000 != item.logEntry.getModifiedDate().getTime()/1000){
			owner.append(System.getProperty("line.separator"));
			owner.append("modified by:");
			owner.append(System.getProperty("line.separator"));
			owner.append(item.logEntry.getOwner());
		    }
		}
		return owner.toString();
	    }
	});

	// gridViewerColumnOwner.getColumn().setSort(SWT.UP);
	GridColumn tblclmnOwner = gridViewerColumnOwner.getColumn();
	tblclmnOwner.setText("Owner");
	tblclmnOwner.setWordWrap(true);
	new TreeColumnViewerLayout(gridTreeViewer, gridViewerColumnOwner, 10, 75);
	new ColumnViewerSorter(gridTreeViewer, gridViewerColumnOwner) {
	    @Override
	    protected int doCompare(Viewer viewer, Object e1, Object e2) {
		return ((LogEntryTreeModel) e1).logEntry.getOwner().compareTo(
			((LogEntryTreeModel) e2).logEntry.getOwner());
	    }
	};

	// Forth column lists the logbooks
	GridViewerColumn gridViewerColumnLogbooks = new GridViewerColumn(
		gridTreeViewer, SWT.MULTI | SWT.DOUBLE_BUFFERED);
	gridViewerColumnLogbooks.setLabelProvider(new ColumnLabelProvider() {

	    @Override
	    public String getText(Object element) {
		LogEntry item = ((LogEntryTreeModel) element).logEntry;
		StringBuilder logbooks = new StringBuilder();
		for (Logbook logbook : item.getLogbooks()) {
		    logbooks.append(logbook.getName() + eol);
		}
		return item == null ? "" : logbooks.toString();
	    }
	});
	GridColumn tblclmnLogbooks = gridViewerColumnLogbooks.getColumn();
	tblclmnLogbooks.setWordWrap(false);
	tblclmnLogbooks.setText("Logbooks");
	new TreeColumnViewerLayout(gridTreeViewer, gridViewerColumnLogbooks,
		10, 75);

	// column lists the tags
	GridViewerColumn gridViewerColumnTags = new GridViewerColumn(
		gridTreeViewer, SWT.DOUBLE_BUFFERED);
	gridViewerColumnTags.setLabelProvider(new ColumnLabelProvider() {

	    public String getText(Object element) {
		LogEntry item = ((LogEntryTreeModel) element).logEntry;
		StringBuilder tags = new StringBuilder();
		for (Tag tag : item.getTags()) {
		    tags.append(tag.getName() + eol);
		}
		return item == null ? "" : tags.toString();
	    }
	});
	GridColumn tblclmnTags = gridViewerColumnTags.getColumn();
	tblclmnTags.setWordWrap(false);
	tblclmnTags.setText("Tags");
	new TreeColumnViewerLayout(gridTreeViewer, gridViewerColumnTags, 10, 75);

	// Attachments
	GridViewerColumn gridViewerColumnAttachments = new GridViewerColumn(
		gridTreeViewer, SWT.DOUBLE_BUFFERED);
	gridViewerColumnAttachments.setLabelProvider(new ColumnLabelProvider() {

	    @Override
	    public String getText(Object element) {
		LogEntry item = ((LogEntryTreeModel) element).logEntry;
		return String.valueOf(item.getAttachment().size());
	    }
	});
	GridColumn tblclmnAttachment = gridViewerColumnAttachments.getColumn();
	tblclmnAttachment.setText("Attachments");
	new TreeColumnViewerLayout(gridTreeViewer, gridViewerColumnAttachments,
		5, 30);

	new ColumnViewerSorter(gridTreeViewer, gridViewerColumnAttachments) {
	    @Override
	    protected int doCompare(Viewer viewer, Object e1, Object e2) {
		return Integer.compare(((LogEntry) e1).getAttachment().size(),
			((LogEntry) e2).getAttachment().size());
	    }
	};
	gridTreeViewer.refresh();
    }

    public Collection<LogEntry> getlogEntries() {
	return logEntries;
    }

    public void setLogs(List<LogEntry> logEntries) {
	Collection<LogEntry> oldValue = this.logEntries;
	this.logEntries = logEntries;
	changeSupport.firePropertyChange("logEntries", oldValue, this.logEntries);
    }

    /**
     * @return the logEntryOrder
     */
    public int getLogEntryOrder() {
	return logEntryOrder;
    }

    /**
     * @param logEntryOrder
     *            the logEntryOrder to set
     */
    public void setLogEntryOrder(int logEntryOrder) {
	int oldValue = this.logEntryOrder;
	this.logEntryOrder = logEntryOrder;
	changeSupport.firePropertyChange("logEntryOrder", oldValue, this.logEntryOrder);
    }
    
    /**
     * @return the expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * @param expanded the expanded to set
     */
    public void setExpanded(boolean expanded) {
	boolean oldValue = this.expanded;	
        this.expanded = expanded;
        changeSupport.firePropertyChange("expanded", oldValue, this.expanded);
    }

    @Override
    public void addMouseListener(MouseListener listener) {
	gridTreeViewer.getGrid().addMouseListener(listener);
    };

    @Override
    public void removeMouseListener(MouseListener listener) {
	gridTreeViewer.getGrid().removeMouseListener(listener);
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
    public void removeSelectionChangedListener(
	    ISelectionChangedListener listener) {
	selectionProvider.removeSelectionChangedListener(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
	selectionProvider.setSelection(selection);
    }

    @Override
    public void setMenu(Menu menu) {
	super.setMenu(menu);
	gridTreeViewer.getGrid().setMenu(menu);
    }

    private class LogEntryTreeContentProvider implements ITreeContentProvider {

	public Object[] getElements(Object inputElement) {
	    return ((LogEntryTreeModel) inputElement).child.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
	    return getElements(parentElement);
	}

	public Object getParent(Object element) {
	    if (element == null) {
		return null;
	    }
	    return ((LogEntryTreeModel) element).parent;
	}

	public boolean hasChildren(Object element) {
	    return ((LogEntryTreeModel) element).child.size() > 0;
	}

    }

    public class LogEntryTreeModel {
	public LogEntryTreeModel parent;

	public List<LogEntryTreeModel> child = new ArrayList<LogEntryTreeModel>();
	public final String creater;
	public final LogEntry logEntry;

	public LogEntryTreeModel(LogEntry logEntry, String creater, LogEntryTreeModel root) {
	    this.parent = root;
	    this.logEntry = logEntry;
	    this.creater = creater;
	}

	public String toString() {
	    String rv = "Item ";
	    if (parent != null) {
		rv = parent.toString() + ".";
	    }

	    rv += logEntry.toString();

	    return rv;
	}
    }

    private LogEntryTreeModel createModel(List<LogEntry> logEntries) {

	LogEntryTreeModel root = new LogEntryTreeModel(null, null, null);

	LogEntryTreeModel subItem;

	// organize the log entries and the versions

	Map<Long, List<LogEntry>> model = new LinkedHashMap<Long, List<LogEntry>>();

	for (LogEntry logEntry : logEntries) {
	    if (!model.containsKey(logEntry.getId())){
		List<LogEntry> versions = new ArrayList<LogEntry>();
		model.put((Long) logEntry.getId(), versions);
	    }
	    model.get(logEntry.getId()).add(logEntry);	  
	}

	for (Entry<Long, List<LogEntry>> entry : model.entrySet()) {
	    List<LogEntry> entries = entry.getValue();	    
	    String creater = "";
	    if(logEntryOrder == SWT.UP){
		Collections.sort(entries, new Comparator<LogEntry>(){
		    @Override
		    public int compare(LogEntry o1, LogEntry o2) {
			return o1.getModifiedDate().compareTo(o2.getModifiedDate());
		    }		    
		});
		creater = entries.get(0).getOwner();
	    }else{
		Collections.sort(entries, new Comparator<LogEntry>(){
		    @Override
		    public int compare(LogEntry o1, LogEntry o2) {
			return o2.getModifiedDate().compareTo(o1.getModifiedDate());
		    }		    
		});
		creater = entries.get(entries.size()-1).getOwner();
	    }
	    if (entries.size() > 0) {
		subItem = new LogEntryTreeModel(entries.get(0), creater, root);
		for (LogEntry logEntry : entries.subList(1, entries.size())) {
		    subItem.child.add(new LogEntryTreeModel(logEntry, creater, subItem));
		}
		root.child.add(subItem);
	    }
	}

	Collections.sort(root.child, new Comparator<LogEntryTreeModel>(){

	    @Override
	    public int compare(LogEntryTreeModel o1, LogEntryTreeModel o2) {
		Date d1 =  o1.logEntry.getCreateDate();
		Date d2 =  o2.logEntry.getCreateDate();
		return d2.compareTo(d1);
	    }
	    
	});
	return root;
    }
}
