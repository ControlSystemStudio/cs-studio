/**
 * 
 */
package org.csstudio.ui.util.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A widget to display a set of Images
 * 
 * @author shroffk
 * 
 */
public class ImageStackWidget extends Composite {

    private boolean editable;
    private String selectedImageName;

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
	    this);
    private ImagePreview imagePreview;
    private Table table;
    private TableViewer tableViewer;
    private Map<String, InputStream> imageInputStreamsMap = new HashMap<String, InputStream>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.removePropertyChangeListener(listener);
    }

    public ImageStackWidget(final Composite parent, int style) {
	super(parent, style);
	setLayout(new FormLayout());

	tableViewer = new TableViewer(this, SWT.NONE);
	table = tableViewer.getTable();
	table.setBackground(SWTResourceManager
		.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	FormData fd_table = new FormData();
	fd_table.top = new FormAttachment(0, 10);
	fd_table.right = new FormAttachment(100, -10);
	fd_table.bottom = new FormAttachment(100, -10);
	fd_table.left = new FormAttachment(100, -124);
	table.setLayoutData(fd_table);

	TableViewerColumn tableViewerColumn = new TableViewerColumn(
		tableViewer, SWT.NONE);
	tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {
	    @Override
	    public void update(ViewerCell cell) {
		// TODO does not center
		// TODO does not preserve aspect ratio
		// use the OwnerDrawLabelProvider
		String imageName = cell.getElement() == null ? "" : cell
			.getElement().toString();
		ImageData imageData = new ImageData(imageInputStreamsMap
			.get(imageName));
		cell.setImage(new Image(getDisplay(), imageData
			.scaledTo(90, 90)));
	    }
	});
	TableColumn tblclmnImage = tableViewerColumn.getColumn();
	tblclmnImage.setResizable(false);
	tblclmnImage.setWidth(90);
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

	tableViewer
		.addSelectionChangedListener(new ISelectionChangedListener() {

		    @Override
		    public void selectionChanged(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();
			if (selection != null
				&& selection instanceof IStructuredSelection) {
			    IStructuredSelection sel = (IStructuredSelection) selection;
			    if (sel.size() == 1) {
				setSelectedImageName((String) sel.iterator()
					.next());
			    }
			}
		    }
		});
	imagePreview = new ImagePreview(this);
	FormData fd_imagePreview = new FormData();
	fd_imagePreview.right = new FormAttachment(table, -10);
	fd_imagePreview.top = new FormAttachment(0, 10);
	fd_imagePreview.left = new FormAttachment(0, 10);
	fd_imagePreview.bottom = new FormAttachment(100, -10);
	imagePreview.setLayoutData(fd_imagePreview);
	this.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case "editable":
		    break;
		case "imageInputStreamsMap":
		    if (imageInputStreamsMap != null
			    && !imageInputStreamsMap.isEmpty()) {
			// Populate the list on the side
			tableViewer.setInput(imageInputStreamsMap.keySet()
				.toArray(
					new String[imageInputStreamsMap
						.keySet().size()]));
			// tableViewer.setInput(imageInputStreamsMap.entrySet().toArray(new
			// Entry<String,InputStream>[]));
			// Set the Selected Image
			if (imageInputStreamsMap.keySet().contains(
				selectedImageName)) {
			    imagePreview.setImage(imageInputStreamsMap
				    .get(selectedImageName));
			} else {
			    imagePreview.setImage(imageInputStreamsMap.values()
				    .iterator().next());
			}
		    } else {
			tableViewer.setInput(null);
			imagePreview.setImage((InputStream) null);
		    }
		    tableViewer.refresh();
		    imagePreview.redraw();
		    break;
		case "selectedImageName":
		    imagePreview.setImage(imageInputStreamsMap
			    .get(selectedImageName));
		    imagePreview.redraw();
		    break;
		default:
		    break;
		}

	    }
	});
    }

    public boolean isEditable() {
	return editable;
    }

    public void setEditable(boolean editable) {
	boolean oldValue = this.editable;
	this.editable = editable;
	changeSupport.firePropertyChange("editable", oldValue, this.editable);
    }

    public void setImageInputStream(
	    Map<String, InputStream> imageInputStreamsMap) {
	Map<String, InputStream> oldValue = this.imageInputStreamsMap;
	this.imageInputStreamsMap = imageInputStreamsMap == null ? new HashMap<String, InputStream>()
		: imageInputStreamsMap;
	changeSupport.firePropertyChange("imageInputStreamsMap", oldValue,
		this.imageInputStreamsMap);
    }

    public void addImage(String imageName, InputStream imageInputStream) {
	Map<String, InputStream> oldValue = this.imageInputStreamsMap;
	this.imageInputStreamsMap.put(imageName, imageInputStream);
	changeSupport.firePropertyChange("imageInputStreamsMap", oldValue,
		this.imageInputStreamsMap);
    }

    public List<String> getImageFilenames() {
	return (List<String>) imageInputStreamsMap.keySet();
    }

    public String getSelectedImageFile() {
	return selectedImageName;
    }

    public void setSelectedImageName(String selectedImageName) {
	String oldValue = this.selectedImageName;
	this.selectedImageName = selectedImageName;
	changeSupport.firePropertyChange("selectedImageName", oldValue,
		this.selectedImageName);
    }

}
