/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.RoundedRectangleModel;
import org.csstudio.swt.widgets.figures.RoundedRectangleFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

/**The editpart of a rectangle widget.
 * @author Xihui Chen
 *
 */
public class RoundedRectangleEditpart extends AbstractShapeEditPart {



    @Override
    protected IFigure doCreateFigure() {
        RoundedRectangleFigure figure = new RoundedRectangleFigure();
        RoundedRectangleModel model = getWidgetModel();
        figure.setFill(model.getFillLevel());
        figure.setHorizontalFill(model.isHorizontalFill());
        figure.setTransparent(model.isTransparent());
        figure.setCornerDimensions(new Dimension(model.getCornerWidth(), model.getCornerHeight()));
        figure.setLineColor(model.getLineColor());
        figure.setGradient(model.isGradient());
        figure.setBackGradientStartColor(model.getBackgroundGradientStartColor());
        figure.setForeGradientStartColor(model.getForegroundGradientStartColor());

        return figure;
    }

    @Override
    public RoundedRectangleModel getWidgetModel() {
        return (RoundedRectangleModel)getModel();
    }


    @Override
    protected void registerPropertyChangeHandlers() {
        super.registerPropertyChangeHandlers();
        // fill
        IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RoundedRectangleFigure figure = (RoundedRectangleFigure) refreshableFigure;
                figure.setFill((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractShapeModel.PROP_FILL_LEVEL, fillHandler);

        // fill orientaion
        IWidgetPropertyChangeHandler fillOrientHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RoundedRectangleFigure figure = (RoundedRectangleFigure) refreshableFigure;
                figure.setHorizontalFill((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractShapeModel.PROP_HORIZONTAL_FILL, fillOrientHandler);

        // transparent
        IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RoundedRectangleFigure figure = (RoundedRectangleFigure) refreshableFigure;
                figure.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(RoundedRectangleModel.PROP_TRANSPARENT, transparentHandler);


        // line color
        IWidgetPropertyChangeHandler lineColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ((RoundedRectangleFigure)refreshableFigure).setLineColor(
                        ((OPIColor)newValue).getSWTColor());
                return true;
            }
        };
        setPropertyChangeHandler(AbstractShapeModel.PROP_LINE_COLOR,
                lineColorHandler);


        //corner width
        IWidgetPropertyChangeHandler cornerWidthHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RoundedRectangleFigure figure = (RoundedRectangleFigure) refreshableFigure;
                figure.setCornerWidth((Integer)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(RoundedRectangleModel.PROP_CORNER_WIDTH, cornerWidthHandler);

        //corner height
        IWidgetPropertyChangeHandler cornerHeightHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RoundedRectangleFigure figure = (RoundedRectangleFigure) refreshableFigure;
                figure.setCornerHeight((Integer)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(RoundedRectangleModel.PROP_CORNER_HEIGHT, cornerHeightHandler);

        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                ((RoundedRectangleFigure)figure).setGradient((Boolean)newValue);
                return false;
            }
        };
        setPropertyChangeHandler(RoundedRectangleModel.PROP_GRADIENT, handler);

        handler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                ((RoundedRectangleFigure)figure).setBackGradientStartColor(((OPIColor)newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(RoundedRectangleModel.PROP_BACKGROUND_GRADIENT_START_COLOR, handler);

        handler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                ((RoundedRectangleFigure)figure).setForeGradientStartColor(((OPIColor)newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(RoundedRectangleModel.PROP_FOREGROUND_GRADIENT_START_COLOR, handler);

    }

    @Override
    public void setValue(Object value) {
        if(value instanceof Number){
            ((RoundedRectangleFigure)getFigure()).setFill(((Number)value).doubleValue());
        }else
            super.setValue(value);
    }

    @Override
    public Object getValue() {
        return ((RoundedRectangleFigure)getFigure()).getFill();
    }


}
