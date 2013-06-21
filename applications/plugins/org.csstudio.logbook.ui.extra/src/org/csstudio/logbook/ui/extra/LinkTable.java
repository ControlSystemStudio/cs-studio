package org.csstudio.logbook.ui.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.logbook.Attachment;
import org.csstudio.ui.util.composites.BeanComposite;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

public class LinkTable extends BeanComposite {

    private List<Attachment> files = Collections.emptyList();
    private List<Integer> selectionIndex = new ArrayList<Integer>();

    private GridTableViewer gridTableViewer;
    private Grid grid;

    public LinkTable(Composite parent, int style) {
	super(parent, style);
	setLayout(new GridLayout(1, false));

	addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
		switch (event.getPropertyName()) {
		case "files":
		    gridTableViewer.setSelection(null, true);
		    createTable();
		    break;
		default:
		    break;
		}
	    }
	});

	gridTableViewer = new GridTableViewer(this, SWT.BORDER | SWT.V_SCROLL
		| SWT.DOUBLE_BUFFERED);
	grid = gridTableViewer.getGrid();
	grid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	grid.setLinesVisible(false);
	grid.setHeaderVisible(true);
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

	GridViewerColumn columnRemove = new GridViewerColumn(gridTableViewer,
		SWT.NONE);
	new ColumnViewerSimpleLayout(gridTableViewer, columnRemove, 0, 20);
	columnRemove.getColumn().setWordWrap(true);

	GridViewerColumn columnFile = new GridViewerColumn(gridTableViewer,
		SWT.NONE);
	new ColumnViewerSimpleLayout(gridTableViewer, columnFile, 95, 50);
	columnFile.getColumn().setText("Attached Files:");
	columnFile.getColumn().setWordWrap(true);

	createTable();

    }

    private void createTable() {
	grid.removeAll();
	for (final Attachment file : files) {
	    GridEditor editor = new GridEditor(grid);

	    GridItem item = new GridItem(grid, SWT.NONE);
	    final Button check = new Button(grid, SWT.CHECK);
	    check.addSelectionListener(new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent e) {
		    List<Integer> newSelectionIndex;
		    if (check.getSelection()) {
			newSelectionIndex = getSelectionIndex();
			newSelectionIndex.add(Integer.valueOf(files
				.indexOf(file)));
		    } else {
			newSelectionIndex = getSelectionIndex();
			newSelectionIndex.add((Integer) files.indexOf(file));
		    }
		    setSelectionIndex(newSelectionIndex);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}
	    });
	    editor.grabHorizontal = true;
	    editor.setEditor(check, item, 0);

	    editor = new GridEditor(grid);
	    final Link link = new Link(grid, SWT.RIGHT);
	    editor.grabHorizontal = true;
	    editor.setEditor(link, item, 1);
	    link.setText("<a>" + file.getFileName() + "</a>");
	    link.addSelectionListener(new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent event) {
		    String url = file.getFileName();
		    Program.launch(url);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
	    });

	}
    }

    /**
     * @return the files
     */
    public synchronized List<Attachment> getFiles() {
	return Collections.unmodifiableList(files);
    }

    /**
     * @param files
     *            the files to set
     */
    public synchronized void setFiles(List<Attachment> files) {
	List<Attachment> oldValue = this.files;
	this.files = files;
	changeSupport.firePropertyChange("files", oldValue, this.files);
    }

    /**
     * @return the selectionIndex
     */
    private synchronized List<Integer> getSelectionIndex() {
	return new ArrayList<Integer>(selectionIndex);
    }

    /**
     * @param selectionIndex
     *            the selectionIndex to set
     */
    private synchronized void setSelectionIndex(List<Integer> selectionIndex) {
	List<Integer> oldValue = this.selectionIndex;
	this.selectionIndex = selectionIndex;
	changeSupport.firePropertyChange("selection", oldValue,
		this.selectionIndex);
    }

    /**
     * @return the selection
     */
    public synchronized List<Attachment> getSelection() {
	List<Attachment> selection = new ArrayList<Attachment>(
		this.selectionIndex.size());
	for (int index : this.selectionIndex) {
	    selection.add(files.get(index));
	}
	return Collections.unmodifiableList(selection);
    }

    /**
     * @param selection
     *            the selection to set
     */
    public synchronized void setSelection(List<Attachment> selection) {
	List<Integer> oldValue = this.selectionIndex;
	List<Integer> newSelectionIndex = new ArrayList<Integer>(
		selection.size());
	for (Attachment t : selection) {
	    int index = files.indexOf(t);
	    if (index >= 0) {
		newSelectionIndex.add(files.indexOf(t));
	    }
	}
	this.selectionIndex = newSelectionIndex;
	changeSupport.firePropertyChange("selection", oldValue,
		this.selectionIndex);
    }

}
