package org.csstudio.sds.ui.internal.editor;

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
import org.eclipse.swt.widgets.Shell;

public class ThumbnailCreator {

	public static ImageData createImage(final DisplayModel model,
			final int thumbSize) {

		final ImageDataContainer resultContainer = new ImageDataContainer();

		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = new Shell(display, SWT.NO_TRIM);
				Image image = null;
				try {
					shell.setLayout(new FillLayout());
					shell.setSize(model.getWidth(), model.getHeight());

					ThumbnailDrawingViewer viewer = new ThumbnailDrawingViewer();
					viewer.createControl(shell);
					viewer.setEditPartFactory(new NoBorderWidgetEditPartFactory(
							ExecutionMode.EDIT_MODE));
					viewer.setRootEditPart(new ScalableFreeformRootEditPart());
					viewer.setContents(model);

					shell.layout();

					image = viewer.getImage(thumbSize);
					ImageData result = image.getImageData();
					resultContainer.imageData = result;

				} finally {
					if (image != null) {
						image.dispose();
					}
					shell.dispose();
				}
			}
		});

		return resultContainer.imageData;
	}

	private static class ImageDataContainer {
		public ImageData imageData;
	}

	private static class ThumbnailDrawingViewer extends GraphicalViewerImpl {

		public Image getImage(int thumbSize) {

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
	
	private static class NoBorderWidgetEditPartFactory extends WidgetEditPartFactory {

		public NoBorderWidgetEditPartFactory(ExecutionMode executionMode) {
			super(executionMode);
		}
		
		@Override
		public EditPart createEditPart(EditPart context, Object modelElement) {
			EditPart result = super.createEditPart(context, modelElement); 
			
			if(modelElement instanceof DisplayModel) {
				((DisplayEditPart)result).setExecutionMode(ExecutionMode.RUN_MODE);
			}
			return result;
		}
		
	}
}
