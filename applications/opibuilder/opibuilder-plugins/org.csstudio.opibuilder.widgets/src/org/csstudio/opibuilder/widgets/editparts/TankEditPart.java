/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AlarmSeverityListener;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.TankModel;
import org.csstudio.swt.widgets.figures.TankFigure;
import org.eclipse.draw2d.IFigure;
import org.diirt.vtype.AlarmSeverity;

/**
 * EditPart controller for the tank widget. The controller mediates between
 * {@link TankModel} and {@link TankFigure}.
 *
 * @author Xihui Chen
 * @author Takashi Nakamoto - support for "FillColor Alarm Sensitive" property
 */
public final class TankEditPart extends AbstractMarkedWidgetEditPart {
    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        TankModel model = getWidgetModel();

        TankFigure tank = new TankFigure();

        initializeCommonFigureProperties(tank, model);
        tank.setFillColor(model.getFillColor());
        tank.setEffect3D(model.isEffect3D());
        tank.setFillBackgroundColor(model.getFillbackgroundColor());
        return tank;

    }

    @Override
    public TankModel getWidgetModel() {
        return (TankModel)getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        //fillColor
        IWidgetPropertyChangeHandler fillColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                TankFigure tank = (TankFigure) refreshableFigure;
                tank.setFillColor(((OPIColor) newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(TankModel.PROP_FILL_COLOR, fillColorHandler);

        //fillBackgroundColor
        IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                TankFigure tank = (TankFigure) refreshableFigure;
                tank.setFillBackgroundColor(((OPIColor) newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(TankModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);

        //effect 3D
        IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                TankFigure tank = (TankFigure) refreshableFigure;
                tank.setEffect3D((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(TankModel.PROP_EFFECT3D, effect3DHandler);

        // Change fill color when "FillColor Alarm Sensitive" property changes.
        IWidgetPropertyChangeHandler fillColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
                TankFigure figure = (TankFigure) refreshableFigure;
                boolean sensitive = (Boolean)newValue;
                figure.setFillColor(
                        delegate.calculateAlarmColor(sensitive,
                                                     getWidgetModel().getFillColor()));
                return true;
            }
        };
        setPropertyChangeHandler(TankModel.PROP_FILLCOLOR_ALARM_SENSITIVE, fillColorAlarmSensitiveHandler);

        // Change fill color when alarm severity changes.
        delegate.addAlarmSeverityListener(new AlarmSeverityListener() {

            @Override
            public boolean severityChanged(AlarmSeverity severity, IFigure figure) {
                if (!getWidgetModel().isFillColorAlarmSensitive())
                    return false;
                TankFigure tank = (TankFigure) figure;
                tank.setFillColor(
                        delegate.calculateAlarmColor(getWidgetModel().isFillColorAlarmSensitive(),
                                                     getWidgetModel().getFillColor()));
                return true;
            }
        });
    }
}
