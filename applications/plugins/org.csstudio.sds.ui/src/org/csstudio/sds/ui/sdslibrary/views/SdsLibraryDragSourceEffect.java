package org.csstudio.sds.ui.sdslibrary.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.csstudio.sds.ui.thumbnail.SdsThumbnailCreator;
import org.csstudio.sds.ui.thumbnail.SdsThumbnailPanel;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.swt.dnd.DragSourceEffect;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class SdsLibraryDragSourceEffect extends DragSourceEffect {

	private final SdsThumbnailPanel sdsThumbnailPanel;
	private final IWorkbenchPage workbenchPage;

	public SdsLibraryDragSourceEffect(SdsThumbnailPanel panel,
			IWorkbenchPage workbenchPage) {
		super(panel);
		this.sdsThumbnailPanel = panel;
		this.workbenchPage = workbenchPage;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		File selectedFile = sdsThumbnailPanel.getSelectedFile();
		if (selectedFile != null) {
			Image img = createDragImage(selectedFile);
			if (img != null) {
				event.image = img;
			}
		}
	}

	private Image createDragImage(File file) {
		// Get DisplayModel for file
		DisplayModel model = new DisplayModel();
		FileInputStream fip = null;
		try {
			fip = new FileInputStream(file);
			PersistenceUtil.syncFillModel(model, fip);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int maxSize = Math.max(model.getWidth(), model.getHeight());
		double zoom = 1.0;
		IEditorPart activeEditor = workbenchPage.getActiveEditor();
		if (activeEditor instanceof DisplayEditor) {
			ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) ((DisplayEditor) activeEditor)
					.getGraphicalViewer().getRootEditPart();
			zoom = rootEditPart.getZoomManager().getZoom();
		}
		SdsThumbnailCreator thumbnailCreator = new SdsThumbnailCreator();
		ImageData imageData = thumbnailCreator.createImageWithZoomingIn(model,
				(int) (maxSize * zoom), Display.getDefault());
		return new Image(Display.getDefault(), imageData);
	}
}
