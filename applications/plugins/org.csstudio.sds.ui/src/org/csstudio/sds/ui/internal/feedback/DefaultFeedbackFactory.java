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
	public void showChangeBoundsFeedback(final AbstractWidgetModel model, final PrecisionRectangle bounds, final IFigure feedbackFigure, final ChangeBoundsRequest request) {
		feedbackFigure.translateToRelative(bounds);
		feedbackFigure.setBounds(bounds);
	}

	/**
	 * {@inheritDoc}
	 */
	public Shape createSizeOnDropFeedback(final CreateRequest createRequest) {
		return new RectangleFigure();
	}

	/**
	 * {@inheritDoc}
	 */
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
	public Class getCreationTool() {
		return null;
	}

	/**
	 * Handles the given request.
	 * @param widgetModel
	 * 				The AbstractWidgetModel
	 * @param request
	 * 				The Request
	 */
	public void handleRequest(final AbstractWidgetModel widgetModel,
			final Request request) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
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
	public List<Handle> createCustomHandles(final GraphicalEditPart hostEP) {
		return null;
	}
}
