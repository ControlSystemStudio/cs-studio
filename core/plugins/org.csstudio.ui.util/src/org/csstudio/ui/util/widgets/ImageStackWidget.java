/**
 * 
 */
package org.csstudio.ui.util.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    private Map<String, byte[]> imageInputStreamsMap = new HashMap<String, byte[]>();

    /**
     * Adds a listener, notified a porperty has been changed.
     * 
     * @param listener
     *            a new listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener.
     * 
     * @param listener
     *            listener to be removed
     */
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
		ImageData imageData = new ImageData(new ByteArrayInputStream(
			imageInputStreamsMap.get(imageName)));
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
			if (imageInputStreamsMap.keySet().contains(
				selectedImageName)) {
			    imagePreview
				    .setImage(new ByteArrayInputStream(
					    imageInputStreamsMap
						    .get(selectedImageName)));
			} else {
			    imagePreview.setImage(new ByteArrayInputStream(
				    imageInputStreamsMap.values().iterator()
					    .next()));
			}
		    } else {
			tableViewer.setInput(null);
			imagePreview.setImage((InputStream) null);
		    }
		    tableViewer.refresh();
		    imagePreview.redraw();
		    break;
		case "selectedImageName":
		    imagePreview.setImage(new ByteArrayInputStream(
			    imageInputStreamsMap.get(selectedImageName)));
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

    /**
     * Set multiple Images to the widget, this will remove all existing images.
     * In the imageInputStreamMap - the key defines the imageName and the value
     * is an inputStream to the Image
     * 
     * @param imageInputStreamsMap
     *            - a map of image names and image input streams
     * @throws IOException
     */
    public void setImageInputStreamsMap(
	    Map<String, InputStream> imageInputStreamsMap) throws IOException {
	Map<String, byte[]> oldValue = this.imageInputStreamsMap;
	this.imageInputStreamsMap = new HashMap<String, byte[]>();
	for (Entry<String, InputStream> test : imageInputStreamsMap.entrySet()) {
	    this.imageInputStreamsMap.put(test.getKey(),
		    read2byteArray(test.getValue()));
	}
	changeSupport.firePropertyChange("imageInputStreamsMap", oldValue,
		this.imageInputStreamsMap);
    }

    /**
     * Add a single Image to the stack
     * 
     * @param name
     *            - the name to Identify the Image.
     * @param imageInputStream
     *            - an inputStream for the Image to be added.
     * @throws IOException
     */
    public void addImage(String name, InputStream imageInputStream)
	    throws IOException {
	Map<String, byte[]> oldValue = new HashMap<String, byte[]>(
		this.imageInputStreamsMap);
	this.imageInputStreamsMap.put(name, read2byteArray(imageInputStream));
	changeSupport.firePropertyChange("imageInputStreamsMap", oldValue,
		this.imageInputStreamsMap);
    }

    /**
     * Return an InputStream for the Image identified by name
     * 
     * @param name
     *            - name of the Image
     * @return InputStream - to the Image identified by name
     */
    public InputStream getImage(String name) {
	return new ByteArrayInputStream(imageInputStreamsMap.get(name));
    }

    /**
     * get a set of all the image Names associated with the Images being
     * displayed by this widget
     * 
     * @return Set of strings containing the names of all Images
     */
    public Set<String> getImageNames() {
	return imageInputStreamsMap.keySet();
    }

    /**
     * get the name of the current Image in focus
     * 
     * @return String imageName of the Image in focus
     */
    public String getSelectedImageName() {
	return selectedImageName;
    }

    /**
     * set the Image to be brought into focus using its imageName
     * 
     * @param selectedImageName
     */
    public void setSelectedImageName(String selectedImageName) {
	String oldValue = this.selectedImageName;
	this.selectedImageName = selectedImageName;
	changeSupport.firePropertyChange("selectedImageName", oldValue,
		this.selectedImageName);
    }

    private static byte[] read2byteArray(InputStream input) throws IOException {
	byte[] buffer = new byte[8192];
	int bytesRead;
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	while ((bytesRead = input.read(buffer)) != -1) {
	    output.write(buffer, 0, bytesRead);
	}
	return output.toByteArray();
    }
}
