/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.feedback;

import java.util.List;

import org.csstudio.opibuilder.feedback.IGraphicalFeedbackFactory;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.ui.util.ColorConstants;
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
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

/**Feedback factory for widgets which need square size.
 * @author Xihui Chen
 *
 */
public abstract class AbstractFixRatioSizeFeedbackFactory implements IGraphicalFeedbackFactory {

    private Shape sizeOnDropFeedback;

    public Command createChangeBoundsCommand(AbstractWidgetModel widgetModel,
            ChangeBoundsRequest request, Rectangle targetBounds) {

        return new ChangeSquareBoundsCommand(widgetModel, targetBounds, "Move/Resize");
    }

    public List<Handle> createCustomHandles(GraphicalEditPart editPart) {
        return null;
    }

    public IFigure createDragSourceFeedbackFigure(AbstractWidgetModel model,
            Rectangle initalBounds) {
        // Use a ghost rectangle for feedback
        RectangleFigure r = new RectangleFigure();
        FigureUtilities.makeGhostShape(r);
        r.setLineStyle(Graphics.LINE_DOT);
        r.setForegroundColor(ColorConstants.white);
        r.setBounds(initalBounds);
        return r;
    }

    public Command createInitialBoundsCommand(AbstractWidgetModel widgetModel,
            CreateRequest request, Rectangle targetBounds) {
        return new InitialSquareBoundsCommand(widgetModel, targetBounds);
    }

    public Shape createSizeOnDropFeedback(CreateRequest createRequest) {
        return getSizeOnDropFeedback();
    }

    @SuppressWarnings("rawtypes")
    public Class getCreationTool() {
        return null;
    }

    public void showChangeBoundsFeedback(AbstractWidgetModel widgetModel,
            PrecisionRectangle newBounds, IFigure feedbackFigure,
            ChangeBoundsRequest request) {
        if(newBounds.getSize().getArea() != widgetModel.getSize().getArea()){
            if(isSquareSizeRequired(widgetModel)){
                if(newBounds.getSize().getArea() > widgetModel.getSize().getArea())
                    newBounds.width = Math.max(newBounds.width, getWidthFromHeight(newBounds.height, widgetModel));
                else
                    newBounds.width = Math.min(newBounds.width, getWidthFromHeight(newBounds.height, widgetModel));
                if(newBounds.width < getMinimumWidth())
                    newBounds.width = getMinimumWidth();
                newBounds.height = getHeightFromWidth(newBounds.width, widgetModel);
            }
        }
        feedbackFigure.translateToRelative(newBounds);
        feedbackFigure.setBounds(newBounds);
    }

    public void showSizeOnDropFeedback(CreateRequest request,
            IFigure feedbackFigure, Insets insets) {
        Point p = new Point(request.getLocation().getCopy());
        IFigure feedback = getSizeOnDropFeedback(request);
        feedback.translateToRelative(p);
        Dimension size = request.getSize().getCopy();
        feedback.translateToRelative(size);
        if(isSquareSizeRequired((AbstractWidgetModel) request.getNewObject())){
            if(size.width < getMinimumWidth() && size.height < getMinimumWidth())
                size.width = getMinimumWidth();
            else
                size.width = Math.max(size.width,
                        getWidthFromHeight(size.height, (AbstractWidgetModel) request.getNewObject()));
            size.height = getHeightFromWidth(size.width, (AbstractWidgetModel) request.getNewObject());
        }
        feedback.setBounds(new Rectangle(p, size).expand(insets));
    }


    /**
     * Lazily creates and returns the Figure to use for size-on-drop feedback.
     * @param createRequest the createRequest
     * @return the size-on-drop feedback figure
     */
    protected IFigure getSizeOnDropFeedback(CreateRequest createRequest) {
        if (sizeOnDropFeedback == null)
            sizeOnDropFeedback = createSizeOnDropFeedback(createRequest);

        return getSizeOnDropFeedback();
    }

