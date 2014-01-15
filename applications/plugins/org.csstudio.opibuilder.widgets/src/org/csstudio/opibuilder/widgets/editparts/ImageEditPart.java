/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.opibuilder.widgets.editparts;


import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.swt.widgets.figures.ImageFigure;
import org.csstudio.swt.widgets.util.IImageLoadedListener;
import org.csstudio.swt.widgets.util.PermutationMatrix;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.widgets.Display;

/**
 * EditPart controller for the image widget.
 * 
 * @author jbercic, Xihui Chen
 * 
 */
public final class ImageEditPart extends AbstractWidgetEditPart {

	

	private int maxAttempts;

	/**
	 * Returns the casted model. This is just for convenience.
	 * 
	 * @return the casted {@link ImageModel}
	 */
	public ImageModel getWidgetModel() {
		return (ImageModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ImageModel model = getWidgetModel();
		// create AND initialize the view properly
		final ImageFigure figure = new ImageFigure();		

		// Resize when new image is loaded
		figure.setImageLoadedListener(new IImageLoadedListener() {

			@Override
			public void imageLoaded(IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				autoSizeWidget(imageFigure);
			}
		});

		figure.setFilePath(model.getFilename());		
		figure.setStretch(model.getStretch());
		figure.setAutoSize(model.isAutoSize());
		figure.setAnimationDisabled(model.isStopAnimation());
		figure.setTopCrop(model.getTopCrop());
		figure.setBottomCrop(model.getBottomCrop());
		figure.setLeftCrop(model.getLeftCrop());
		figure.setRightCrop(model.getRightCrop());
		figure.setPermutationMatrix(model.getPermutationMatrix());
		return figure;
	}
	
	@Override
	public void activate() {
		super.activate();
		if(((ImageModel)getModel()).isVisible() && !((ImageModel)getModel()).isStopAnimation())
			((ImageFigure) getFigure()).startAnimation();
	}
	/**
	 * Register change handlers for the four crop properties.
	 */
	protected void registerCropPropertyHandlers() {
		// top
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.setTopCrop((Integer)newValue);				
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_TOPCROP, handle);
		
		// bottom
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.setBottomCrop((Integer)newValue);			
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_BOTTOMCROP, handle);
		
		// left
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.setLeftCrop((Integer)newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_LEFTCROP, handle);
		
		// right
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.setRightCrop((Integer)newValue);	
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_RIGHTCROP, handle);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// changes to the filename property
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				IPath absolutePath = (IPath)newValue;
				if(!absolutePath.isAbsolute())
					absolutePath = ResourceUtil.buildAbsolutePath(
							getWidgetModel(), absolutePath);
				imageFigure.setFilePath(absolutePath);
				autoSizeWidget(imageFigure);
				return false;
			}

			
		};
		setPropertyChangeHandler(ImageModel.PROP_IMAGE_FILE, handle);
		
		// changes to the stretch property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.setStretch((Boolean)newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_STRETCH, handle);
	
		// changes to the autosize property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.setAutoSize((Boolean)newValue);
				ImageModel model = (ImageModel)getModel();
				Dimension d = imageFigure.getAutoSizedDimension();
				if((Boolean) newValue && !model.getStretch() && d != null) 
					model.setSize(d.width, d.height);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_AUTOSIZE, handle);
		
		
		// changes to the stop animation property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.setAnimationDisabled((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_NO_ANIMATION, handle);
		
		// changes to the border width property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.resizeImage();
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_BORDER_WIDTH, handle);
		setPropertyChangeHandler(ImageModel.PROP_BORDER_STYLE, handle);
		
		//size change handlers - so we can stretch accordingly
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				imageFigure.resizeImage();
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_HEIGHT, handle);
		setPropertyChangeHandler(ImageModel.PROP_WIDTH, handle);
		
		registerCropPropertyHandlers();
		registerImageRotationPropertyHandlers();
	}
	
	
	
	@Override
	public void deactivate() {
		super.deactivate();		
		((ImageFigure) getFigure()).dispose();
	}
	
	private void autoSizeWidget(final ImageFigure imageFigure) {
		if(!getWidgetModel().isAutoSize())
			return;
		maxAttempts = 10;
		Runnable task = new Runnable() {			
			public void run() {
				if(maxAttempts-- > 0 && imageFigure.isLoadingImage()){
					Display.getDefault().timerExec(100, this);
					return;
				}
				ImageModel model = (ImageModel)getModel();
				imageFigure.setAutoSize(model.isAutoSize());
				Dimension d = imageFigure.getAutoSizedDimension();
				if(model.isAutoSize() && !model.getStretch() && d != null) 
					model.setSize(d.width, d.height);
				
			}
		};
		Display.getDefault().timerExec(100, task);
		
	}
	
	/**
	 * Registers image rotation property change handlers for the properties
	 * defined in {@link MonitorBoolSymbolModel}.
	 */
	public void registerImageRotationPropertyHandlers() {
		// degree rotation property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				int newDegree = getWidgetModel().getDegree((Integer) newValue);
				int oldDegree = getWidgetModel().getDegree((Integer) oldValue);

				PermutationMatrix oldMatrix = new PermutationMatrix((double[][]) getPropertyValue(ImageModel.PERMUTATION_MATRIX));
				PermutationMatrix newMatrix = PermutationMatrix.generateRotationMatrix(newDegree - oldDegree);
				PermutationMatrix result = newMatrix.multiply(oldMatrix);
				
				// As we use only % Pi/2 angles, we can round to integer values
				// => equals work better
				result.roundToIntegers();

				setPropertyValue(ImageModel.PERMUTATION_MATRIX, result.getMatrix());
				setPropertyValue(ImageModel.PROP_DEGREE, (Integer) newValue);
				imageFigure.setPermutationMatrix(result);
				autoSizeWidget(imageFigure);
					
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_DEGREE, handler);

		// flip horizontal rotation property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				// imageFigure.setFlipH((Boolean) newValue);
				PermutationMatrix newMatrix = PermutationMatrix.generateFlipHMatrix();
				PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
				PermutationMatrix result = newMatrix.multiply(oldMatrix);

				// As we use only % Pi/2 angles, we can round to integer values
				// => equals work better
				result.roundToIntegers();
				
				setPropertyValue(ImageModel.PERMUTATION_MATRIX, result.getMatrix());
				setPropertyValue(ImageModel.PROP_FLIP_HORIZONTAL, (Boolean) newValue);
				imageFigure.setPermutationMatrix(result);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_FLIP_HORIZONTAL, handler);

		// flip vertical rotation property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ImageFigure imageFigure = (ImageFigure) figure;
				// imageFigure.setFlipV((Boolean) newValue);
				PermutationMatrix newMatrix = PermutationMatrix.generateFlipVMatrix();
				PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
				PermutationMatrix result = newMatrix.multiply(oldMatrix);
				
				// As we use only % Pi/2 angles, we can round to integer values
				// => equals work better
				result.roundToIntegers();

				setPropertyValue(ImageModel.PERMUTATION_MATRIX, result.getMatrix());
				setPropertyValue(ImageModel.PROP_FLIP_VERTICAL, (Boolean) newValue);
				imageFigure.setPermutationMatrix(result);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_FLIP_VERTICAL, handler);
	}
}