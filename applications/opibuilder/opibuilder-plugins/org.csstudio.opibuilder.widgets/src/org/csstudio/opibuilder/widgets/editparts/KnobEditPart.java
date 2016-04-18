/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.KnobModel;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figures.KnobFigure;
import org.eclipse.draw2d.IFigure;

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
    protected IFigure doCreateFigure() {
        final KnobModel model = (KnobModel) getModel();

        KnobFigure knob = new KnobFigure();

        initializeCommonFigureProperties(knob, model);

        knob.setBulbColor(model.getKnobColor());
        knob.setEffect3D(model.isEffect3D());
        knob.setThumbColor(model.getThumbColor());
        knob.setValueLabelVisibility(model.isShowValueLabel());
        knob.setGradient(model.isRampGradient());
        knob.setIncrement(model.getIncrement());

        knob.addManualValueChangeListener(new IManualValueChangeListener() {

            @Override
            public void manualValueChanged(double newValue) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE){
                    setPVValue(AbstractPVWidgetModel.PROP_PVNAME, newValue);
                }
            }
        });

        markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);

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
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setBulbColor(((OPIColor) newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_KNOB_COLOR, knobColorHandler);


        //thumbColor
        IWidgetPropertyChangeHandler thumbColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setThumbColor(((OPIColor) newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_THUMB_COLOR, thumbColorHandler);

        //effect 3D
        IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setEffect3D((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_EFFECT3D, effect3DHandler);


        //show value label
        IWidgetPropertyChangeHandler valueLabelHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setValueLabelVisibility((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_SHOW_VALUE_LABEL, valueLabelHandler);

        //Ramp gradient
        IWidgetPropertyChangeHandler gradientHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setGradient((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_RAMP_GRADIENT, gradientHandler);




        //increment
        IWidgetPropertyChangeHandler incrementHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setIncrement((Double)newValue);
                return false;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_INCREMENT, incrementHandler);

        //force square size
        final IWidgetPropertyChangeHandler sizeHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                if(((Integer)newValue) < KnobModel.MINIMUM_SIZE)
                    newValue = KnobModel.MINIMUM_SIZE;
                getWidgetModel().setSize((Integer)newValue, (Integer)newValue);
                return false;
            }
        };
        PropertyChangeListener sizeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                sizeHandler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
            }
        };
        getWidgetModel().getProperty(AbstractWidgetModel.PROP_WIDTH).
            addPropertyChangeListener(sizeListener);
        getWidgetModel().getProperty(AbstractWidgetModel.PROP_HEIGHT).
            addPropertyChangeListener(sizeListener);

    }

}