    /**
     * Lazily creates and returns the Figure to use for size-on-drop feedback.
     * @return the size-on-drop feedback figure
     */
    protected Shape getSizeOnDropFeedback() {
        if (sizeOnDropFeedback == null) {
            sizeOnDropFeedback = new RectangleFigure();
            FigureUtilities.makeGhostShape((Shape)sizeOnDropFeedback);
            ((Shape)sizeOnDropFeedback).setLineStyle(Graphics.LINE_DASHDOT);
            sizeOnDropFeedback.setForegroundColor(ColorConstants.white);
        }
        return sizeOnDropFeedback;
    }

    /**
     * @return the minimum size
     */
    public abstract int getMinimumWidth();

    public boolean isSquareSizeRequired(AbstractWidgetModel widgetModel){
        return true;
    }

    /**Calculate height from width. Return same value as width by default.
     * @param width
     * @return height.
     */
    public int getHeightFromWidth(int width, AbstractWidgetModel widgetModel){
        return width;
    }



    /**Calculate width from height. Return same value as height by default.
     * @param height
     * @return height.
     */
    public int getWidthFromHeight(int height, AbstractWidgetModel widgetModel){
        return height;
    }

    class ChangeSquareBoundsCommand extends Command{
        /** Stores the new size and location. */
        private final Rectangle newBounds;
        /** Stores the old size and location. */
        private Rectangle oldBounds;


        private final AbstractWidgetModel widget;
        public ChangeSquareBoundsCommand(AbstractWidgetModel widgetModel,
                Rectangle newBounds, String label) {
            super(label);
            if (widgetModel == null || newBounds == null)
                throw new IllegalArgumentException();
            this.widget = widgetModel;
            this.newBounds = newBounds;

        }


        @Override
        public void execute() {
            oldBounds = new Rectangle(widget.getLocation(), widget.getSize());
            redo();
        }

        @Override
        public void redo() {
            widget.setLocation(newBounds.x, newBounds.y);
            if(newBounds.getSize().getArea() == oldBounds.getSize().getArea())
                return;
            if(isSquareSizeRequired(widget)){
                if(newBounds.getSize().getArea() > oldBounds.getSize().getArea())
                    newBounds.width = Math.max(newBounds.width, getWidthFromHeight(newBounds.height, widget));
                else
                    newBounds.width = Math.min(newBounds.width, getWidthFromHeight(newBounds.height, widget));

                if(newBounds.width < getMinimumWidth())
                    newBounds.width = getMinimumWidth();
                newBounds.height = getHeightFromWidth(newBounds.width, widget);
            }
            widget.setSize(newBounds.width, newBounds.height);
        }

        @Override
        public void undo() {
            widget.setLocation(oldBounds.x, oldBounds.y);
            widget.setSize(oldBounds.width, oldBounds.height);
        }

    }

    class InitialSquareBoundsCommand extends Command{
        /** Stores the new size and location. */
        private final Rectangle newBounds;
        /** Stores the old size and location. */
        private Rectangle oldBounds;


        private final AbstractWidgetModel widget;
        public InitialSquareBoundsCommand(AbstractWidgetModel widgetModel,
                Rectangle newBounds) {
            if (widgetModel == null || newBounds == null)
                throw new IllegalArgumentException();
            this.widget = widgetModel;
            this.newBounds = newBounds;

        }


        @Override
        public void execute() {
            oldBounds = new Rectangle(widget.getLocation(), widget.getSize());
            redo();
        }

        @Override
        public void redo() {
            widget.setLocation(newBounds.x, newBounds.y);
            if(isSquareSizeRequired(widget)){
                if(newBounds.width <=0 || newBounds.height <= 0)
                    newBounds.width = oldBounds.width;
                else if(newBounds.width < getMinimumWidth() && newBounds.height < getMinimumWidth())
                    newBounds.width = getMinimumWidth();
                else
                    newBounds.width = Math.max(newBounds.width, getWidthFromHeight(newBounds.height, widget));
                newBounds.height = getHeightFromWidth(newBounds.width, widget);
            }
            if(newBounds.width >0 && newBounds.height >0)
                widget.setSize(newBounds.width, newBounds.height);
        }

        @Override
        public void undo() {
            widget.setLocation(oldBounds.x, oldBounds.y);
            widget.setSize(oldBounds.width, oldBounds.height);
        }

    }

}
