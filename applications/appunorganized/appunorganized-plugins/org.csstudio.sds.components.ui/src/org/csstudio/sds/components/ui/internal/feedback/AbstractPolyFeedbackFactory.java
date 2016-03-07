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
package org.csstudio.sds.components.ui.internal.feedback;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.components.model.AbstractPolyModel;
import org.csstudio.sds.components.model.PolylineModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Graphical feedback factory for polyline widgets.
 *
 * @author Sven Wende
 */
abstract class AbstractPolyFeedbackFactory implements
        IGraphicalFeedbackFactory {
    /**
     * An identifier which is used as key for extended data in request objects.
     */
    public static final String PROP_POINTS = "points"; //$NON-NLS-1$

    /**
     * Subclasses should return an appropriate feedback figure. This basically
     * supports code inheritance for the polyline and polygon implementations.
     *
     * @return a polyline or polygon figure which is used for graphical feedback
     */
    protected abstract Polyline createFeedbackFigure();

    /**
     * {@inheritDoc}
     */
    @Override
    public final IFigure createDragSourceFeedbackFigure(
            final AbstractWidgetModel model, final Rectangle initalBounds) {
        assert model != null;
        assert model instanceof AbstractPolyModel : "model instanceof AbstractPolyModel"; //$NON-NLS-1$
        assert initalBounds != null;

        // get the points from the model
        AbstractPolyModel abstractPolyElement = (AbstractPolyModel) model;
        PointList points = abstractPolyElement.getPoints();

        // create feedbackfigure
        // RectangleWithPolyLineFigure r = new
        // RectangleWithPolyLineFigure(points);

        PolyFeedbackFigureWithRectangle feedbackFigure = new PolyFeedbackFigureWithRectangle(
                createFeedbackFigure(), points);

        return feedbackFigure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void showChangeBoundsFeedback(
            final AbstractWidgetModel model, final PrecisionRectangle bounds,
            final IFigure feedbackFigure, final ChangeBoundsRequest request) {
        assert model != null;
        assert model instanceof AbstractPolyModel : "model instanceof AbstractPolyModel"; //$NON-NLS-1$
        assert bounds != null;
        assert feedbackFigure != null;
        assert feedbackFigure instanceof PolyFeedbackFigureWithRectangle : "feedbackFigure instanceof AbstractPolyFeedbackFigure"; //$NON-NLS-1$
        assert request != null;

        PolyFeedbackFigureWithRectangle figure = (PolyFeedbackFigureWithRectangle) feedbackFigure;

        figure.translateToRelative(bounds);

        // try to get a point list from the request (this happens only, when
        // poly point handles are dragged arround)
        PointList points = (PointList) request.getExtendedData().get(
                PROP_POINTS);

        // otherwise take the points from the model
        if (points == null) {
            points = ((AbstractPolyModel) model).getPoints();
        }

        // scale the points to the specified bounds
        PointList scaledPoints = PointListHelper.scaleTo(points.getCopy(),
                bounds);

        // apply the scaled points
        figure.setPoints(scaledPoints);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Shape createSizeOnDropFeedback(
            final CreateRequest createRequest) {
        assert createRequest != null;

        // Polyline polyline = new Polyline();

        // the request should contain a point list, because the creation is done
        // by a special creation tool
        PointList points = (PointList) createRequest.getExtendedData().get(
                PROP_POINTS);

        assert points != null;

        // polyline.setPoints(points);

        Polyline feedbackFigure = createFeedbackFigure();
        feedbackFigure.setPoints(points);

        return feedbackFigure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void showSizeOnDropFeedback(final CreateRequest createRequest,
            final IFigure feedbackFigure, final Insets insets) {
        assert createRequest != null;
        assert feedbackFigure instanceof Polyline : "feedbackFigure instanceof Polyline"; //$NON-NLS-1$
        Polyline polyline = (Polyline) feedbackFigure;

        // the request should contain a point list, because the creation is done
        // by a special creation tool
        PointList points = ((PointList) createRequest.getExtendedData().get(
                PROP_POINTS)).getCopy();

        assert points != null;

        // the points are viewer relative and need to be translated to reflect
        // the zoom level, scrollbar occurence etc.
        polyline.translateToRelative(points);

        polyline.setPoints(points);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public final Class getCreationTool() {
        return PointListCreationTool.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Command createInitialBoundsCommand(
            final AbstractWidgetModel widgetModel,
            final CreateRequest request, final Rectangle bounds) {
        assert widgetModel instanceof AbstractPolyModel : "widgetModel instanceof AbstractPolyModel"; //$NON-NLS-1$
        assert request != null;
        assert bounds != null;

        AbstractPolyModel abstractPolyElement = (AbstractPolyModel) widgetModel;

        PointList points = (PointList) request.getExtendedData().get(
                PROP_POINTS);
        // necessary if the call was occurred by a "Drag and Drop" action
        if (points==null) {
            points = widgetModel.getPointlistProperty(AbstractPolyModel.PROP_POINTS);
        }

        // the points are viewer relative and need to be translated to the
        // specified bounds, to reflect zoom level, scrollbar occurence etc.
        PointList scaledPoints = PointListHelper.scaleTo(points, bounds);

        return new ChangePolyPointsCommand(abstractPolyElement, scaledPoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Command createChangeBoundsCommand(
            final AbstractWidgetModel model,
            final ChangeBoundsRequest request, final Rectangle targetBounds) {
        assert model instanceof AbstractPolyModel : "model instanceof AbstractPolyModel"; //$NON-NLS-1$

        Rectangle correctedBounds = targetBounds;
        if (model instanceof PolylineModel) {
            PolylineModel polyline = (PolylineModel) model;
            int correctedX = targetBounds.x + (polyline.getLineWidth() / 2);
            int correctedY = targetBounds.y + (polyline.getLineWidth() / 2);
            correctedBounds = new Rectangle(correctedX, correctedY, targetBounds.width, targetBounds.height);
        }

        AbstractPolyModel abstractPolyElement = (AbstractPolyModel) model;

        // try to get a point list from the request (this happens only, when
        // poly point handles are dragged arround)
        PointList points = (PointList) request.getExtendedData().get(
                PROP_POINTS);

        // otherwise take the points from the model
        if (points == null) {
            points = ((AbstractPolyModel) model).getPoints();
        }

        assert points != null;

        // the points are viewer relative and need to be translated to the
        // specified bounds, to reflect zoom level, scrollbar occurence etc.
        points = PointListHelper.scaleTo(points, correctedBounds);

        return new ChangePolyPointsCommand(abstractPolyElement, points);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Handle> createCustomHandles(final GraphicalEditPart hostEP) {
        assert hostEP != null;
        assert hostEP.getModel() instanceof AbstractPolyModel : "hostEP.getModel() instanceof AbstractPolyModel"; //$NON-NLS-1$

        // create some custom handles, which enable the user to drag arround
        // single points of the polyline
        List<Handle> handles = new ArrayList<Handle>();

        AbstractPolyModel abstractPolyElement = (AbstractPolyModel) hostEP
                .getModel();

        int pointCount = abstractPolyElement.getPoints().size();

        for (int i = 0; i < pointCount; i++) {
            PolyPointHandle myHandle = new PolyPointHandle(hostEP, i);
            handles.add(myHandle);
        }

        return handles;
    }

}
