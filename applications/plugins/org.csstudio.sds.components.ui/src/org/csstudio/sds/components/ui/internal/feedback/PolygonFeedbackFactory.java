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
		Polygon r = new Polygon();
		PolygonElement polygonElement = (PolygonElement) model;
		r.setPoints(polygonElement.getPoints());
		FigureUtilities.makeGhostShape(r);
		r.setLineStyle(Graphics.LINE_DOT);
		r.setForegroundColor(ColorConstants.white);
		r.setBackgroundColor(ColorConstants.red);
		r.setBounds(initalBounds);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public void showChangeBoundsFeedback(final PrecisionRectangle targetBounds,
			final IFigure feedbackFigure) {
		Polygon polygon = (Polygon) feedbackFigure;
		PointList pointsOld = polygon.getPoints();

		polygon.repaint();

		PointList pointsNew = PointListHelper.moveToLocation(pointsOld,
				targetBounds.x, targetBounds.y);
		pointsNew = PointListHelper.moveToSize(pointsNew, targetBounds.width,
				targetBounds.height);
		polygon.setPoints(pointsNew);
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

	public Class getCreationTool(String typeId) {
		return PolygonCreationTool.class;
	}
}
