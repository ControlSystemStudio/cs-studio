package org.csstudio.sds.ui.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.internal.editparts.DisplayEditPart;
import org.csstudio.sds.ui.internal.editparts.WidgetEditPartFactory;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ThumbnailCreator {

	private static ThumbnailImageCache imageCache = new ThumbnailImageCache();

	public ThumbnailCreator() {
		
		Display.getCurrent().addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				imageCache.shutdown();
			}
		});
		
	}

	public ImageData createImage(final File file, final int thumbSize,
			final Display display) {

		ImageData imageData = imageCache.getCachedImage(file);
		if (imageData == null) {
			
			ThumbnailCreationJob job = new ThumbnailCreationJob(display, file, thumbSize);
			imageData = job.getImageData();
			if (imageData != null) {
				imageCache.cacheImage(file, imageData);
			}
		}
		return imageData;
	}
	
	private static class ThumbnailCreationJob implements Runnable {

		private final Display display;
		private final DisplayModel model;
		private final int thumbSize;

		private ImageData imageData;

		public ThumbnailCreationJob(Display display, File file, int thumbSize) {
			this.display = display;
			this.thumbSize = thumbSize;
			
			model = new DisplayModel();
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
			
			if (!display.isDisposed()) {
				display.syncExec(this);
			}
		}
		
		public ImageData getImageData() {
			return imageData;
		}
		
		@Override
		public void run() {
			if (!display.isDisposed()) {
				Shell shell = new Shell(display, SWT.NO_TRIM);
				Image image = null;
				try {
					shell.setLayout(new FillLayout());
					shell.setSize(model.getWidth(),
							model.getHeight());

					ThumbnailDrawingViewer viewer = new ThumbnailDrawingViewer();
					viewer.createControl(shell);
					viewer.setEditPartFactory(new NoBorderWidgetEditPartFactory(
							ExecutionMode.EDIT_MODE));
					viewer.setRootEditPart(new ScalableFreeformRootEditPart());
					viewer.setContents(model);

					shell.layout();

					image = viewer.createImage(thumbSize);
					imageData = image.getImageData();
					
				} finally {
					if (image != null) {
						image.dispose();
					}
					shell.dispose();
				}
			}
		}
		
	}
	
	private static class ThumbnailDrawingViewer extends GraphicalViewerImpl {

		public Image createImage(int thumbSize) {

			IFigure figure = getLightweightSystem().getRootFigure();

			Rectangle bounds = figure.getBounds();

			double factor = Math.min(
					(double) thumbSize / (double) bounds.width,
					(double) thumbSize / (double) bounds.height);

			Image image = new Image(Display.getCurrent(),
					(int) Math.ceil((double) bounds.width * factor),
					(int) Math.ceil((double) bounds.height * factor));
			GC gc = new GC(image);
			Graphics graphics = new SWTGraphics(gc);

			// TODO Antialiasing an GC oder Graphics konfigurieren

			graphics.scale(factor);

			figure.paint(graphics);

			gc.dispose(); // TODO disposing in finally-Block

			return image;
		}

	}

	private static class NoBorderWidgetEditPartFactory extends
			WidgetEditPartFactory {

		public NoBorderWidgetEditPartFactory(ExecutionMode executionMode) {
			super(executionMode);
		}

		@Override
		public EditPart createEditPart(EditPart context, Object modelElement) {
			EditPart result = super.createEditPart(context, modelElement);

			if (modelElement instanceof DisplayModel) {
				((DisplayEditPart) result)
						.setExecutionMode(ExecutionMode.RUN_MODE);
			}
			return result;
		}

	}
}
