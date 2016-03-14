/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.RectangleModel;
import org.csstudio.opibuilder.widgets.model.RoundedRectangleModel;
import org.csstudio.swt.widgets.figures.OPIRectangleFigure;
import org.eclipse.draw2d.IFigure;

/**The editpart of a rectangle widget.
 * @author Sven Wende & Stefan Hofer (similar class in SDS)
 * @author Xihui Chen
 *
 */
public class RectangleEditpart extends AbstractShapeEditPart {



    @Override
    protected IFigure doCreateFigure() {
        OPIRectangleFigure figure = new OPIRectangleFigure();
        RectangleModel model = getWidgetModel();
        figure.setFill(model.getFillLevel());
        figure.setHorizontalFill(model.isHorizontalFill());
        figure.setTransparent(model.isTransparent());
        figure.setLineColor(model.getLineColor());
        figure.setGradient(model.isGradient());
        figure.setBackGradientStartColor(model.getBackgroundGradientStartColor());
        figure.setForeGradientStartColor(model.getForegroundGradientStartColor());

        return figure;
    }

    @Override
    public RectangleModel getWidgetModel() {
        return (RectangleModel)getModel();
    }


    @Override
    protected void registerPropertyChangeHandlers() {
        super.registerPropertyChangeHandlers();
        // fill
        IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                OPIRectangleFigure figure = (OPIRectangleFigure) refreshableFigure;
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
                OPIRectangleFigure figure = (OPIRectangleFigure) refreshableFigure;
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
                OPIRectangleFigure figure = (OPIRectangleFigure) refreshableFigure;
                figure.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(RectangleModel.PROP_TRANSPARENT, transparentHandler);

        // line color
        IWidgetPropertyChangeHandler lineColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ((OPIRectangleFigure)refreshableFigure).setLineColor(
                        ((OPIColor)newValue).getSWTColor());
                return true;
            }
        };
        setPropertyChangeHandler(AbstractShapeModel.PROP_LINE_COLOR,
                lineColorHandler);

        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                ((OPIRectangleFigure)figure).setGradient((Boolean)newValue);
                return false;
            }
        };
        setPropertyChangeHandler(RectangleModel.PROP_GRADIENT, handler);

        handler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                ((OPIRectangleFigure)figure).setBackGradientStartColor(((OPIColor)newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(RectangleModel.PROP_BACKGROUND_GRADIENT_START_COLOR, handler);

        handler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                ((OPIRectangleFigure)figure).setForeGradientStartColor(((OPIColor)newValue).getSWTColor());
                return false;
            }
        };
        setPropertyChangeHandler(RoundedRectangleModel.PROP_FOREGROUND_GRADIENT_START_COLOR, handler);


    }

    @Override
    public void setValue(Object value) {
        if(value instanceof Number){
            ((OPIRectangleFigure)getFigure()).setFill(((Number)value).doubleValue());
        }else
            super.setValue(value);
    }

    @Override
    public Object getValue() {
        return ((OPIRectangleFigure)getFigure()).getFill();
    }



}
