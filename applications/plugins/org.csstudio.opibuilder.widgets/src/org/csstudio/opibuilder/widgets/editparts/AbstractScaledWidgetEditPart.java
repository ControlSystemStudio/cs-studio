/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.AbstractScaledWidgetModel;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.swt.widgets.figures.AbstractScaledWidgetFigure;
import org.csstudio.swt.xygraph.linearscale.AbstractScale;
import org.eclipse.draw2d.IFigure;

/**
 * Base editPart controller for a widget based on {@link AbstractScaledWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public abstract class AbstractScaledWidgetEditPart extends AbstractPVWidgetEditPart {

	/**
	 * Sets those properties on the figure that are defined in the
	 * {@link AbstractScaledWidgetFigure} base class. This method is provided for the
	 * convenience of subclasses, which can call this method in their
	 * implementation of {@link AbstractBaseEditPart#doCreateFigure()}.
	 *
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	protected void initializeCommonFigureProperties(
			final AbstractScaledWidgetFigure figure, final AbstractScaledWidgetModel model) {

		figure.setRange(model.getMinimum(), model.getMaximum());
		figure.setValue((model.getMinimum() + model.getMaximum())/2);
		figure.setMajorTickMarkStepHint(model.getMajorTickStepHint());
		figure.setLogScale(model.isLogScaleEnabled());
		figure.setShowScale(model.isShowScale());
		figure.setShowMinorTicks(model.isShowMinorTicks());
		figure.setTransparent(model.isTransparent());
		figure.getScale().setFont(model.getScaleFont().getSWTFont());
		setScaleFormat(figure, model.getScaleFormat());
		setValueLabelFormat(figure, model.getValueLabelFormat());
	}

	/**
	 * Registers property change handlers for the properties defined in
	 * {@link AbstractScaledWidgetModel}. This method is provided for the convenience
	 * of subclasses, which can call this method in their implementation of
	 * {@link #registerPropertyChangeHandlers()}.
	 */
	protected void registerCommonPropertyChangeHandlers() {
		// value
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				if(newValue == null)
					return false;
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setValue(ValueUtil.getDouble((IValue)newValue));
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, valueHandler);

		//minimum
		IWidgetPropertyChangeHandler minimumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setRange((Double) newValue, ((AbstractScaledWidgetModel)getModel()).getMaximum());
				return false;
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
				return false;
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
				return false;
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
				return false;
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
				return false;
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
				return false;
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
				return false;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_TRANSPARENT, transparentHandler);

		IWidgetPropertyChangeHandler scaleFontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.getScale().setFont(((OPIFont)newValue).getSWTFont());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_SCALE_FONT, scaleFontHandler);

		//scale format
		IWidgetPropertyChangeHandler numericFormatHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				AbstractScaledWidgetFigure scaleFigure = (AbstractScaledWidgetFigure) figure;
				setScaleFormat(scaleFigure, (String)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_SCALE_FORMAT, numericFormatHandler);

		//value label format
		IWidgetPropertyChangeHandler valueFormatHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				AbstractScaledWidgetFigure scaleFigure = (AbstractScaledWidgetFigure) figure;
				setValueLabelFormat(scaleFigure, (String)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_VALUE_LABEL_FORMAT, valueFormatHandler);

	}

	private void setScaleFormat(AbstractScaledWidgetFigure scaleFigure, String numericFormat){
		AbstractScale scale = scaleFigure.getScale();
		if(numericFormat.trim().equals("")) //$NON-NLS-1$
			scale.setAutoFormat(true);
		else{
			try {
				scale.setAutoFormat(false);
				scale.setFormatPattern(numericFormat);
			} catch (Exception e) {
				ConsoleService.getInstance().writeError(numericFormat +
						" is illegal Numeric Format." +
						" The scale will be auto formatted.");
				scale.setAutoFormat(true);
			}
		}
		//update value label
		scaleFigure.setValue(scaleFigure.getValue());
	}

	private void setValueLabelFormat(AbstractScaledWidgetFigure scaleFigure,
			String valueLabelFormat){
		try {
			scaleFigure.setValueLabelFormat(valueLabelFormat);
		} catch (Exception e) {
			ConsoleService.getInstance().writeError(valueLabelFormat +
					" is illegal Numeric Format." +
					" The value label will be formatted in the same way as scale.");
			scaleFigure.setValueLabelFormat(""); //$NON-NLS-1$
		}
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof Number)
			((AbstractScaledWidgetFigure)getFigure()).setValue(((Number)value).doubleValue());
		else 
			super.setValue(value);
	}

	@Override
	public Double getValue() {
		return ((AbstractScaledWidgetFigure)getFigure()).getValue();
	}

}
