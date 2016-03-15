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
import org.csstudio.opibuilder.widgets.model.AbstractPolyModel;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;
import org.csstudio.swt.widgets.figures.PolylineFigure;
import org.csstudio.swt.widgets.figures.PolylineFigure.ArrowType;
import org.eclipse.draw2d.IFigure;

/**
 *Editpart for polyline widget.
 *
 * @author Sven Wende, Alexander Will (similar class as in SDS)
 * @author Xihui Chen
 *
 */
public final class PolylineEditPart extends AbstractPolyEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        PolylineFigure polyline = new PolylineFigure();
        PolyLineModel model = getWidgetModel();
        polyline.setPoints(model.getPoints());
        polyline.setFill(model.getFillLevel());
        polyline.setHorizontalFill(model.isHorizontalFill());
        polyline.setTransparent(model.isTransparent());
        polyline.setArrowLineLength(model.getArrowLength());
        polyline.setArrowType(ArrowType.values()[model.getArrowType()]);
        polyline.setFillArrow(model.isFillArrow());

        return polyline;
    }


    @Override
    public PolyLineModel getWidgetModel() {
        return (PolyLineModel)getModel();
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
                PolylineFigure polyline = (PolylineFigure) refreshableFigure;
                polyline.setFill((Double) newValue);
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
                PolylineFigure figure = (PolylineFigure) refreshableFigure;
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
                PolylineFigure figure = (PolylineFigure) refreshableFigure;
                figure.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractShapeModel.PROP_TRANSPARENT, transparentHandler);

        // arrow Type
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                PolylineFigure figure = (PolylineFigure) refreshableFigure;
                figure.setArrowType(ArrowType.values()[(Integer)newValue]);
                getWidgetModel().updateBounds();
                return true;
            }
        };
        setPropertyChangeHandler(PolyLineModel.PROP_ARROW, handler);


        // arrow length
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                PolylineFigure figure = (PolylineFigure) refreshableFigure;
                figure.setArrowLineLength((Integer)newValue);
                getWidgetModel().updateBounds();
                return true;
            }
        };
        setPropertyChangeHandler(PolyLineModel.PROP_ARROW_LENGTH, handler);

        // Fill Arrow
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                PolylineFigure figure = (PolylineFigure) refreshableFigure;
                figure.setFillArrow((Boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(PolyLineModel.PROP_FILL_ARROW, handler);


    }

    @Override
    public void setValue(Object value) {
        if(value instanceof Number){
            ((PolylineFigure)getFigure()).setFill(((Number)value).doubleValue());
        }else
            super.setValue(value);
    }

    @Override
    public Object getValue() {
        return ((PolylineFigure)getFigure()).getFill();
    }
}
