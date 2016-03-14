/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;


import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.ScaledSliderModel;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figures.ScaledSliderFigure;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the scaled slider widget. The controller mediates between
 * {@link ScaledSliderModel} and {@link ScaledSliderFigure}.
 *
 * @author Xihui Chen
 *
 */
public final class ScaledSliderEditPart extends AbstractMarkedWidgetEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        final ScaledSliderModel model = getWidgetModel();

        ScaledSliderFigure slider = new ScaledSliderFigure();

        initializeCommonFigureProperties(slider, model);
        slider.setFillColor(model.getFillColor());
        slider.setEffect3D(model.isEffect3D());
        slider.setFillBackgroundColor(model.getFillbackgroundColor());
        slider.setThumbColor(model.getThumbColor());
        slider.setHorizontal(model.isHorizontal());
        slider.setStepIncrement(model.getStepIncrement());
        slider.setPageIncrement(model.getPageIncrement());
        slider.addManualValueChangeListener(new IManualValueChangeListener() {

            public void manualValueChanged(double newValue) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE)
                    setPVValue(ScaledSliderModel.PROP_PVNAME, newValue);
            }
        });

        markAsControlPV(ScaledSliderModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
        return slider;

    }

    @Override
    public ScaledSliderModel getWidgetModel() {
        return (ScaledSliderModel)getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        //fillColor
        IWidgetPropertyChangeHandler fillColorHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setFillColor(((OPIColor) newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_FILL_COLOR, fillColorHandler);

        //fillBackgroundColor
        IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setFillBackgroundColor(((OPIColor) newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);

        //thumbColor
        IWidgetPropertyChangeHandler thumbColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setThumbColor(((OPIColor) newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_THUMB_COLOR, thumbColorHandler);

        //effect 3D
        IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setEffect3D((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_EFFECT3D, effect3DHandler);


        //horizontal
        IWidgetPropertyChangeHandler horizontalHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setHorizontal((Boolean) newValue);
                ScaledSliderModel model = (ScaledSliderModel)getModel();

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
        setPropertyChangeHandler(ScaledSliderModel.PROP_HORIZONTAL, horizontalHandler);


        //enabled. WidgetBaseEditPart will force the widget as disabled in edit model,
        //which is not the case for the scaled slider
        IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setEnabled((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_ENABLED, enableHandler);


        IWidgetPropertyChangeHandler incrementHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setStepIncrement((Double)newValue);
                return false;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_STEP_INCREMENT, incrementHandler);

        IWidgetPropertyChangeHandler pageIncrementHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setPageIncrement((Double)newValue);
                return false;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_PAGE_INCREMENT, pageIncrementHandler);

    }

}
