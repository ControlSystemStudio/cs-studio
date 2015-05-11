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
package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.RectangleModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangleFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the Rectangle widget. The controller mediates between
 * {@link RectangleModel} and {@link RefreshableRectangleFigure}.
 *
 * @author Sven Wende & Stefan Hofer
 *
 */
public final class RectangleEditPart extends AbstractWidgetEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        RectangleModel model = (RectangleModel) getWidgetModel();

        RefreshableRectangleFigure rectangle = new RefreshableRectangleFigure();
        rectangle.setOrientation(model.getOrientation());
        rectangle.setFill(model.getFillLevel());
        rectangle.setTransparent(model.getTransparent());

        return rectangle;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // fill
        IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableRectangleFigure rectangle = (RefreshableRectangleFigure) refreshableFigure;
                rectangle.setFill((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(RectangleModel.PROP_FILL, fillHandler);
        //orientation
        IWidgetPropertyChangeHandler orientationHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableRectangleFigure rectangle = (RefreshableRectangleFigure) refreshableFigure;
                rectangle.setOrientation((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(RectangleModel.PROP_ORIENTATION, orientationHandler);
        //transparent
        IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableRectangleFigure rectangle = (RefreshableRectangleFigure) refreshableFigure;
                rectangle.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(RectangleModel.PROP_TRANSPARENT, transparentHandler);
    }

}
