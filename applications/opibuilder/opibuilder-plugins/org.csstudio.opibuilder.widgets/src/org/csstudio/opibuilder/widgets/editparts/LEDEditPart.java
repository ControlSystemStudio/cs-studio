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

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractBoolWidgetModel;
import org.csstudio.opibuilder.widgets.model.LEDModel;
import org.csstudio.swt.widgets.figures.AbstractBoolFigure;
import org.csstudio.swt.widgets.figures.LEDFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

/**
 * LED EditPart
 * @author Xihui Chen
 *
 */
public class LEDEditPart extends AbstractBoolEditPart{

    @Override
    protected IFigure doCreateFigure() {
        final LEDModel model = getWidgetModel();

        LEDFigure led = new LEDFigure();

        initializeCommonFigureProperties(led, model);
        led.setEffect3D(model.isEffect3D());
        led.setSquareLED(model.isSquareLED());
        return led;


    }

    @Override
    public LEDModel getWidgetModel() {
        return (LEDModel)getModel();
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        //effect 3D
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                LEDFigure led = (LEDFigure) refreshableFigure;
                led.setEffect3D((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(LEDModel.PROP_EFFECT3D, handler);

        //Sqaure LED
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                LEDFigure led = (LEDFigure) refreshableFigure;
                led.setSquareLED((Boolean) newValue);
                if(!(Boolean)newValue){
                    int width = Math.min(getWidgetModel().getWidth(), getWidgetModel().getHeight());
                    getWidgetModel().setSize(width, width);
                }
                return true;
            }
        };
        setPropertyChangeHandler(LEDModel.PROP_SQUARE_LED, handler);

        //force square size
        final IWidgetPropertyChangeHandler sizeHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                if(getWidgetModel().isSquareLED())
                    return false;
                if(((Integer)newValue) < LEDModel.MINIMUM_SIZE)
                    newValue = LEDModel.MINIMUM_SIZE;
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

        //nStates
        getWidgetModel().getProperty(LEDModel.PROP_NSTATES).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                initializeNStatesProperties((Integer)evt.getOldValue(), (Integer)evt.getNewValue(), (LEDFigure)getFigure(), getWidgetModel());
            }
        });


        //stateFallbackLabel
        getWidgetModel().getProperty(LEDModel.PROP_STATE_FALLBACK_LABEL).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                initializeStateFallbackLabel((String)evt.getOldValue(), (String)evt.getNewValue(), (LEDFigure)getFigure(), getWidgetModel());
            }
        });


        //stateFallbackColor
        getWidgetModel().getProperty(LEDModel.PROP_STATE_FALLBACK_COLOR).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                initializeStateFallbackColor(((OPIColor)evt.getOldValue()).getSWTColor(), ((OPIColor)evt.getNewValue()).getSWTColor(), (LEDFigure)getFigure(), getWidgetModel());
            }
        });


        //bulbBorderWidth
        getWidgetModel().getProperty(LEDModel.PROP_BULB_BORDER).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                initializeStateBulbBorderWidth((Integer)evt.getNewValue(), (LEDFigure)getFigure(), getWidgetModel());
            }
        });


        //bulbBorderColor
        getWidgetModel().getProperty(LEDModel.PROP_BULB_BORDER_COLOR).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                initializeStateBulbBorderColor(((OPIColor)evt.getNewValue()).getSWTColor(), (LEDFigure)getFigure(), getWidgetModel());
            }
        });

        for(int idx=0; idx<LEDFigure.MAX_NSTATES; idx++) {
            final int state = idx;
            //stateLabelN
            getWidgetModel().getProperty(String.format(LEDModel.PROP_STATE_LABEL, state)).addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    initializeStateLabel(state, (String)evt.getOldValue(), (String)evt.getNewValue(), (LEDFigure)getFigure(), getWidgetModel());
                }
            });
            //stateColorN
            getWidgetModel().getProperty(String.format(LEDModel.PROP_STATE_COLOR, state)).addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    initializeStateColor(state, ((OPIColor)evt.getOldValue()).getSWTColor(), ((OPIColor)evt.getNewValue()).getSWTColor(), (LEDFigure)getFigure(), getWidgetModel());
                }
            });
            //stateValueN
            getWidgetModel().getProperty(String.format(LEDModel.PROP_STATE_VALUE, state)).addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    initializeStateValue(state, (Double)evt.getOldValue(), (Double)evt.getNewValue(), (LEDFigure)getFigure(), getWidgetModel());
                }
            });
        }
    }

    @Override
    protected void initializeCommonFigureProperties(
            final AbstractBoolFigure abstractFigure, final AbstractBoolWidgetModel abstractModel) {

        super.initializeCommonFigureProperties(abstractFigure, abstractModel);

        LEDModel model = (LEDModel)abstractModel;
        LEDFigure figure = (LEDFigure)abstractFigure;

        initializeStateBulbBorderColor(model.getBulbBorderColor(), figure, model);
        initializeStateBulbBorderWidth(model.getBulbBorderWidth(), figure, model);

        initializeNStatesProperties(LEDFigure.MAX_NSTATES, model.getNStates(), figure, model);
        initializeStateFallbackLabel(null, model.getStateFallbackLabel(), figure, model);
        initializeStateFallbackColor(null, model.getStateFallbackColor(), figure, model);
        for(int state = 0; state < LEDFigure.MAX_NSTATES; state++) {
            initializeStateColor(state, null, model.getStateColor(state), figure, model);
            initializeStateLabel(state, null, model.getStateLabel(state), figure, model);
            initializeStateValue(state, 0.0, model.getStateValue(state), figure, model);
        }
    }

    protected void initializeNStatesProperties(int oldNStates, int newNStates, LEDFigure figure, LEDModel model) {
        if(newNStates <= 2) {
            model.setPropertyVisible(LEDModel.PROP_ON_COLOR, true);
            model.setPropertyVisible(LEDModel.PROP_ON_LABEL, true);
            model.setPropertyVisible(LEDModel.PROP_OFF_COLOR, true);
            model.setPropertyVisible(LEDModel.PROP_OFF_LABEL, true);
            model.setPropertyVisibleAndSavable(LEDModel.PROP_NSTATES, true, false);
            model.setPropertyVisibleAndSavable(LEDModel.PROP_STATE_FALLBACK_COLOR, false, false);
            model.setPropertyVisibleAndSavable(LEDModel.PROP_STATE_FALLBACK_LABEL, false, false);
            for(int idx = 0; idx<oldNStates; idx++) {
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_COLOR, idx), false, false);
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_LABEL, idx), false, false);
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_VALUE, idx), false, false);
            }
        } else if(newNStates > 2) {
            model.setPropertyVisible(LEDModel.PROP_ON_COLOR, false);
            model.setPropertyVisible(LEDModel.PROP_ON_LABEL, false);
            model.setPropertyVisible(LEDModel.PROP_OFF_COLOR, false);
            model.setPropertyVisible(LEDModel.PROP_OFF_LABEL, false);
            model.setPropertyVisibleAndSavable(LEDModel.PROP_NSTATES, true, true);
            model.setPropertyVisibleAndSavable(LEDModel.PROP_STATE_FALLBACK_COLOR, true, true);
            model.setPropertyVisibleAndSavable(LEDModel.PROP_STATE_FALLBACK_LABEL, true, true);
            for(int idx = 0; idx<newNStates; idx++) {
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_COLOR, idx), true, true);
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_LABEL, idx), true, true);
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_VALUE, idx), true, true);
            }
            for(int idx = newNStates; idx<oldNStates; idx++) {
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_COLOR, idx), false, false);
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_LABEL, idx), false, false);
                model.setPropertyVisibleAndSavable(String.format(LEDModel.PROP_STATE_VALUE, idx), false, false);
            }
        }
        figure.setNStates(newNStates);
    }

    protected void initializeStateFallbackLabel(String oldLabel, String newLabel, LEDFigure figure, LEDModel model) {
        figure.setStateFallbackLabel(newLabel);
    }

    protected void initializeStateFallbackColor(Color oldColor, Color newColor, LEDFigure figure, LEDModel model) {
        figure.setStateFallbackColor(newColor);
    }

    protected void initializeStateLabel(int state, String oldLabel, String newLabel, LEDFigure figure, LEDModel model) {
        figure.setStateLabel(state, newLabel);
    }

    protected void initializeStateColor(int state, Color oldColor, Color newColor, LEDFigure figure, LEDModel model) {
        figure.setStateColor(state, newColor);
    }

    protected void initializeStateValue(int state, double oldValue, double newValue, LEDFigure figure, LEDModel model) {
        figure.setStateValue(state, newValue);
    }


    protected void initializeStateBulbBorderWidth(int newWidth, LEDFigure figure, LEDModel model) {
        figure.setBulbBorderWidth(newWidth);
    }

    protected void initializeStateBulbBorderColor(Color newColor, LEDFigure figure, LEDModel model) {
        figure.setBulbBorderColor(newColor);
    }
}
