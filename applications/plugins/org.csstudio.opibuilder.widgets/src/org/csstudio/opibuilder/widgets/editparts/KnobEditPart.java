package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.AbstractScaledWidgetModel;
import org.csstudio.opibuilder.widgets.model.KnobModel;
import org.csstudio.sds.components.ui.internal.figures.KnobFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the knob widget. The controller mediates between
 * {@link KnobModel} and {@link KnobFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class KnobEditPart extends AbstractMarkedWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		final KnobModel model = (KnobModel) getModel();

		KnobFigure knob = new KnobFigure();
	/*
		//initializeCommonFigureProperties(knob, model);		
		
		//knob.setBulbColor(model.getKnobColor());
		//knob.setEffect3D(model.isEffect3D());	
		knob.setThumbColor(model.getThumbColor());
		knob.setValueLabelVisibility(model.isShowValueLabel());
		knob.setGradient(model.isRampGradient());
		knob.setIncrement(model.getIncrement());
		*/
		knob.addKnobListener(new KnobFigure.IKnobListener() {
			public void knobValueChanged(final double newValue) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE){
					setPVValue(KnobModel.PROP_CONTROL_PV, newValue);
				}
									
			}
		});		
		
		return knob;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		//knob color
		IWidgetPropertyChangeHandler knobColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				KnobFigure knob = (KnobFigure) refreshableFigure;
				knob.setBulbColor((RGB)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(KnobModel.PROP_KNOB_COLOR, knobColorHandler);	
		
	
		//thumbColor
		IWidgetPropertyChangeHandler thumbColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				KnobFigure knob = (KnobFigure) refreshableFigure;
				knob.setThumbColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(KnobModel.PROP_THUMB_COLOR, thumbColorHandler);		
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				KnobFigure knob = (KnobFigure) refreshableFigure;
				knob.setEffect3D((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(KnobModel.PROP_EFFECT3D, effect3DHandler);
		
		
		//show value label
		IWidgetPropertyChangeHandler valueLabelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				KnobFigure knob = (KnobFigure) refreshableFigure;
				knob.setValueLabelVisibility((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(KnobModel.PROP_SHOW_VALUE_LABEL, valueLabelHandler);
		
		//Ramp gradient
		IWidgetPropertyChangeHandler gradientHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				KnobFigure knob = (KnobFigure) refreshableFigure;
				knob.setGradient((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(KnobModel.PROP_RAMP_GRADIENT, gradientHandler);	
		
		
		
		
		//increment
		IWidgetPropertyChangeHandler incrementHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				KnobFigure knob = (KnobFigure) refreshableFigure;
				knob.setIncrement((Double)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(KnobModel.PROP_INCREMENT, incrementHandler);
		
	}

}
