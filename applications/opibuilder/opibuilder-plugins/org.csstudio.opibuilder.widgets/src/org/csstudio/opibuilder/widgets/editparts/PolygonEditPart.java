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
import org.csstudio.opibuilder.widgets.model.AbstractPolyModel;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.PolygonModel;
import org.csstudio.swt.widgets.figures.PolygonFigure;
import org.eclipse.draw2d.IFigure;

/**
 * Editpart of polygon widget.
 *
 * @author Sven Wende & Stefan Hofer (similar class as in SDS)
 * @author Xihui Chen
 *
 */
public final class PolygonEditPart extends AbstractPolyEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        PolygonFigure polygon = new PolygonFigure();
        PolygonModel model = getWidgetModel();
        polygon.setPoints(model.getPoints());
        polygon.setFill(model.getFillLevel());
        polygon.setHorizontalFill(model.isHorizontalFill());
        polygon.setTransparent(model.isTransparent());
        polygon.setLineColor(model.getLineColor());
        return polygon;
    }

    @Override
    public PolygonModel getWidgetModel() {
        return (PolygonModel)getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        super.registerPropertyChangeHandlers();

        // fill
        IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                PolygonFigure polygon = (PolygonFigure) refreshableFigure;
                polygon.setFill((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractPolyModel.PROP_FILL_LEVEL, fillHandler);

        // fill orientaion
        IWidgetPropertyChangeHandler fillOrientHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                PolygonFigure figure = (PolygonFigure) refreshableFigure;
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
                PolygonFigure figure = (PolygonFigure) refreshableFigure;
                figure.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractShapeModel.PROP_TRANSPARENT, transparentHandler);

        // line color
        IWidgetPropertyChangeHandler lineColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ((PolygonFigure)refreshableFigure).setLineColor(
                        ((OPIColor)newValue).getSWTColor());
                return true;
            }
        };
        setPropertyChangeHandler(AbstractShapeModel.PROP_LINE_COLOR,
                lineColorHandler);


    }

    @Override
    public void setValue(Object value) {
        if(value instanceof Number){
            ((PolygonFigure)getFigure()).setFill(((Number)value).doubleValue());
        }else
            super.setValue(value);
    }

    @Override
    public Object getValue() {
        return ((PolygonFigure)getFigure()).getFill();
    }
}
