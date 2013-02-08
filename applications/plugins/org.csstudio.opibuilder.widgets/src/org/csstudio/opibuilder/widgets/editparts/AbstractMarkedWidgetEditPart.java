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
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractMarkedWidgetModel;
import org.csstudio.opibuilder.widgets.model.AbstractScaledWidgetModel;
import org.csstudio.swt.widgets.figures.AbstractMarkedWidgetFigure;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.IFigure;

/**
 * Base editPart controller for a widget based on {@link AbstractMarkedWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public abstract class AbstractMarkedWidgetEditPart extends AbstractScaledWidgetEditPart{

	private INumericMetaData meta = null;
	private PVListener pvLoadLimitsListener;

	/**
	 * Sets those properties on the figure that are defined in the
	 * {@link AbstractMarkedWidgetFigure} base class. This method is provided for the
	 * convenience of subclasses, which can call this method in their
	 * implementation of {@link AbstractBaseEditPart#doCreateFigure()}.
	 *
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	protected void initializeCommonFigureProperties(
			final AbstractMarkedWidgetFigure figure, final AbstractMarkedWidgetModel model) {

		super.initializeCommonFigureProperties(figure, model);
		figure.setShowMarkers(model.isShowMarkers());

		figure.setLoloLevel(model.getLoloLevel());
		figure.setLoLevel(model.getLoLevel());
		figure.setHiLevel(model.getHiLevel());
		figure.setHihiLevel(model.getHihiLevel());

		figure.setShowLolo(model.isShowLolo());
		figure.setShowLo(model.isShowLo());
		figure.setShowHi(model.isShowHi());
		figure.setShowHihi(model.isShowHihi());

		figure.setLoloColor(model.getLoloColor());
		figure.setLoColor(model.getLoColor());
		figure.setHiColor(model.getHiColor());
		figure.setHihiColor(model.getHihiColor());


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
			final AbstractMarkedWidgetModel model = (AbstractMarkedWidgetModel)getModel();
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
										if(!Double.isNaN(meta.getDisplayHigh()))
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_MAX,	meta.getDisplayHigh());
										if(!Double.isNaN(meta.getDisplayLow()))
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_MIN,	meta.getDisplayLow());
										if(Double.isNaN(meta.getWarnHigh()))
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HI, false);
										else{
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HI, true);
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_HI_LEVEL,	meta.getWarnHigh());
										}
										if(Double.isNaN(meta.getAlarmHigh()))
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HIHI, false);
										else{
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HIHI, true);
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_HIHI_LEVEL, meta.getAlarmHigh());
										}
										if(Double.isNaN(meta.getWarnLow()))
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_LO, false);
										else{
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_LO, true);
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_LO_LEVEL,	meta.getWarnLow());
										}
										if(Double.isNaN(meta.getAlarmLow()))
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_LOLO, false);
										else{
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_LOLO, true);
											model.setPropertyValue(AbstractMarkedWidgetModel.PROP_LOLO_LEVEL,	meta.getAlarmLow());
										}
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
	public AbstractMarkedWidgetModel getWidgetModel() {
		return (AbstractMarkedWidgetModel) getModel();
	}
	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if(getWidgetModel().isLimitsFromPV()){
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if(pv != null && pvLoadLimitsListener !=null){
				pv.removeListener(pvLoadLimitsListener);
			}
		}

	}
	/**
	 * Registers property change handlers for the properties defined in
	 * {@link AbstractScaledWidgetModel}. This method is provided for the convenience
	 * of subclasses, which can call this method in their implementation of
	 * {@link #registerPropertyChangeHandlers()}.
	 */
	protected void registerCommonPropertyChangeHandlers() {
		super.registerCommonPropertyChangeHandlers();

		IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				registerLoadLimitsListener();
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, pvNameHandler);

		//showMarkers
		IWidgetPropertyChangeHandler showMarkersHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setShowMarkers((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, showMarkersHandler);


		//LoLo Level
		IWidgetPropertyChangeHandler loloHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setLoloLevel((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_LOLO_LEVEL, loloHandler);

		//Lo Level
		IWidgetPropertyChangeHandler loHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setLoLevel((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_LO_LEVEL, loHandler);

		//Hi Level
		IWidgetPropertyChangeHandler hiHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setHiLevel((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_HI_LEVEL, hiHandler);

		//HiHi Level
		IWidgetPropertyChangeHandler hihiHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setHihiLevel((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_HIHI_LEVEL, hihiHandler);

		//show lolo
		IWidgetPropertyChangeHandler showLoloHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setShowLolo((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_LOLO, showLoloHandler);

		//show lo
		IWidgetPropertyChangeHandler showLoHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setShowLo((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_LO, showLoHandler);

		//show Hi
		IWidgetPropertyChangeHandler showHiHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setShowHi((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_HI, showHiHandler);

		//show Hihi
		IWidgetPropertyChangeHandler showHihiHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setShowHihi((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_HIHI, showHihiHandler);


		//Lolo color
		IWidgetPropertyChangeHandler LoloColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setLoloColor(((OPIColor)newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_LOLO_COLOR, LoloColorHandler);

		//Lo color
		IWidgetPropertyChangeHandler LoColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setLoColor(((OPIColor)newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_LO_COLOR, LoColorHandler);

		//Hi color
		IWidgetPropertyChangeHandler HiColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setHiColor(((OPIColor)newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_HI_COLOR, HiColorHandler);

		//Hihi color
		IWidgetPropertyChangeHandler HihiColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
				figure.setHihiColor(((OPIColor)newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_HIHI_COLOR, HihiColorHandler);


	}

}
