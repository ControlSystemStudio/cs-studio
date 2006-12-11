package org.csstudio.sds.components.ui.internal.feedback;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
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
			final IFigure feedbackFigure, final ChangeBoundsRequest request) {

		RectangleWithPolyLineFigure figure = (RectangleWithPolyLineFigure) feedbackFigure;

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
		Polyline polyline = new Polyline();

		PointList points = (PointList) createRequest.getExtendedData().get(
				"points");

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
		assert createRequest != null;
		assert feedbackFigure instanceof Polyline : "feedbackFigure instanceof Polyline";
		Polyline polyline = (Polyline) feedbackFigure;

		PointList points = (PointList) createRequest.getExtendedData().get(
				"points");
		polyline.setPoints(points);
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
		assert modelElement instanceof PolylineElement : "modelElement instanceof PolylineElement"; //$NON-NLS-1$
		assert request != null;
		assert bounds != null;
		
		Command command = null;

		PolylineElement polylineElement = (PolylineElement) modelElement;
		PointList points = (PointList) request.getExtendedData().get("points");
		PointList scaled = PointListHelper.scaleTo(points, bounds);
		if (points != null) {
			command = new ChangePolylinePointsCommand(polylineElement, scaled);
			// polylineElement.setPoints(scaled);
		}

		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	public Command createChangeBoundsCommand(final DisplayModelElement modelElement,
			final ChangeBoundsRequest request, final Rectangle targetBounds) {
		assert modelElement instanceof PolylineElement : "modelElement instanceof PolylineElement"; //$NON-NLS-1$

		PolylineElement polylineElement = (PolylineElement) modelElement;
		PointList points = (PointList) request.getExtendedData().get("points");

		if (points == null) {
			points = PointListHelper.scaleTo(polylineElement.getPoints(),
					targetBounds);
		}

		return new ChangePolylinePointsCommand(polylineElement, points);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Handle> createCustomHandles(final GraphicalEditPart hostEP) {
		assert hostEP != null;
		assert hostEP.getModel() instanceof PolylineElement : "hostEP.getModel() instanceof PolylineElement"; //$NON-NLS-1$
		List<Handle> handles = new ArrayList<Handle>();

		PolylineElement polylineElement = (PolylineElement) hostEP.getModel();

		int pointCount = polylineElement.getPoints().size();

		for (int i = 0; i < pointCount; i++) {
			PolyPointHandle myHandle = new PolyPointHandle(hostEP, i);
			handles.add(myHandle);
		}

		return handles;
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
		}

		/**
		 * Gets the point list for the polyline part of this figure.
		 * 
		 * @return a point list
		 */
		public PointList getPoints() {
			return _polyLine.getPoints();
		}

		/**
		 * Sets the point list for the polyline part.
		 * 
		 * @param points
		 *            the point list
		 */
		public void setPoints(final PointList points) {
			_polyLine.setPoints(points);
			setBounds(points.getBounds());
		}

	}

}
