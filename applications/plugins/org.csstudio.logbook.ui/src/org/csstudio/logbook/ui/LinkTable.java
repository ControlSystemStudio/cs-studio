package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.logbook.Attachment;
import org.csstudio.ui.util.composites.BeanComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class LinkTable extends BeanComposite {

    private List<Attachment> files = Collections.emptyList();
    private List<Integer> selectionIndex = new ArrayList<Integer>();
    private ErrorBar errorBar;
    private Composite composite;

    public LinkTable(Composite parent, int style) {
	super(parent, style);
	setLayout(new GridLayout(1, false));

	errorBar = new ErrorBar(this, SWT.NONE);
	errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));

	Label lblNewLabel = new Label(this, SWT.NONE);
	lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
		false, 1, 1));
	lblNewLabel.setText("Attachments:");

	composite = new Composite(this, SWT.NONE);
	composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
		1));

	composite.setLayout(new GridLayout(2, false));

	addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
		switch (event.getPropertyName()) {
		case "files":
		    updateGrid(composite);
		    break;
		default:
		    break;
		}
	    }
	});

	updateGrid(composite);

    }

    private void updateGrid(Composite parent) {
	for (Control control : parent.getChildren()) {
	    control.dispose();
	}
	parent.redraw();
	for (final Attachment attachment : files) {
	    final Button check = new Button(parent, SWT.CHECK);
	    check.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
		    false, 1, 1));

	    check.addSelectionListener(new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent e) {
		    List<Integer> newSelectionIndex;
		    if (check.getSelection()) {
			newSelectionIndex = getSelectionIndex();
			newSelectionIndex.add(Integer.valueOf(files
				.indexOf(attachment)));
		    } else {
			newSelectionIndex = getSelectionIndex();
			newSelectionIndex.add((Integer) files
				.indexOf(attachment));
		    }
		    setSelectionIndex(newSelectionIndex);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		    // TODO Auto-generated method stub

		}
	    });

	    final Link link = new Link(parent, SWT.RIGHT);
	    link.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		    1, 1));

	    link.setText("<a>" + attachment.getFileName() + "</a>");
	    link.addSelectionListener(new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent event) {
		    linkAction(attachment);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
	    });

	}
	parent.layout();
	parent.redraw();
    }

    public void linkAction(Attachment attachment) {
	String url = attachment.getFileName();
	Program.launch(url);
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
