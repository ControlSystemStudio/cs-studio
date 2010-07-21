package org.csstudio.opibuilder.widgets.editparts;


import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.model.ImageBoolButtonModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.swt.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figures.ImageBoolButtonFigure;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * EditPart controller for the image widget.
 * 
 * @author Xihui Chen
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
		figure.addManualValueChangeListener(new IManualValueChangeListener() {
			
			public void manualValueChanged(double newValue) {
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
		
		//removeAllPropertyChangeHandlers(AbstractPVWidgetModel.PROP_PVVALUE);
		// value
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				if(newValue == null)
					return false;
				ImageBoolButtonFigure figure = (ImageBoolButtonFigure) refreshableFigure;
				//figure.setValue(ValueUtil.getDouble((IValue)newValue));
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
				IPath absolutePath = (IPath)newValue;
				if(!absolutePath.isAbsolute())
					absolutePath = ResourceUtil.buildAbsolutePath(
							getWidgetModel(), absolutePath);				
				imageFigure.setOnImagePath(absolutePath);
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
				IPath absolutePath = (IPath)newValue;
				if(!absolutePath.isAbsolute())
					absolutePath = ResourceUtil.buildAbsolutePath(
							getWidgetModel(), absolutePath);				
				imageFigure.setOffImagePath(absolutePath);
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
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handle);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handle);
		
		//size change handlers - so we can stretch accordingly
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
				autoSizeWidget(imageFigure);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT, handle);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH, handle);
		

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
