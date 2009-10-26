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


import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.ImageBoolButtonFigure;
import org.csstudio.opibuilder.widgets.figures.AbstractBoolControlFigure.IBoolControlListener;
import org.csstudio.opibuilder.widgets.model.ImageBoolButtonModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * EditPart controller for the image widget.
 * 
 * @author jbercic, Xihui Chen
 * 
 */
public final class ImageBoolButtonEditPart extends AbstractBoolControlEditPart {
	

	/**
	 * Returns the casted model. This is just for convenience.
	 * 
	 * @return the casted {@link ImageModel}
	 */
	public ImageBoolButtonModel getWidgetModel() {
		return (ImageBoolButtonModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ImageBoolButtonModel model = getWidgetModel();
		// create AND initialize the view properly
		final ImageBoolButtonFigure figure = new ImageBoolButtonFigure();	
		initializeCommonFigureProperties(figure, model);			
		figure.addBoolControlListener(new IBoolControlListener() {
			public void valueChanged(final double newValue) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE)
					autoSizeWidget(figure);
			}
		});		
		figure.setOnImagePath(model.getOnImagePath());
		figure.setOffImagePath(model.getOffImagePath());
		figure.setStretch(model.isStretch());
		return figure;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		removeAllPropertyChangeHandlers(AbstractPVWidgetModel.PROP_PVVALUE);
		// value
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				if(newValue == null)
					return false;
				ImageBoolButtonFigure figure = (ImageBoolButtonFigure) refreshableFigure;
				figure.setValue(ValueUtil.getDouble((IValue)newValue));
				autoSizeWidget(figure);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);
		
		
		
		// changes to the on image property
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
				imageFigure.setOnImagePath((IPath)newValue);
				autoSizeWidget(imageFigure);
				return true;
			}

			
		};
		setPropertyChangeHandler(ImageBoolButtonModel.PROP_ON_IMAGE, handle);
		
		// changes to the off image property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
				imageFigure.setOffImagePath((IPath)newValue);
				autoSizeWidget(imageFigure);
				return true;
			}

			
		};
		setPropertyChangeHandler(ImageBoolButtonModel.PROP_OFF_IMAGE, handle);
		
		// changes to the stretch property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
				imageFigure.setStretch((Boolean)newValue);
				autoSizeWidget(imageFigure);
				return true;
			}
		};
		setPropertyChangeHandler(ImageBoolButtonModel.PROP_STRETCH, handle);
	
		// changes to the autosize property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
				autoSizeWidget(imageFigure);
				return true;
			}
		};
		setPropertyChangeHandler(ImageBoolButtonModel.PROP_AUTOSIZE, handle);
		
		
	
		
		// changes to the border width property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
				autoSizeWidget(imageFigure);
				return true;
			}
		};
		setPropertyChangeHandler(ImageBoolButtonModel.PROP_BORDER_WIDTH, handle);
		setPropertyChangeHandler(ImageBoolButtonModel.PROP_BORDER_STYLE, handle);
		
		//size change handlers - so we can stretch accordingly
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
				autoSizeWidget(imageFigure);
				return true;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_HEIGHT, handle);
		setPropertyChangeHandler(ImageModel.PROP_WIDTH, handle);
		

	}
	
	
	
	@Override
	public void deactivate() {
		super.deactivate();
		((ImageBoolButtonFigure) getFigure()).dispose();
	}
	
	private void autoSizeWidget(ImageBoolButtonFigure imageFigure) {
		ImageBoolButtonModel model = getWidgetModel();
		Dimension d = imageFigure.getAutoSizedDimension();
		if(model.isAutoSize() && !model.isStretch() && d != null) 
			model.setSize(d.width, d.height);
	}
	
}
