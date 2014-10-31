/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map.Entry;

import org.csstudio.logbook.Property;
import org.csstudio.ui.util.composites.BeanComposite;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author shroffk
 * 
 */
public class PropertyTree extends BeanComposite {

    private Collection<Property> properties;

    private TreeViewer treeViewer;

    public PropertyTree(Composite parent, int style) {
	super(parent, style);

	addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("properties")) {
		    treeViewer.setInput(properties);
		}
	    }
	});
	GridLayout gridLayout = new GridLayout(1, false);
	gridLayout.marginHeight = 0;
	gridLayout.marginWidth = 0;
	setLayout(gridLayout);

	Composite composite = new Composite(this, SWT.NONE);
	composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	TreeColumnLayout tcl_composite = new TreeColumnLayout();
	composite.setLayout(tcl_composite);

	treeViewer = new TreeViewer(composite, SWT.BORDER);
	Tree tree = treeViewer.getTree();
	tree.setLinesVisible(true);

	TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer,
		SWT.NONE);
	treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
	    public Image getImage(Object element) {
		return null;
	    }

	    @SuppressWarnings("unchecked")
	    public String getText(Object element) {
		if (element instanceof Property) {
		    return ((Property) element).getName();
		} else if (element instanceof Entry) {
		    return ((Entry<String, String>) element).getKey();
		}
		return "";
	    }
	});
	TreeColumn trclmnNewColumn = treeViewerColumn.getColumn();
	tcl_composite.setColumnData(trclmnNewColumn, new ColumnWeightData(10,
		ColumnWeightData.MINIMUM_WIDTH, true));
	trclmnNewColumn.setText("Name");

	TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(treeViewer,
		SWT.NONE);
	treeViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
	    public Image getImage(Object element) {
		return null;
	    }

	    @SuppressWarnings("unchecked")
	    public String getText(Object element) {
		if (element instanceof Entry) {
		    return ((Entry<String, String>) element).getValue();
		}
		return "";
	    }
	});
	TreeColumn trclmnNewColumn_1 = treeViewerColumn_1.getColumn();
	tcl_composite.setColumnData(trclmnNewColumn_1, new ColumnWeightData(7,
		ColumnWeightData.MINIMUM_WIDTH, true));
	trclmnNewColumn_1.setText("Description");
	treeViewer.setContentProvider(new PropertyTreeContentProvider());
	treeViewer.setInput(properties);
    }

    /**
     * @return the properties
     */
    public synchronized Collection<Property> getProperties() {
	return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public synchronized void setProperties(Collection<Property> properties) {
	Collection<Property> oldValue = this.properties;
	this.properties = properties;
	changeSupport.firePropertyChange("properties", oldValue,
		this.properties);
    }

}
