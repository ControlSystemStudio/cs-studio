package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.CheckBoxModel;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figures.CheckBoxFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Display;

/**The editpart of a checkbox.
 * @author Xihui Chen
 *
 */
public class CheckBoxEditPart extends AbstractPVWidgetEditPart {

	@Override
	protected IFigure doCreateFigure() {
		CheckBoxFigure figure = new CheckBoxFigure();
		figure.setBit(getWidgetModel().getBit());
		figure.setText(getWidgetModel().getLabel());
		figure.addManualValueChangeListener(new IManualValueChangeListener() {
			
			public void manualValueChanged(double newValue) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE)
					setPVValue(AbstractPVWidgetModel.PROP_PVNAME, newValue);				
			}
		});
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		figure.setRunMode(getExecutionMode().equals(
				ExecutionMode.RUN_MODE));
		
		return figure;
	}
	
	@Override
	public CheckBoxModel getWidgetModel() {
		return  (CheckBoxModel)getModel();
	}

	@Override
	protected void registerPropertyChangeHandlers() {

		// value
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				if(newValue == null)
					return false;
				CheckBoxFigure figure = (CheckBoxFigure) refreshableFigure;
				figure.setValue(ValueUtil.getDouble((IValue)newValue));
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);
		
		// bit
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				CheckBoxFigure figure = (CheckBoxFigure) refreshableFigure;
				figure.setBit((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(CheckBoxModel.PROP_BIT, handler);
		
		//label
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				CheckBoxFigure figure = (CheckBoxFigure) refreshableFigure;
				figure.setText((String) newValue);
				Display.getCurrent().timerExec(10, new Runnable() {					
					public void run() {
						if(getWidgetModel().isAutoSize())
							performAutoSize(refreshableFigure);
					}
				});
				return true;
			}
		};
		setPropertyChangeHandler(CheckBoxModel.PROP_LABEL, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {				
				if((Boolean)newValue){
					performAutoSize(figure);
					figure.revalidate();
				}
				return true;
			}
		};
		setPropertyChangeHandler(CheckBoxModel.PROP_AUTOSIZE, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				Display.getCurrent().timerExec(10, new Runnable() {					
					public void run() {
						if(getWidgetModel().isAutoSize()){
							performAutoSize(figure);
							figure.revalidate();
						}
					}
				});
				
				return true;
			}
		};
		setPropertyChangeHandler(CheckBoxModel.PROP_FONT, handler);		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handler);
	}
	
	/**
	 * @param figure
	 */
	private void performAutoSize(IFigure figure) {
		getWidgetModel().setSize(((CheckBoxFigure)figure).getPreferredSize());
	}

	
	@Override
	public void setValue(Object value) {
		if(value instanceof Double)
			((CheckBoxFigure)getFigure()).setValue((Double)value);
		else if (value instanceof Boolean)
			((CheckBoxFigure)getFigure()).setBoolValue((Boolean)value);
	}

	@Override
	public Boolean getValue() {
		return ((CheckBoxFigure)getFigure()).getBoolValue();
	}
	
}
