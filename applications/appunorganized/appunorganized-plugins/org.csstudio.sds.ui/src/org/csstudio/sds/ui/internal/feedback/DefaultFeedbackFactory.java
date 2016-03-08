/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.ui.internal.feedback;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.csstudio.sds.ui.internal.commands.SetBoundsCommand;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Default graphical feedback factory.
 *
 * @author Sven Wende
 *
 */
public final class DefaultFeedbackFactory implements IGraphicalFeedbackFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public IFigure createDragSourceFeedbackFigure(
            final AbstractWidgetModel model, final Rectangle initalBounds) {

        // Use a ghost rectangle for feedback
        RectangleFigure r = new RectangleFigure();
        FigureUtilities.makeGhostShape(r);
        r.setLineStyle(Graphics.LINE_DOT);
        r.setForegroundColor(ColorConstants.white);
        r.setBounds(initalBounds);

        return r;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showChangeBoundsFeedback(final AbstractWidgetModel model, final PrecisionRectangle bounds, final IFigure feedbackFigure, final ChangeBoundsRequest request) {
        feedbackFigure.translateToRelative(bounds);
        feedbackFigure.setBounds(bounds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shape createSizeOnDropFeedback(final CreateRequest createRequest) {
        return new RectangleFigure();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showSizeOnDropFeedback(final CreateRequest request,
            final IFigure feedbackFigure, final Insets insets) {
        Point p = new Point(request.getLocation().getCopy());

        feedbackFigure.translateToRelative(p);
        Dimension size = request.getSize().getCopy();
        feedbackFigure.translateToRelative(size);
        feedbackFigure.setBounds(new Rectangle(p, size).expand(insets));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class getCreationTool() {
        return null;
    }

    /**
     * Handles the given request.
     * @param widgetModel
     *                 The AbstractWidgetModel
     * @param request
     *                 The Request
     */
    public void handleRequest(final AbstractWidgetModel widgetModel,
            final Request request) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command createInitialBoundsCommand(
            final AbstractWidgetModel widgetModel,
            final CreateRequest request, final Rectangle bounds) {
        assert widgetModel != null;
        assert request != null;
        assert bounds != null;
        return new SetBoundsCommand(widgetModel, bounds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command createChangeBoundsCommand(final AbstractWidgetModel widgetModel,
            final ChangeBoundsRequest request, final Rectangle bounds) {
        assert widgetModel != null;
        assert request != null;
        assert bounds != null;
        return new SetBoundsCommand(widgetModel, bounds);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Handle> createCustomHandles(final GraphicalEditPart hostEP) {
        return null;
    }
}
