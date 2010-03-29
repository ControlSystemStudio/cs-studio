package org.csstudio.opibuilder.widgets.editparts;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
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
		scrollBar.setExtent(scrollBar.getExtent()*newFactor/oldFactor);
	}
	
	
	@Override
	protected IFigure doCreateFigure() {
		ScrollBar scrollBar = new ScrollBar(){ 
			//synchronize this method to avoid the race condition which
			//could be caused from manual operation and inner update from new PV value.
			@Override
			public synchronized void setValue(int v) {
				super.setValue(v);
			}
		};
		ScrollBarModel model = getWidgetModel();
		updateMutiplyFactor(null, model.getMaximum());
		updateMutiplyFactor(null, model.getMinimum());
		updateMutiplyFactor(null, model.getPageIncrement());
		updateMutiplyFactor(null, model.getStepIncrement());
		updateMutiplyFactor(null, model.getBarLength());
		
		scrollBar.setMaximum((int) ((model.getMaximum() + model.getBarLength()) * multiplyFactor));
		scrollBar.setMinimum((int) (model.getMinimum() * multiplyFactor));
		scrollBar.setStepIncrement((int) (model.getStepIncrement() * multiplyFactor));
		scrollBar.setPageIncrement((int) (model.getPageIncrement() * multiplyFactor));		
		scrollBar.setExtent((int) (model.getBarLength() * multiplyFactor));
		
		scrollBar.setHorizontal(model.isHorizontal());

		if (getExecutionMode() == ExecutionMode.RUN_MODE){
			scrollBar.addPropertyChangeListener(RangeModel.PROPERTY_VALUE, 
					new PropertyChangeListener() {			
						public void propertyChange(PropertyChangeEvent evt) {
							if(innerUpdate){
								innerUpdate = false;
								return;
							}
							setPVValue(ScrollBarModel.PROP_PVNAME, 
								(((Integer)evt.getNewValue()).doubleValue())/multiplyFactor);
				}
			});
		}
		
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME);
		return scrollBar;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		((ScrollBar)getFigure()).setEnabled(getWidgetModel().isEnabled() && 
				(getExecutionMode() == ExecutionMode.RUN_MODE));		
		
		removeAllPropertyChangeHandlers(AbstractWidgetModel.PROP_ENABLED);
		
		//enable
		IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if(getExecutionMode() == ExecutionMode.RUN_MODE)
					figure.setEnabled((Boolean)newValue);
				return false;
			}
		};		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, enableHandler);
		
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
				figure.setEnabled(getWidgetModel().isEnabled() && 
						getExecutionMode() == ExecutionMode.RUN_MODE && figure.isEnabled());
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
				figure.setMaximum((int) (
						((Double)newValue + getWidgetModel().getBarLength())*multiplyFactor));
				figure.setEnabled(getWidgetModel().isEnabled() && 
						getExecutionMode() == ExecutionMode.RUN_MODE && figure.isEnabled());
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_MAX, maximumHandler);
	

		//page increment
		IWidgetPropertyChangeHandler pageIncrementHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScrollBar figure = (ScrollBar) refreshableFigure;
				updateMutiplyFactor(refreshableFigure, (Double) newValue);				
				figure.setPageIncrement((int) (((Double)newValue)*multiplyFactor));
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_PAGE_INCREMENT, pageIncrementHandler);
		
		//step increment
		IWidgetPropertyChangeHandler stepIncrementHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScrollBar figure = (ScrollBar) refreshableFigure;
				updateMutiplyFactor(refreshableFigure, (Double) newValue);				
				figure.setStepIncrement((int) (((Double)newValue)*multiplyFactor));
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_STEP_INCREMENT, stepIncrementHandler);
		
		//bar length
		IWidgetPropertyChangeHandler barLengthHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScrollBar figure = (ScrollBar) refreshableFigure;
				updateMutiplyFactor(refreshableFigure, (Double) newValue);				
				figure.setExtent((int) (((Double)newValue)*multiplyFactor));
				figure.setMaximum((int)
						((getWidgetModel().getMaximum() + (Double)newValue)*multiplyFactor));
				figure.setEnabled(getWidgetModel().isEnabled() && 
						getExecutionMode() == ExecutionMode.RUN_MODE && figure.isEnabled());
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_BAR_LENGTH, barLengthHandler);
		
		
		//horizontal
		IWidgetPropertyChangeHandler horizontalHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ScrollBar figure = (ScrollBar) refreshableFigure;
				figure.setHorizontal((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_HORIZONTAL, horizontalHandler);
		
		
		
	}
	

	@Override
	public void setValue(Object value) {
		if(value instanceof Double)
			((ScrollBar)getFigure()).setValue((Integer)value);
	}
	
	@Override
	public Integer getValue() {
		return ((ScrollBar)getFigure()).getValue();
	}

	
	
}
