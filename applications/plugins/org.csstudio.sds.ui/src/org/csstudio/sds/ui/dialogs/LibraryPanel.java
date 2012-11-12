package org.csstudio.sds.ui.dialogs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class LibraryPanel extends Composite {

	public enum LibraryPanelLayout {
		GroupedGrid, UngroupedGrid, UngroupedList;
	}

	private Gallery gallery;

	private List<File> sortedDisplays = new ArrayList<File>();
	private Map<File, GalleryItem> displayFileToGalleryItemMap = new HashMap<File, GalleryItem>();
	private Map<GalleryItem, File> galleryItemToDisplayFileMap = new HashMap<GalleryItem, File>();
	private List<File> folderList;

	private final ThumbnailCreator thumbnailCreator = new ThumbnailCreator();

	private IResourceChangeListener resourceChangeListener;

	public LibraryPanel(Composite parent, int style,
			LibraryPanelLayout libraryLayout) {
		super(parent, style);

		setLayout(new FillLayout());

		gallery = new Gallery(this, SWT.V_SCROLL | SWT.SINGLE);
		setLibraryLayout(libraryLayout);

		gallery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				try {
					File selectedFile = getSelectedFile();
					if (selectedFile != null) {
						IWorkspace workspace = ResourcesPlugin.getWorkspace();
						IWorkspaceRoot root = workspace.getRoot();
						IFile file = root.getFileForLocation(new Path(
								selectedFile.getAbsolutePath()));
						IEditorInput editorInput = new FileEditorInput(file);
						IWorkbenchWindow window = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow();
						IWorkbenchPage page = window.getActivePage();
						page.openEditor(editorInput,
								"org.csstudio.sds.ui.internal.editor.DisplayEditor");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		initResourceListener();
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
			itemHeight = 40;
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
					getDisplay().asyncExec(new Runnable() {
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

	public Gallery getGallery() {
		return gallery;
	}

	private GalleryItem getSelected() {

		GalleryItem[] selection = gallery.getSelection();
		if (selection != null && selection.length > 0) {
			return selection[0];
		} else {
			return null;
		}
	}

	public void setFolders(List<File> folderList) {

		this.folderList = folderList;
		sortedDisplays.clear();
		displayFileToGalleryItemMap.clear();
		galleryItemToDisplayFileMap.clear();

		gallery.clearAll();
		gallery.removeAll();

		for (File folder : folderList) {

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

		createThumbnailsAsync();
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

	public File getSelectedFile() {
		GalleryItem selected = getSelected();
		return selected != null ? galleryItemToDisplayFileMap.get(selected)
				: null;
	}

	private void createThumbnailsAsync() {
		// FIXME gs, fz: Handle disposed display correctly!
		final Display display = Display.getDefault();
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (final File file : sortedDisplays) {

					final ImageData imageData = thumbnailCreator.createImage(
							file, 100, display);
					if (!display.isDisposed()) {
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								GalleryItem item = displayFileToGalleryItemMap
										.get(file);
								if (!display.isDisposed()) {
									Image image = new Image(display, imageData);
									if (!image.isDisposed()) {
										item.setImage(image);
									}
								}
							}
						});
					}
				}

			}
		}).start();
	}

	@Override
	public void dispose() {
		super.dispose();

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceChangeListener);
	}
}
