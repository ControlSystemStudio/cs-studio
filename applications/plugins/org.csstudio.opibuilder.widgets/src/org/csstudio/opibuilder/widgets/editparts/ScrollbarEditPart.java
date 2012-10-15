/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.ScrollBarModel;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figures.ScrollbarFigure;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.IFigure;

/**
 * The controller of scrollbar widget.
 *
 * @author Xihui Chen
 *
 */
public class ScrollbarEditPart extends AbstractPVWidgetEditPart {

	private PVListener pvLoadLimitsListener;
	private INumericMetaData meta = null;


	@Override
	public ScrollBarModel getWidgetModel() {
		return (ScrollBarModel) super.getWidgetModel();
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		registerLoadLimitsListener();
	}

	/**
	 *
	 */
	private void registerLoadLimitsListener() {
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			final ScrollBarModel model = getWidgetModel();
			if(model.isLimitsFromPV()){
				PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if(pv != null){
					if(pvLoadLimitsListener == null)
						pvLoadLimitsListener = new PVListener() {
							public void pvValueUpdate(PV pv) {
								IValue value = pv.getValue();
								if (value != null && value.getMetaData() instanceof INumericMetaData){
									INumericMetaData new_meta = (INumericMetaData)value.getMetaData();
									if(meta == null || !meta.equals(new_meta)){
										meta = new_meta;
										model.setPropertyValue(ScrollBarModel.PROP_MAX,	meta.getDisplayHigh());
										model.setPropertyValue(ScrollBarModel.PROP_MIN,	meta.getDisplayLow());
									}
								}
							}
							public void pvDisconnected(PV pv) {}
						};
					pv.addListener(pvLoadLimitsListener);
				}
			}
		}
	}





	@Override
	protected IFigure doCreateFigure() {
		ScrollbarFigure scrollBar = new ScrollbarFigure();
		ScrollBarModel model = getWidgetModel();


		scrollBar.setMaximum(model.getMaximum());
		scrollBar.setMinimum(model.getMinimum());
		scrollBar.setStepIncrement(model.getStepIncrement());
		scrollBar.setPageIncrement(model.getPageIncrement());
		scrollBar.setExtent(model.getBarLength());
		scrollBar.setShowValueTip(model.isShowValueTip());
		scrollBar.setHorizontal(model.isHorizontal());

		if (getExecutionMode() == ExecutionMode.RUN_MODE){
			scrollBar.addManualValueChangeListener(new IManualValueChangeListener() {

				public void manualValueChanged(double newValue) {
					setPVValue(ScrollBarModel.PROP_PVNAME, newValue);
				}
			});
		}

		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		return scrollBar;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				registerLoadLimitsListener();
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, pvNameHandler);


		((ScrollbarFigure)getFigure()).setEnabled(getWidgetModel().isEnabled() &&
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
				 ((ScrollbarFigure) refreshableFigure).
				 	setValue(ValueUtil.getDouble((IValue)newValue));
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, valueHandler);

		//minimum
		IWidgetPropertyChangeHandler minimumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				((ScrollbarFigure) refreshableFigure).setMinimum((Double)newValue);

				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_MIN, minimumHandler);

		//maximum
		IWidgetPropertyChangeHandler maximumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				((ScrollbarFigure) refreshableFigure).setMaximum((Double)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_MAX, maximumHandler);


		//page increment
		IWidgetPropertyChangeHandler pageIncrementHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				((ScrollbarFigure) refreshableFigure).setPageIncrement((Double)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_PAGE_INCREMENT, pageIncrementHandler);

		//step increment
		IWidgetPropertyChangeHandler stepIncrementHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				((ScrollbarFigure) refreshableFigure).setStepIncrement((Double)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_STEP_INCREMENT, stepIncrementHandler);

		//bar length
		IWidgetPropertyChangeHandler barLengthHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				((ScrollbarFigure) refreshableFigure).setExtent((Double)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_BAR_LENGTH, barLengthHandler);

		//value tip
		IWidgetPropertyChangeHandler valueTipHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				((ScrollbarFigure) refreshableFigure).setShowValueTip((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_SHOW_VALUE_TIP, valueTipHandler);


		//horizontal
		IWidgetPropertyChangeHandler horizontalHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				((ScrollbarFigure) refreshableFigure).setHorizontal((Boolean)newValue);
				ScrollBarModel model = getWidgetModel();
				if((Boolean) newValue) //from vertical to horizontal
					model.setLocation(model.getLocation().x - model.getSize().height/2 + model.getSize().width/2,
						model.getLocation().y + model.getSize().height/2 - model.getSize().width/2);
				else  //from horizontal to vertical
					model.setLocation(model.getLocation().x + model.getSize().width/2 - model.getSize().height/2,
						model.getLocation().y - model.getSize().width/2 + model.getSize().height/2);

				model.setSize(model.getSize().height, model.getSize().width);
				return false;
			}
		};
		setPropertyChangeHandler(ScrollBarModel.PROP_HORIZONTAL, horizontalHandler);

	}

	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if(getWidgetModel().isLimitsFromPV()){
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if(pv != null && pvLoadLimitsListener != null){
				pv.removeListener(pvLoadLimitsListener);
			}
		}

	}

	@Override
	public void setValue(Object value) {
		if(value instanceof Number)
			((ScrollbarFigure)getFigure()).setValue(((Number)value).doubleValue());
		else
			super.setValue(value);
	}

	@Override
	public Double getValue() {
		return ((ScrollbarFigure)getFigure()).getValue();
	}



}
