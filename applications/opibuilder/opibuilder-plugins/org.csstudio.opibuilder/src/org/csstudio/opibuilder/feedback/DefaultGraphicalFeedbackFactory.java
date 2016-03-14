/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.feedback;

import java.util.List;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

/**A default implementation of {@link IGraphicalFeedbackFactory} which does nothing to changing
 * default graphical feedback behavior.
 * Subclass can override this class to create customized feedback behavior.
 * @author Xihui Chen
 *
 */
public class DefaultGraphicalFeedbackFactory implements
        IGraphicalFeedbackFactory {

    @Override
    public IFigure createDragSourceFeedbackFigure(AbstractWidgetModel model,
            Rectangle initalBounds) {
        return null;
    }

    @Override
    public void showChangeBoundsFeedback(AbstractWidgetModel widgetModel,
            PrecisionRectangle bounds, IFigure feedbackFigure,
            ChangeBoundsRequest request) {

        feedbackFigure.translateToRelative(bounds);
        feedbackFigure.setBounds(bounds);

    }

    @Override
    public Shape createSizeOnDropFeedback(CreateRequest createRequest) {
        return null;
    }

    @Override
    public void showSizeOnDropFeedback(CreateRequest request,
            IFigure feedback, Insets insets) {
        Point p = new Point(request.getLocation().getCopy());
        feedback.translateToRelative(p);
        Dimension size = request.getSize().getCopy();
        feedback.translateToRelative(size);
        feedback.setBounds(new Rectangle(p, size)
                .expand(insets));

    }

    @Override
    public Class<?> getCreationTool() {
        return null;
    }

    @Override
    public Command createChangeBoundsCommand(AbstractWidgetModel widgetModel,
            ChangeBoundsRequest request, Rectangle targetBounds) {
        return null;
    }

    @Override
    public Command createInitialBoundsCommand(AbstractWidgetModel widgetModel,
            CreateRequest request, Rectangle targetBounds) {
        return null;
    }

    @Override
    public List<Handle> createCustomHandles(GraphicalEditPart editPart) {
        return null;
    }

}
