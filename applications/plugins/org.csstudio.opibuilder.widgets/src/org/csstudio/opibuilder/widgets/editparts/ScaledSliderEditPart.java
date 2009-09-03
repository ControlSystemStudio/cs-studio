package org.csstudio.opibuilder.widgets.editparts;


import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.figures.ScaledSliderFigure;
import org.csstudio.opibuilder.widgets.model.ScaledSliderModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the scaled slider widget. The controller mediates between
 * {@link ScaledSliderModel} and {@link ScaledSliderFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class ScaledSliderEditPart extends AbstractMarkedWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final ScaledSliderModel model = getCastedModel();

		ScaledSliderFigure slider = new ScaledSliderFigure();
		
		initializeCommonFigureProperties(slider, model);		
		slider.setFillColor(model.getFillColor());
		slider.setEffect3D(model.isEffect3D());	
		slider.setFillBackgroundColor(model.getFillbackgroundColor());
		slider.setThumbColor(model.getThumbColor());
		slider.setHorizontal(model.isHorizontal());
		slider.setIncrement(model.getIncrement());
		slider.addSliderListener(new ScaledSliderFigure.IScaledSliderListener() {
			public void sliderValueChanged(final double newValue) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE)
					setPVValue(ScaledSliderModel.PROP_CONTROL_PV, newValue);				
				
			}
		});	
		
		return slider;

	}

	@Override
	public ScaledSliderModel getCastedModel() {
		return (ScaledSliderModel)getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		//fillColor
		IWidgetPropertyChangeHandler fillColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
				slider.setFillColor(((OPIColor) newValue).getRGBValue());
				return true;
			}
		};
		setPropertyChangeHandler(ScaledSliderModel.PROP_FILL_COLOR, fillColorHandler);	
		
		//fillBackgroundColor
		IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
				slider.setFillBackgroundColor(((OPIColor) newValue).getRGBValue());
				return true;
			}
		};
		setPropertyChangeHandler(ScaledSliderModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);	
		
		//thumbColor
		IWidgetPropertyChangeHandler thumbColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
				slider.setThumbColor(((OPIColor) newValue).getRGBValue());
				return true;
			}
		};
		setPropertyChangeHandler(ScaledSliderModel.PROP_THUMB_COLOR, thumbColorHandler);		
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
				slider.setEffect3D((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ScaledSliderModel.PROP_EFFECT3D, effect3DHandler);	
		
		
		//horizontal
		IWidgetPropertyChangeHandler horizontalHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
				slider.setHorizontal((Boolean) newValue);
				ScaledSliderModel model = (ScaledSliderModel)getModel();
				
				if((Boolean) newValue) //from vertical to horizontal
					model.setLocation(model.getLocation().x - model.getSize().height/2 + model.getSize().width/2,
						model.getLocation().y + model.getSize().height/2 - model.getSize().width/2);
				else  //from horizontal to vertical
					model.setLocation(model.getLocation().x + model.getSize().width/2 - model.getSize().height/2,
						model.getLocation().y - model.getSize().width/2 + model.getSize().height/2);					
				
				model.setSize(model.getSize().height, model.getSize().width);
				
				return true;
			}
		};
		setPropertyChangeHandler(ScaledSliderModel.PROP_HORIZONTAL, horizontalHandler);	
		
		
		//enabled. WidgetBaseEditPart will force the widget as disabled in edit model,
		//which is not the case for the scaled slider		
		IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;				
				slider.setEnabled((Boolean) newValue);				
				return true;
			}
		};
		setPropertyChangeHandler(ScaledSliderModel.PROP_ENABLED, enableHandler);	
		
		
		IWidgetPropertyChangeHandler incrementHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
				slider.setIncrement((Double)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ScaledSliderModel.PROP_INCREMENT, incrementHandler);
		
	}

}
