package org.csstudio.sds.ui.sdslibrary.views;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.actions.WidgetModelTransfer;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.csstudio.sds.ui.sdslibrary.preferences.LibraryFolderPreferenceItem;
import org.csstudio.sds.ui.sdslibrary.preferences.LibraryFolderPreferenceService;
import org.csstudio.sds.ui.sdslibrary.preferences.LibraryFolderPreferenceService.LibraryFolderPreferenceChangeListener;
import org.csstudio.sds.ui.thumbnail.SdsThumbnailPanel;
import org.csstudio.sds.ui.thumbnail.SdsThumbnailPanel.LibraryPanelLayout;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdsLibraryView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.sds.ui.thumbnailspike.views.SpikeView";

	private static final Logger LOG = LoggerFactory.getLogger(SdsLibraryView.class);

	private final LibraryFolderPreferenceService libraryFolderPreferenceService;

	private LibraryFolderPreferenceChangeListener preferenceChangeListener;

	private SdsThumbnailPanel libraryPanel;

	/**
	 * The constructor.
	 * 
	 * @throws FileNotFoundException
	 */
	public SdsLibraryView() {
		libraryFolderPreferenceService = SdsUiPlugin.getDefault()
				.getLibraryFolderPreferenceService();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());

		libraryPanel = new SdsThumbnailPanel(parent, SWT.None, LibraryPanelLayout.GroupedGrid);
		libraryPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				try {
					File selectedFile = libraryPanel.getSelectedFile();
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


		preferenceChangeListener = new LibraryFolderPreferenceChangeListener() {
			@Override
			public void preferencesChanged() {
				createLibraryWidgets(libraryFolderPreferenceService.loadLibraryItems());
			}
		};
		libraryFolderPreferenceService.addChangeListener(preferenceChangeListener);

		initDragAndDrop();

		createLibraryWidgets(libraryFolderPreferenceService.loadLibraryItems());
	}

	private void initDragAndDrop() {
		DragSource dragSource = new DragSource(libraryPanel.getGallery(), DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] { WidgetModelTransfer
				.getInstance() });
		SdsLibraryDragSourceEffect effect = new SdsLibraryDragSourceEffect(libraryPanel, getSite().getPage());
		
		dragSource.setDragSourceEffect(effect);
		dragSource.addDragListener(new DragSourceAdapter() {

			public void dragStart(DragSourceEvent event) {
				event.doit = libraryPanel.getSelectedFile() != null;
			}

			public void dragSetData(DragSourceEvent event) {
				DisplayModel model = new DisplayModel();
				FileInputStream fip;
				try {
					fip = new FileInputStream(libraryPanel.getSelectedFile());
					PersistenceUtil.syncFillModel(model, fip);
					try {
						fip.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
				}
				event.data = model.getWidgets();
			}
		});
	}
	
	@Override
	public void dispose() {
		if (preferenceChangeListener != null) {
			this.libraryFolderPreferenceService.removeChangeListener(preferenceChangeListener);
		}
		
		super.dispose();
	}

	@Override
	public void setFocus() {
		libraryPanel.setFocus();
	}

	private void createLibraryWidgets(List<LibraryFolderPreferenceItem> libraryFolders) {
		assert libraryFolders != null;

		List<File> folders = new ArrayList<File>();
		
		String filePathPrefix = ResourcesPlugin.getWorkspace().getRoot()
				.getRawLocation().toOSString();
		for (LibraryFolderPreferenceItem folderItem : libraryFolders) {
			if (folderItem.isChecked()) {
				File folder = new File(filePathPrefix
						+ folderItem.getFolderPath());

				if(!folder.isDirectory()) {
					LOG.warn("library preference folder: \""+folder.getAbsolutePath() + "\" does not exist");
				}
				else {
					folders.add(folder);
				}
			}
		}
		
		libraryPanel.setFolders(folders);
	}

	@Override
	protected void finalize() throws Throwable {
		
		super.finalize();
	}
}