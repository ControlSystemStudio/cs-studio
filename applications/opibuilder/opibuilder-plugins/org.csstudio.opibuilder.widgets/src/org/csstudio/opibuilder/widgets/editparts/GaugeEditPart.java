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
import org.csstudio.opibuilder.widgets.model.GaugeModel;
import org.csstudio.swt.widgets.figures.GaugeFigure;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the Gauge widget. The controller mediates between
 * {@link GaugeModel} and {@link GaugeFigure}.
 *
 * @author Xihui Chen
 *
 */
public final class GaugeEditPart extends AbstractMarkedWidgetEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        final GaugeModel model = getWidgetModel();

        GaugeFigure gauge = new GaugeFigure();

        initializeCommonFigureProperties(gauge, model);
        gauge.setNeedleColor(CustomMediaFactory.getInstance().getColor((model.getNeedleColor())));
        gauge.setEffect3D(model.isEffect3D());
        gauge.setGradient(model.isRampGradient());

        return gauge;

    }

    @Override
    public GaugeModel getWidgetModel() {
        return (GaugeModel)getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        //needle Color
        IWidgetPropertyChangeHandler needleColorColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                GaugeFigure gauge = (GaugeFigure) refreshableFigure;
                gauge.setNeedleColor(((OPIColor) newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(GaugeModel.PROP_NEEDLE_COLOR, needleColorColorHandler);


        //effect 3D
        IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                GaugeFigure gauge = (GaugeFigure) refreshableFigure;
                gauge.setEffect3D((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(GaugeModel.PROP_EFFECT3D, effect3DHandler);

        //Ramp gradient
        IWidgetPropertyChangeHandler gradientHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                GaugeFigure gauge = (GaugeFigure) refreshableFigure;
                gauge.setGradient((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(GaugeModel.PROP_RAMP_GRADIENT, gradientHandler);

        final IWidgetPropertyChangeHandler sizeHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                if(((Integer)newValue) < GaugeModel.MINIMUM_SIZE)
                    newValue = GaugeModel.MINIMUM_SIZE;
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
