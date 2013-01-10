package org.csstudio.sds.ui.thumbnail;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.ui.DisplayInfoService;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.nebula.widgets.gallery.AbstractGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.AbstractGridGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.ListItemRenderer;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SdsThumbnailPanel extends Composite {

	public enum LibraryPanelLayout {
		GroupedGrid, UngroupedGrid, UngroupedList;
	}

	private List<LibraryPanelListener> listeners;

	private Gallery gallery;

	private List<File> sortedDisplays = new ArrayList<File>();
	private Map<File, GalleryItem> displayFileToGalleryItemMap = new HashMap<File, GalleryItem>();
	private Map<GalleryItem, File> galleryItemToDisplayFileMap = new HashMap<GalleryItem, File>();
	private List<File> folderList;

	private final DisplayInfoService displayInfoService = new DisplayInfoService();

	private IResourceChangeListener resourceChangeListener;

	private ThumbnailCreationRunnable currentThumbnailCreationRunnable;

	public SdsThumbnailPanel(Composite parent, int style,
			LibraryPanelLayout libraryLayout) {
		super(parent, style);

		this.listeners = new ArrayList<SdsThumbnailPanel.LibraryPanelListener>();

		setLayout(new FillLayout());

		gallery = new Gallery(this, SWT.V_SCROLL | SWT.SINGLE);
		setLibraryLayout(libraryLayout);

		gallery.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fireSelectionChanged();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				fireSelectionChanged();
			}

		});

		initResourceListener();
	}

	public void addLibraryPanelListener(LibraryPanelListener listener) {
		assert listener != null : "Precondition failed: listener != null";

		this.listeners.add(listener);
	}

	public void removeLibraryPanelListener(LibraryPanelListener listener) {
		assert listener != null : "Precondition failed: listener != null";

		this.listeners.remove(listener);
	}

	@Override
	public void addMouseListener(MouseListener mouseListener) {
		this.gallery.addMouseListener(mouseListener);
	}

	@Override
	public void removeMouseListener(MouseListener mouseListener) {
		this.gallery.removeMouseListener(mouseListener);
	}

	public void setLibraryLayout(LibraryPanelLayout layout) {
		assert layout != null : "Precondition failed: layout != null";
		AbstractGalleryItemRenderer itemRenderer = null;
		final AbstractGridGroupRenderer groupRenderer;
		int itemHeight = 120;

		switch (layout) {
		case GroupedGrid:
			itemRenderer = new DefaultGalleryItemRenderer();
			groupRenderer = new DefaultGalleryGroupRenderer();

			break;
		case UngroupedGrid:
			itemRenderer = new DefaultGalleryItemRenderer();
			groupRenderer = new NoGroupRenderer();

			break;
		case UngroupedList:
			itemRenderer = new ListItemRenderer();
			itemHeight = 20;
			groupRenderer = new NoGroupRenderer();
			gallery.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					int newWidth = gallery.getClientArea().width;
					if (newWidth > 0) {
						groupRenderer.setItemWidth(newWidth);
					}
				}
			});

			break;
		default:
			itemRenderer = new DefaultGalleryItemRenderer();
			groupRenderer = new DefaultGalleryGroupRenderer();
			break;
		}

		groupRenderer.setItemHeight(itemHeight);
		groupRenderer.setItemWidth(100);
		groupRenderer.setAutoMargin(true);
		gallery.setGroupRenderer(groupRenderer);
		gallery.setItemRenderer(itemRenderer);
	}

	public Gallery getGallery() {
		return gallery;
	}

	public File getSelectedFile() {
		GalleryItem selected = getSelected();
		return selected != null ? galleryItemToDisplayFileMap.get(selected)
				: null;
	}

	public void select(int groupIndex, int index) {
		assert index < getItemCount(groupIndex) : "Precondition failed: index < getItemCount("
				+ groupIndex + ")";
		if (groupIndex > -1 && index > -1) {
			GalleryItem group = gallery.getItem(groupIndex);
			gallery.setSelection(new GalleryItem[] { group.getItem(index) });
		} else {
			gallery.deselectAll();
		}
	}

	public int getSelectionIndex() {
		int result = -1;

		if (gallery.getSelectionCount() > 0) {
			result = gallery.indexOf(getSelected());
		}

		return result;
	}

	public int getSelectionGroupIndex() {
		int result = -1;

		if (gallery.getSelectionCount() > 0) {
			result = gallery.indexOf(getSelected().getParentItem());
		}

		return result;
	}

	public int getItemCount(int groupIndex) {
		return gallery.getItem(groupIndex).getItemCount();
	}

	public void setFolders(List<File> folderList) {
		assert folderList != null : "Precondition failed: folderList != null";

		this.folderList = folderList;
		sortedDisplays.clear();
		displayFileToGalleryItemMap.clear();
		galleryItemToDisplayFileMap.clear();

		gallery.clearAll();
		gallery.removeAll();

		for (File folder : folderList) {
			if(folder.exists()) {
				String text = getWorkspaceRelativePath(folder);

				final GalleryItem folderGroupItem = new GalleryItem(gallery,
						SWT.NONE);

				folderGroupItem.setText(text);
				folderGroupItem.setExpanded(true);
				FileFilter fileFilter = new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.isFile()
								&& pathname.getName().endsWith(".css-sds");
					}
				};

				for (File file : folder.listFiles(fileFilter)) {
					GalleryItem galleryItem = new GalleryItem(folderGroupItem,
							SWT.NONE);
					galleryItem.setText(file.getName().substring(0,
							file.getName().lastIndexOf(".")));
					addFileToLibrary(file, galleryItem);
				}
			}
		}

		fireSelectionChanged();

		createThumbnailsAsync();
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceChangeListener);
		super.dispose();
	}

	private void fireSelectionChanged() {
		for (LibraryPanelListener listener : listeners) {
			listener.onSelectionChanged(this);
		}
	}

	private void initResourceListener() {
		resourceChangeListener = new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				boolean relevantChange = false;

				IResourceDelta rootDelta = event.getDelta();
				for (File folder : folderList) {

					IResourceDelta folderDelta = rootDelta.findMember(new Path(
							getWorkspaceRelativePath(folder)));
					if (folderDelta != null) {

						// if (folderDelta.getKind() == IResourceDelta.REMOVED
						// || folderDelta.getKind() == IResourceDelta.MOVED_TO)
						// {
						// force complete update
						relevantChange = true;
						// return;
						// }
						//
						// handleFolderChanged(folderDelta);
					}
				}

				if (relevantChange) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							setFolders(folderList);
						}
					});
				}
			}

			// private void handleFolderChanged(IResourceDelta folderDelta) {
			//
			// try {
			// folderDelta.accept(new IResourceDeltaVisitor() {
			// public boolean visit(IResourceDelta delta) {
			//
			// IResource resource = delta.getResource();
			// // only interested in files with the "txt" extension
			//
			// if (resource.getType() == IResource.FILE
			// && "css-sds".equalsIgnoreCase(resource
			// .getFileExtension())) {
			//
			// if (resource.exists()) {
			//
			// resource.get
			//
			// }
			//
			// }
			// return true;
			// }
			// });
			// } catch (CoreException e) {
			// // open error dialog with syncExec or print to plugin log
			// // file
			// }
			// }
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceChangeListener, IResourceChangeEvent.POST_CHANGE);

	}

	private GalleryItem getSelected() {

		GalleryItem[] selection = gallery.getSelection();
		if (selection != null && selection.length > 0) {
			return selection[0];
		} else {
			return null;
		}
	}

	private String getWorkspaceRelativePath(File folder) {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath rootPath = root.getLocation();
		Path path = new Path(folder.getAbsolutePath());
		return path.makeRelativeTo(rootPath).toString();
	}

	private void addFileToLibrary(File file, GalleryItem galleryItem) {
		sortedDisplays.add(file);
		displayFileToGalleryItemMap.put(file, galleryItem);
		galleryItemToDisplayFileMap.put(galleryItem, file);
	}

	private void createThumbnailsAsync() {
		final Display display = Display.getDefault();
		final ArrayList<File> displayListCopy = new ArrayList<File>(sortedDisplays);
		if(currentThumbnailCreationRunnable != null) {
			currentThumbnailCreationRunnable.cancel();
		}
		currentThumbnailCreationRunnable = new ThumbnailCreationRunnable(displayListCopy, display);
		new Thread(currentThumbnailCreationRunnable).start();
	}

	public interface LibraryPanelListener {
		void onSelectionChanged(SdsThumbnailPanel libraryPanel);
	}
	
	private class ThumbnailCreationRunnable implements Runnable {

		private final List<File> displayList;
		private final Display display;
		
		private boolean isCanceled = false;

		public ThumbnailCreationRunnable(List<File> displayList, Display display) {
			this.displayList = displayList;
			this.display = display;
		}
		
		public void cancel() {
			this.isCanceled = true;
		}
		
		@Override
		public void run() {
			
			for (int fileIndex = 0; fileIndex < displayList.size() && !isCanceled; fileIndex++) {
				final File file = displayList.get(fileIndex);
				final ImageData imageData = displayInfoService.getImage(file);
				if (!display.isDisposed()) {
					display.syncExec(new Runnable() {
						@Override
						public void run() {
							Image image = null;
							try {

								GalleryItem item = displayFileToGalleryItemMap
										.get(file);
								if (item != null && !display.isDisposed()) {
									image = new Image(display, imageData);
									if (!item.isDisposed()) {
										item.setImage(image);
									}
								}
							} catch (SWTException swtException) {
								if (image != null) {
									image.dispose();
								}
							}
						}
					});
				}
			}
		}
	}
}
