/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.apputil.ui.swt.ImagePreview;
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
 * @author shroffk
 * 
 */
public class ImageStackWidget extends Composite {

	private boolean editable;
	private List<String> imageFilenames = new ArrayList<String>();
	private String selectedImageFile;

	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	private ImagePreview imagePreview;
	private Table table;
	private TableViewer tableViewer;

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
		fd_table.left = new FormAttachment(100, -120);
		table.setLayoutData(fd_table);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// TODO does not center
				// TODO does not preserve aspect ratio
				// use the OwnerDrawLabelProvider
				ImageData imageData = new ImageData(
						cell.getElement() == null ? "" : cell.getElement()
								.toString());
				cell.setImage(new Image(getDisplay(), imageData.scaledTo(100,
						100)));
			}
		});
		TableColumn tblclmnImage = tableViewerColumn.getColumn();
		tblclmnImage.setResizable(false);
		tblclmnImage.setWidth(110);
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
								setSelectedImageFile((String) sel.iterator()
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
				case "imageFilenames":
					if (imageFilenames != null) {
						// Populate the list on the side
						tableViewer.setInput(imageFilenames
								.toArray(new String[imageFilenames.size()]));
						// Set the Selected Image
						if (imageFilenames.contains(selectedImageFile)) {
							imagePreview.setImage(selectedImageFile);
						} else {
							imagePreview.setImage(imageFilenames.iterator()
									.next());
						}
						tableViewer.refresh();
						imagePreview.redraw();
					}
					break;
				case "selectedImageFile":
					imagePreview.setImage(selectedImageFile);
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

	public void setImageFilenames(List<String> imageFilenames) {
		List<String> oldValue = this.imageFilenames;
		this.imageFilenames = imageFilenames;
		changeSupport.firePropertyChange("imageFilenames", oldValue,
				this.imageFilenames);
	}

	public List<String> getImageFilenames() {
		return imageFilenames;
	}

	public String getSelectedImageFile() {
		return selectedImageFile;
	}

	public void setSelectedImageFile(String selectedImageFile) {
		String oldValue = this.selectedImageFile;
		this.selectedImageFile = selectedImageFile;
		changeSupport.firePropertyChange("selectedImageFile", oldValue,
				this.imageFilenames);
	}

}
