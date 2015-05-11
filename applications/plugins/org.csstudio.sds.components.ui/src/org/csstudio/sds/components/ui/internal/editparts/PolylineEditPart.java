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

import org.csstudio.sds.components.model.AbstractPolyModel;
import org.csstudio.sds.components.model.PolylineModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolylineFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.EditPart;

/**
 * EditPart controller for the Polyline widget. The controller mediates between
 * {@link PolylineModel} and {@link RefreshablePolylineFigure}.
 *
 * @author Sven Wende, Alexander Will
 *
 */
public final class PolylineEditPart extends AbstractWidgetEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        RefreshablePolylineFigure polyline = new RefreshablePolylineFigure();
        PolylineModel model = (PolylineModel) getWidgetModel();

        polyline.setPoints(model.getPoints());
        polyline.setFill(model.getFill());
        polyline.setLineWidth(model.getLineWidth());
        polyline.setLineStyle(model.getLineStyle());

        return polyline;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // line width
        IWidgetPropertyChangeHandler lineWidthHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) refreshableFigure;
                polyline.setLineWidth((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(PolylineModel.PROP_LINE_WIDTH,
                lineWidthHandler);

        // line style
        IWidgetPropertyChangeHandler lineStyleHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) refreshableFigure;
                polyline.setLineStyle((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(PolylineModel.PROP_LINE_STYLE,
                lineStyleHandler);

        // fill
        IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) refreshableFigure;
                polyline.setFill((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractPolyModel.PROP_FILL, fillHandler);

        // points
        IWidgetPropertyChangeHandler pointsHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) refreshableFigure;

                PointList points = (PointList) newValue;

                // deselect the widget (this refreshes the polypoint drag
                // handles)
                int selectionState = getSelected();
                setSelected(EditPart.SELECTED_NONE);

                polyline.setPoints(points);
                doRefreshVisuals(polyline);

                // restore the selection state
                setSelected(selectionState);

                return true;
            }
        };
        setPropertyChangeHandler(AbstractPolyModel.PROP_POINTS, pointsHandler);
    }
}
