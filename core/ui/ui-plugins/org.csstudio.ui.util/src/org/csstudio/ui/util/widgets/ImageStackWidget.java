/**
 * 
 */
package org.csstudio.ui.util.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * A widget to display a set of Images
 * 
 * @author shroffk
 * 
 */
public class ImageStackWidget extends Composite {

    private boolean editable;
    private String selectedImageName;
    private boolean scrollBarVisble;

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
	    this);
    private ImagePreview imagePreview;
    private Table table;
    private TableViewer tableViewer;
    private Map<String, byte[]> imageInputStreamsMap = new HashMap<String, byte[]>();
    private Button buttonRemove;
    private TableViewerColumn tableViewerColumn;
    private TableColumn tblclmnImage;

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
	setLayout(new FormLayout());

	Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
	FormData fd_label = new FormData();
	fd_label.bottom = new FormAttachment(100, 5);
	fd_label.top = new FormAttachment(0, 5);
	fd_label.right = new FormAttachment(100, 100, -125);
	label.setLayoutData(fd_label);

	Label lblImages = new Label(this, SWT.NONE);
	FormData fd_lblImages = new FormData();
	fd_lblImages.left = new FormAttachment(label, 5);
	fd_lblImages.top = new FormAttachment(0, 5);
	lblImages.setLayoutData(fd_lblImages);
	lblImages.setText("Images:");

	tableViewer = new TableViewer(this, SWT.DOUBLE_BUFFERED | SWT.NO_SCROLL
		| SWT.V_SCROLL);
	table = tableViewer.getTable();
	FormData fd_table = new FormData();
	fd_table.left = new FormAttachment(label, 5);
	fd_table.right = new FormAttachment(100, -5);
	fd_table.bottom = new FormAttachment(100, -5);
	fd_table.top = new FormAttachment(0, 30);
	table.setLayoutData(fd_table);
	table.setBackground(SWTResourceManager
		.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

	tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
	tableViewerColumn.setLabelProvider(new OwnerDrawLabelProvider() {
		@Override
		protected void paint(Event event, Object element) {
			String imageName = element == null ? "" : element.toString();
			ImageData imageData = new ImageData(new ByteArrayInputStream(
				imageInputStreamsMap.get(imageName)));
			int width = scrollBarVisble ? 90 : 100;
			double scale = determineImageScale(imageData, width, width);
			Image img = new Image(getDisplay(), imageData.scaledTo(
					(int) (imageData.width * scale),
					(int) (imageData.height * scale)));
			if (img != null) {
				Rectangle bounds = ((TableItem) event.item)
						.getBounds(event.index);
				Rectangle imgBounds = img.getBounds();
				bounds.width /= 2;
				bounds.width -= imgBounds.width / 2;
				bounds.height /= 2;
				bounds.height -= imgBounds.height / 2;

				int x = bounds.width > 0 ? bounds.x + bounds.width
						: bounds.x;
				int y = bounds.height > 0 ? bounds.y + bounds.height
						: bounds.y;

				event.gc.drawImage(img, x, y);
			}
		}
		
		@Override
		protected void measure(Event event, Object element) {
			String imageName = element == null ? "" : element.toString();
			ImageData imageData = new ImageData(new ByteArrayInputStream(
					imageInputStreamsMap.get(imageName)));
			double scale = determineImageScale(imageData, 85, 85);
			event.height = (int) (scale * imageData.height) + 10;
		}
		
		private double determineImageScale(ImageData imgData,
				int targetWidth, int targetHeight) {
			if (imgData == null) {
				return 1;
			}
			double scalex = (double) targetWidth / imgData.width;
			double scaley = (double) targetHeight / imgData.height;
			double ratio = Math.min(scalex, scaley);
			if (ratio > 1) {
				return 1;
			}
			return ratio;
		}
	});

	table.addPaintListener(new PaintListener() {

	    public void paintControl(PaintEvent e) {
		Rectangle rect = table.getClientArea();
		int itemHeight = table.getItemHeight();
		int headerHeight = table.getHeaderHeight();
		int visibleCount = (rect.height - headerHeight + itemHeight - 1)
			/ itemHeight;
		setScrollBarVisble(table.getItemCount() >= visibleCount);
	    }
	});

	tblclmnImage = tableViewerColumn.getColumn();
	tblclmnImage.setResizable(false);
	tblclmnImage.setWidth(104);
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

	buttonRemove = new Button(this, SWT.NONE);
	buttonRemove.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		try {
		    removeImage(getSelectedImageName());
		} catch (IOException e1) {
		}
	    }
	});
	buttonRemove.setImage(ResourceManager.getPluginImage(
		"org.csstudio.ui.util", "icons/remove-16.gif"));
	FormData fd_lblNewLabel = new FormData();
	fd_lblNewLabel.right = new FormAttachment(label, -5);
	fd_lblNewLabel.top = new FormAttachment(0, 5);
	buttonRemove.setLayoutData(fd_lblNewLabel);
	buttonRemove.setText("Remove");
	buttonRemove.setVisible(false);

	imagePreview = new ImagePreview(this);
	FormData fd_imagePreview = new FormData();
	fd_imagePreview.right = new FormAttachment(label, -5);
	fd_imagePreview.bottom = new FormAttachment(100, -5);
	fd_imagePreview.top = new FormAttachment(0, 5);
	fd_imagePreview.left = new FormAttachment(0, 5);
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
			    Entry<String, byte[]> next = imageInputStreamsMap
				    .entrySet().iterator().next();
			    imagePreview.setImage(new ByteArrayInputStream(next
				    .getValue()));
			    selectedImageName = next.getKey();
			    buttonRemove.setVisible(true && editable);
			    table.setSelection(0);
			}
		    } else {
			tableViewer.setInput(null);
			imagePreview.setImage((InputStream) null);
			selectedImageName = null;
		    }
		    tableViewer.refresh();
		    table.redraw();
		    imagePreview.redraw();
		    break;
		case "selectedImageName":
		    imagePreview.setImage(new ByteArrayInputStream(
			    imageInputStreamsMap.get(selectedImageName)));
		    buttonRemove.setVisible(true && editable);
		    imagePreview.redraw();
		    for (int index = 0; index < table.getItemCount(); index++) {
		    	if(selectedImageName.equals(
		    			table.getItem(index).getData())) {
		    		table.select(index);
				    table.redraw();
				    break;
		    	}
		    }
		    break;
		case "scrollBarVisible":
		    tblclmnImage.setWidth(scrollBarVisble ? 94 : 104);
		    tableViewer.getTable().layout();
		    tableViewer.refresh();
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
     * Remove the Image identified by name
     * 
     * @param name
     *            - the name of the Image to be removed
     * @throws IOException
     */
    public void removeImage(String name) throws IOException {
	if (imageInputStreamsMap.containsKey(name)) {
	    Map<String, byte[]> oldValue = new HashMap<String, byte[]>(
		    this.imageInputStreamsMap);
	    this.imageInputStreamsMap.remove(name);
	    changeSupport.firePropertyChange("imageInputStreamsMap", oldValue,
		    this.imageInputStreamsMap);
	}
    }

    /**
     * @param scrollBarVisble
     *            the scrollBarVisble to set
     */
    private void setScrollBarVisble(boolean scrollBarVisble) {
	boolean oldValue = this.scrollBarVisble;
	this.scrollBarVisble = scrollBarVisble;
	changeSupport.firePropertyChange("scrollBarVisible", oldValue,
		this.scrollBarVisble);
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
