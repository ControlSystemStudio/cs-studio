package org.csstudio.sds.components.ui.internal.feedback;

import org.csstudio.sds.components.internal.model.PointListHelper;
import org.csstudio.sds.components.internal.model.PolylineElement;
import org.csstudio.sds.model.DisplayModelElement;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Graphical feedback factory for polyline elements.
 * 
 * @author Sven Wende
 */
public final class PolyLineFeedbackFactory implements IGraphicalFeedbackFactory {
	/**
	 * {@inheritDoc}
	 */
	public IFigure createDragSourceFeedbackFigure(
			final DisplayModelElement model, final Rectangle initalBounds) {
		PolylineElement polyLineElement = (PolylineElement) model;
		PointList points = polyLineElement.getPoints();
		RectangleWithPolyLineFigure r = new RectangleWithPolyLineFigure(points);
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
		Polyline polyline = new Polyline();

		PointList points = ((PolygonRequest) createRequest).getPoints();

		assert points != null;

		polyline.setPoints(points);

		polyline.setLineWidth(4);
		return polyline;
	}

	/**
	 * {@inheritDoc}
	 */
	public void showSizeOnDropFeedback(final CreateRequest createRequest,
			final IFigure feedbackFigure, final Insets insets) {
		assert createRequest instanceof PolygonRequest : "createRequest instanceof PolygonRequest";
		assert feedbackFigure instanceof Polygon : "feedbackFigure instanceof Polygon";
		Polyline polyline = (Polyline) feedbackFigure;
		PolygonRequest polygonRequest = (PolygonRequest) createRequest;

		PointList points = polygonRequest.getPoints();
		polyline.setPoints(points);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getCreationTool() {
		return PolylineCreationTool.class;
	}

	/**
	 * Custom feedback figure for polyglines. The figure shows a rectangle,
	 * which does also include the shape of the polyline.
	 * 
	 * @author Sven Wende
	 * 
	 */
	class RectangleWithPolyLineFigure extends RectangleFigure {
		/**
		 * The "included" polyline.
		 */
		private Polyline _polyLine;

		/**
		 * Constructor.
		 * 
		 * @param points
		 *            the polygon points
		 */
		public RectangleWithPolyLineFigure(final PointList points) {
			_polyLine = new Polyline();

			FigureUtilities.makeGhostShape(_polyLine);
			_polyLine.setLineStyle(Graphics.LINE_DOT);
			_polyLine.setForegroundColor(ColorConstants.white);

			_polyLine.setPoints(points.getCopy());
			add(_polyLine);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setBounds(final Rectangle rect) {
			super.setBounds(rect);
			PointList points = _polyLine.getPoints();
			PointList newPoints = PointListHelper.scaleTo(points, rect
					.getCopy());

			_polyLine.setPoints(newPoints);

		}

	}
}
