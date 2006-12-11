package org.csstudio.sds.components.ui.internal.feedback;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.components.internal.model.PolygonElement;
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
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
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
			final IFigure feedbackFigure, final ChangeBoundsRequest request) {
		RectangleWithPolygonFigure figure = (RectangleWithPolygonFigure) feedbackFigure;

		figure.translateToRelative(targetBounds);

		PointList points = (PointList) request.getExtendedData().get("points");

		if (points == null) {
			points = figure.getPoints();
		}
		points = PointListHelper.scaleTo(points.getCopy(), targetBounds);
		figure.setPoints(points);
	}

	/**
	 * {@inheritDoc}
	 */
	public Shape createSizeOnDropFeedback(final CreateRequest createRequest) {
		assert createRequest != null;
		Polygon polygon = new Polygon();

		PointList points = (PointList) createRequest.getExtendedData().get(
				"points");

		assert points != null;

		polygon.setPoints(points);

		return polygon;
	}

	/**
	 * {@inheritDoc}
	 */
	public void showSizeOnDropFeedback(final CreateRequest createRequest,
			final IFigure feedbackFigure, final Insets insets) {
		assert createRequest != null;
		assert feedbackFigure instanceof Polygon : "feedbackFigure instanceof Polygon";
		Polygon polygon = (Polygon) feedbackFigure;

		PointList points = (PointList) createRequest.getExtendedData().get(
				"points");
		polygon.setPoints(points);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getCreationTool() {
		return PointListCreationTool.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public Command createInitialBoundsCommand(
			final DisplayModelElement modelElement,
			final CreateRequest request, final Rectangle bounds) {
		assert modelElement instanceof PolygonElement : "modelElement instanceof PolygonElement"; //$NON-NLS-1$
		assert request != null;
		assert bounds != null;

		Command command = null;

		PolygonElement polygonElement = (PolygonElement) modelElement;
		PointList points = (PointList) request.getExtendedData().get("points");
		PointList scaled = PointListHelper.scaleTo(points, bounds);
		if (points != null) {
			command = new ChangePolygonPointsCommand(polygonElement, scaled);
			// polylineElement.setPoints(scaled);
		}

		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	public Command createChangeBoundsCommand(final DisplayModelElement modelElement,
			final ChangeBoundsRequest request, final Rectangle targetBounds) {
		assert modelElement instanceof PolygonElement : "modelElement instanceof PolygonElement"; //$NON-NLS-1$

		PolygonElement polygonElement = (PolygonElement) modelElement;
		PointList points = (PointList) request.getExtendedData().get("points");

		if (points == null) {
			points = PointListHelper.scaleTo(polygonElement.getPoints(),
					targetBounds);
		}

		return new ChangePolygonPointsCommand(polygonElement, points);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Handle> createCustomHandles(final GraphicalEditPart hostEP) {
		assert hostEP != null;
		assert hostEP.getModel() instanceof PolygonElement : "hostEP.getModel() instanceof PolygonElement"; //$NON-NLS-1$
		List<Handle> handles = new ArrayList<Handle>();

		PolygonElement polygonElement = (PolygonElement) hostEP.getModel();

		int pointCount = polygonElement.getPoints().size();

		for (int i = 0; i < pointCount; i++) {
			PolyPointHandle myHandle = new PolyPointHandle(hostEP, i);
			handles.add(myHandle);
		}

		return handles;
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

		/**
		 * Gets the point list for the polygon part of this figure.
		 * 
		 * @return a point list
		 */
		public PointList getPoints() {
			return _polygon.getPoints();
		}

		/**
		 * Sets the point list for the polygon part.
		 * 
		 * @param points
		 *            the point list
		 */
		public void setPoints(final PointList points) {
			_polygon.setPoints(points);
			setBounds(points.getBounds());
		}

	}

}
