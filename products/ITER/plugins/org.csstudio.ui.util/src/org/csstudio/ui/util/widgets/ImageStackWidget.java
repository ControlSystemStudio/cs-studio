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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;

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
	super(parent, SWT.NONE);
	setLayout(new GridLayout(3, false));
	imagePreview = new ImagePreview(this);
	imagePreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
	
	Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
	label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 2));
	
	Label lblImages = new Label(this, SWT.NONE);
	lblImages.setText("Images:");
		
			tableViewer = new TableViewer(this, SWT.DOUBLE_BUFFERED);
			table = tableViewer.getTable();
			table.addMouseTrackListener(new MouseTrackAdapter() {
				@Override
				public void mouseHover(MouseEvent e) {
				}
			});
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
			table.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			
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
	this.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		if("editable".equals(evt.getPropertyName())) {
		} else if("imageInputStreamsMap".equals(evt.getPropertyName())) {
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
		} else if("selectedImageName".equals(evt.getPropertyName())) {
		    imagePreview.setImage(new ByteArrayInputStream(
			    imageInputStreamsMap.get(selectedImageName)));
		    imagePreview.redraw();
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
