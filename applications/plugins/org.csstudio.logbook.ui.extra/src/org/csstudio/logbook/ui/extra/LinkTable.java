package org.csstudio.logbook.ui.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

public class LinkTable extends BeanComposite {
    private List<Attachment> files = Collections.emptyList();
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

	// First Columns displays the Date
	GridViewerColumn columnFile = new GridViewerColumn(gridTableViewer,
		SWT.NONE);
	new ColumnViewerSimpleLayout(gridTableViewer, columnFile, 100, 50);
	columnFile.getColumn().setText("Attached Files:");
	columnFile.getColumn().setWordWrap(true);
	createTable();
    }

    private void createTable() {
	grid.removeAll();
	for (final Attachment file : files) {
	    GridItem item = new GridItem(grid, SWT.NONE);
	    GridEditor editor = new GridEditor(grid);
	    final Link link = new Link(grid, SWT.NONE);
	    editor.grabHorizontal = true;
	    editor.setEditor(link, item, 0);
	    link.setText("<a>" + file.getFileName() + "</a>");
	    link.addSelectionListener(new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent event) {
		    String url = file.getFileName();
//		    url = url.substring("<a>".length(),
//			    url.length() - "</a>".length());
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

}
