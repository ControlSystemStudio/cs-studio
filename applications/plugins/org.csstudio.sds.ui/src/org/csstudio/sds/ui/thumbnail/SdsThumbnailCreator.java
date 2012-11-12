package org.csstudio.sds.ui.thumbnail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.LinkingContainerModel;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.internal.editparts.DisplayEditPart;
import org.csstudio.sds.ui.internal.editparts.WidgetEditPartFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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

public class SdsThumbnailCreator {

	public ImageData createImage(DisplayModel displayModel,
			final int thumbSize, final Display display) {
		ThumbnailCreationJob job = new ThumbnailCreationJob(display,
				displayModel, thumbSize, false);
		return job.getImageData();
	}

	public ImageData createImageWithZoomingIn(DisplayModel displayModel,
			final int thumbSize, final Display display) {
		ThumbnailCreationJob job = new ThumbnailCreationJob(display,
				displayModel, thumbSize, true);
		return job.getImageData();
	}
	
	private static class ThumbnailCreationJob implements Runnable {

		private final Display display;
		private final DisplayModel displayModel;
		private final int thumbSize;
		private final boolean isZoomInEnabled;

		private ImageData imageData;

		public ThumbnailCreationJob(Display display, DisplayModel displayModel,
				int thumbSize, boolean isZoomInEnabled) {
			this.display = display;
			this.thumbSize = thumbSize;

			this.displayModel = displayModel;
			this.isZoomInEnabled = isZoomInEnabled;

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
				
				loadLinkingContainersRecursive(displayModel, 10);
				
				Shell shell = new Shell(display, SWT.NO_TRIM);
				Image image = null;
				try {
					shell.setLayout(new FillLayout());
					shell.setSize(displayModel.getWidth(),
							displayModel.getHeight());

					ThumbnailDrawingViewer viewer = new ThumbnailDrawingViewer();
					viewer.createControl(shell);
					viewer.setEditPartFactory(new NoBorderWidgetEditPartFactory(
							ExecutionMode.EDIT_MODE));
					viewer.setRootEditPart(new ScalableFreeformRootEditPart());
					viewer.setContents(displayModel);

					shell.layout();

					image = viewer.createImage(thumbSize, isZoomInEnabled);
					imageData = image.getImageData();
				} finally {
					if (image != null) {
						image.dispose();
					}
					shell.dispose();
				}
			}
		}

		private void loadLinkingContainersRecursive(ContainerModel model,
				int recursionDepth) {
			List<AbstractWidgetModel> childWidgets = model.getWidgets();
			for (AbstractWidgetModel childModel : childWidgets) {
				int childRecursionDepth = recursionDepth;
				if (childModel instanceof LinkingContainerModel) {
					LinkingContainerModel linkingContainerChildModel = (LinkingContainerModel) childModel;
					if (childRecursionDepth > 0) {
						loadLinkingContainer(linkingContainerChildModel);
						childRecursionDepth -= 1;
					} else {
						mockFillLinkingContainer(linkingContainerChildModel);
					}
				}
		
				if (childModel instanceof ContainerModel) {
					loadLinkingContainersRecursive((ContainerModel) childModel, childRecursionDepth);
				}
			}
		}

		private void mockFillLinkingContainer(LinkingContainerModel modelToBeFilled) {
			modelToBeFilled.setResourceLoaded(true);
			modelToBeFilled.setColor(
					AbstractWidgetModel.PROP_COLOR_BACKGROUND,
					"#FF0000");
			modelToBeFilled.setColor(
					AbstractWidgetModel.PROP_COLOR_FOREGROUND,
					"#000000");
		}

		private void loadLinkingContainer(LinkingContainerModel linkingContainerModel) {
			
			// Load linked resource
			DisplayModel loadedModel = new DisplayModel();
			InputStream inputStream = getInputStream(linkingContainerModel.getResource());
			if (inputStream != null) {
				PersistenceUtil.syncFillModel(loadedModel, inputStream);
			}
			List<AbstractWidgetModel> loadedWidgets = loadedModel.getWidgets();
			loadedModel.removeWidgets(loadedWidgets);
			
			linkingContainerModel.addWidgets(loadedWidgets);
			
			linkingContainerModel.setColor(
					AbstractWidgetModel.PROP_COLOR_BACKGROUND,
					loadedModel.getColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
			linkingContainerModel.setColor(
					AbstractWidgetModel.PROP_COLOR_FOREGROUND,
					loadedModel.getColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
			linkingContainerModel.setResourceLoaded(true);
		}


		/**
		 * Return the {@link InputStream} of the file that is available on the
		 * specified path.
		 * 
		 * @param path
		 *            The {@link IPath} to the file
		 * 
		 * @return The corresponding {@link InputStream} or null
		 */
		private InputStream getInputStream(final IPath path) {
			InputStream result = null;
		
			// try workspace
			IResource r = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(path, false);
			if (r instanceof IFile) {
				try {
					result = ((IFile) r).getContents();
				} catch (CoreException e) {
					result = null;
				}
			}
		
			if (result == null) {
				// try from local file system
				try {
					result = new FileInputStream(path.toFile());
				} catch (FileNotFoundException e) {
					result = null;
				}
		
			}
		
			return result;
		}

	}

	private static class ThumbnailDrawingViewer extends GraphicalViewerImpl {

		public Image createImage(int thumbSize, boolean isZoomInEnabled) {

			IFigure figure = getLightweightSystem().getRootFigure();

			Rectangle bounds = figure.getBounds();

			double widthFactor = (double) thumbSize / (double) bounds.width;
			double heightFactor = (double) thumbSize / (double) bounds.height;
			double factor = Math.min(widthFactor, heightFactor);

			Image image = new Image(Display.getCurrent(),
					(int) Math.ceil((double) bounds.width * factor),
					(int) Math.ceil((double) bounds.height * factor));
			GC gc = new GC(image);
			Graphics graphics = new SWTGraphics(gc);

			// Special handling if max zoom level == 1
			if(!isZoomInEnabled && factor > 1) {
				factor = 1;
				// center figure in image
				figure.translate((image.getBounds().width - bounds.width) / 2, (image.getBounds().height - bounds.height) / 2);
			}
			
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
