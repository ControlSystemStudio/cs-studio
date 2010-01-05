package org.csstudio.opibuilder.widgets.editparts;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.AbstractScaledWidgetFigure;
import org.csstudio.opibuilder.widgets.model.AbstractScaledWidgetModel;
import org.csstudio.opibuilder.widgets.model.ScrollBarModel;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.ScrollBar;

/**
 * The controller of scrollbar widget.
 * 
 * @author Xihui Chen
 * 
 */
public class ScrollbarEditPart extends AbstractPVWidgetEditPart {
	
	/**
	 * All double value properties can converted to integer value by multiplying
	 * this number. 
	 */
	private int multiplyFactor = 1;
	
	private boolean innerUpdate;

	@Override
	public ScrollBarModel getWidgetModel() {
		return (ScrollBarModel) super.getWidgetModel();
	}
	
	private void updateMutiplyFactor(IFigure figure, double value){
		int currentPrecision = (int) Math.log10(multiplyFactor);
		String valueString = Double.toString(value);
		int dotPosition = valueString.indexOf(".");
		if(dotPosition < 0)
			return;
		int newPrecision = valueString.length() - dotPosition-1; 
		if(currentPrecision > newPrecision)
			return;
		int oldMultiplyFactor = multiplyFactor;
		multiplyFactor = (int) Math.pow(10, newPrecision);		
		resetScrollbarWithNewMultiplyFactor(figure, oldMultiplyFactor, multiplyFactor);
	}
	
	private void resetScrollbarWithNewMultiplyFactor(IFigure figure, int oldFactor, int newFactor){
		if(figure == null || !(figure instanceof ScrollBar))
			return;
		ScrollBar scrollBar = (ScrollBar) getFigure();
		scrollBar.setMaximum(scrollBar.getMaximum()*newFactor/oldFactor);
		scrollBar.setMinimum(scrollBar.getMinimum()*newFactor/oldFactor);
		scrollBar.setStepIncrement(scrollBar.getStepIncrement()*newFactor/oldFactor);
		scrollBar.setPageIncrement(scrollBar.getPageIncrement()*newFactor/oldFactor);
	}
	
	
	@Override
	protected IFigure doCreateFigure() {
		ScrollBar scrollBar = new ScrollBar();
		ScrollBarModel model = getWidgetModel();
		updateMutiplyFactor(null, model.getMaximum());
		updateMutiplyFactor(null, model.getMinimum());
		updateMutiplyFactor(null, model.getPageIncrement());
		updateMutiplyFactor(null, model.getStepIncrement());
		scrollBar.setMaximum((int) (model.getMaximum() * multiplyFactor));
		scrollBar.setMinimum((int) (model.getMinimum() * multiplyFactor));
		scrollBar.setStepIncrement((int) (model.getStepIncrement() * multiplyFactor));
		scrollBar.setPageIncrement((int) (model.getPageIncrement() * multiplyFactor));		
		scrollBar.setHorizontal(model.isHorizontal());
		scrollBar.setExtent((scrollBar.getMaximum() - scrollBar.getMinimum())/5);
		if (getExecutionMode() == ExecutionMode.RUN_MODE){
			scrollBar.addPropertyChangeListener(RangeModel.PROPERTY_VALUE, 
					new PropertyChangeListener() {			
						public void propertyChange(PropertyChangeEvent evt) {
							if(innerUpdate){
								innerUpdate = false;
								return;
							}
							setPVValue(ScrollBarModel.PROP_CONTROL_PV, 
								(((Integer)evt.getNewValue()).doubleValue())/multiplyFactor);
				}
			});
		}else
			scrollBar.setEnabled(false);
		
		return scrollBar;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// value
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				if(newValue == null)
					return false;
				ScrollBar figure = (ScrollBar) refreshableFigure;
				innerUpdate = true;
				figure.setValue((int) (ValueUtil.getDouble((IValue)newValue) * multiplyFactor));
				innerUpdate = false;
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, valueHandler);
		
		//minimum
		IWidgetPropertyChangeHandler minimumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScrollBar figure = (ScrollBar) refreshableFigure;
				updateMutiplyFactor(refreshableFigure, (Double) newValue);				
				figure.setMinimum((int) (((Double)newValue)*multiplyFactor));
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_MIN, minimumHandler);
		
		//maximum
		IWidgetPropertyChangeHandler maximumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScrollBar figure = (ScrollBar) refreshableFigure;
				updateMutiplyFactor(refreshableFigure, (Double) newValue);				
				figure.setMaximum((int) (((Double)newValue)*multiplyFactor));
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_MAX, maximumHandler);
	
	
		
	}
	
	
	/**
	 * Registers property change handlers for the properties defined in
	 * {@link AbstractScaledWidgetModel}. This method is provided for the convenience
	 * of subclasses, which can call this method in their implementation of
	 * {@link #registerPropertyChangeHandlers()}.
	 */
	protected void registerCommonPropertyChangeHandlers() {
		
		
		//minimum
		IWidgetPropertyChangeHandler minimumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setRange((Double) newValue, ((AbstractScaledWidgetModel)getModel()).getMaximum());
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_MIN, minimumHandler);
		
		//maximum
		IWidgetPropertyChangeHandler maximumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setRange(((AbstractScaledWidgetModel)getModel()).getMinimum(), (Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_MAX, maximumHandler);
		
		//major tick step hint
		IWidgetPropertyChangeHandler majorTickHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setMajorTickMarkStepHint((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_MAJOR_TICK_STEP_HINT, majorTickHandler);
	
		
		
		//logScale
		IWidgetPropertyChangeHandler logScaleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setLogScale((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_LOG_SCALE, logScaleHandler);
		
		//showScale
		IWidgetPropertyChangeHandler showScaleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setShowScale((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_SHOW_SCALE, showScaleHandler);
		
	
		//showMinorTicks
		IWidgetPropertyChangeHandler showMinorTicksHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setShowMinorTicks((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_SHOW_MINOR_TICKS, showMinorTicksHandler);
		
		//Transparent
		IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_TRANSPARENT, transparentHandler);
		
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof Double)
			((AbstractScaledWidgetFigure)getFigure()).setValue((Double)value);
	}
	
	@Override
	public Double getValue() {
		return ((AbstractScaledWidgetFigure)getFigure()).getValue();
	}

	
	
}
