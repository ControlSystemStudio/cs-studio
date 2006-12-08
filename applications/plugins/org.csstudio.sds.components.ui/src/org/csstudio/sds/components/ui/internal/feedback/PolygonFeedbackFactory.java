package org.csstudio.sds.components.ui.internal.feedback;

import org.csstudio.sds.components.internal.model.PointListHelper;
import org.csstudio.sds.components.internal.model.PolygonElement;
import org.csstudio.sds.components.ui.internal.feedback.PolygonCreationTool.PolygonRequest;
import org.csstudio.sds.model.DisplayModelElement;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Graphical feedback factory for polygon elements.
 * 
 * @author Sven Wende
 */
public final class PolygonFeedbackFactory implements IGraphicalFeedbackFactory {
	/**
	 * {@inheritDoc}
	 */
	public IFigure createDragSourceFeedbackFigure(
			final DisplayModelElement model, final Rectangle initalBounds) {
		PolygonElement polygonElement = (PolygonElement) model;
		PointList points = polygonElement.getPoints();
		RectangleWithPolygonFigure r = new RectangleWithPolygonFigure(points);
		FigureUtilities.makeGhostShape(r);
		r.setLineStyle(Graphics.LINE_DOT);
		r.setForegroundColor(ColorConstants.white);
		r.setBounds(initalBounds);

		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public void showChangeBoundsFeedback(final PrecisionRectangle targetBounds,
			final IFigure feedbackFigure) {
		feedbackFigure.translateToRelative(targetBounds);
		feedbackFigure.setBounds(targetBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	public Shape createSizeOnDropFeedback(final CreateRequest createRequest) {
		assert createRequest instanceof PolygonRequest : "createRequest instanceof PolygonRequest";
		Polygon polygon = new Polygon();

		PointList points = ((PolygonRequest) createRequest).getPoints();

		assert points != null;

		polygon.setPoints(points);

		return polygon;
	}

	/**
	 * {@inheritDoc}
	 */
	public void showSizeOnDropFeedback(final CreateRequest createRequest,
			final IFigure feedbackFigure, final Insets insets) {
		assert createRequest instanceof PolygonRequest : "createRequest instanceof PolygonRequest";
		assert feedbackFigure instanceof Polygon : "feedbackFigure instanceof Polygon";
		Polygon polygon = (Polygon) feedbackFigure;
		PolygonRequest polygonRequest = (PolygonRequest) createRequest;

		PointList points = polygonRequest.getPoints();
		polygon.setPoints(points);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getCreationTool() {
		return PolygonCreationTool.class;
	}

	/**
	 * Custom feedback figure for polygons. The figure shows a rectangle, which
	 * does also include the shape of the polygon.
	 * 
	 * @author Sven Wende
	 * 
	 */
	class RectangleWithPolygonFigure extends RectangleFigure {
		/**
		 * The "included" polygon.
		 */
		private Polygon _polygon;

		/**
		 * Constructor.
		 * 
		 * @param points
		 *            the polygon points
		 */
		public RectangleWithPolygonFigure(final PointList points) {
			_polygon = new Polygon();

			FigureUtilities.makeGhostShape(_polygon);
			_polygon.setLineStyle(Graphics.LINE_DOT);
			_polygon.setForegroundColor(ColorConstants.white);

			_polygon.setPoints(points.getCopy());
			add(_polygon);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setBounds(final Rectangle rect) {
			super.setBounds(rect);
			PointList points = _polygon.getPoints();
			PointList newPoints = PointListHelper.scaleTo(points, rect
					.getCopy());

			_polygon.setPoints(newPoints);

		}

	}
}
