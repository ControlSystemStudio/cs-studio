package org.csstudio.sds.components.ui.internal.feedback;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.components.internal.model.AbstractPolyElement;
import org.csstudio.sds.model.DisplayModelElement;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
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
public abstract class AbstractPolyFeedbackFactory implements
		IGraphicalFeedbackFactory {
	protected abstract Polyline createFeedbackFigure();

	/**
	 * {@inheritDoc}
	 */
	public IFigure createDragSourceFeedbackFigure(
			final DisplayModelElement model, final Rectangle initalBounds) {
		assert model != null;
		assert model instanceof AbstractPolyElement : "model instanceof AbstractPolyElement"; //$NON-NLS-1$
		assert initalBounds != null;

		// get the points from the model
		AbstractPolyElement AbstractPolyElement = (AbstractPolyElement) model;
		PointList points = AbstractPolyElement.getPoints();

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
	public void showChangeBoundsFeedback(final DisplayModelElement model,
			final PrecisionRectangle bounds, final IFigure feedbackFigure,
			final ChangeBoundsRequest request) {
		assert model != null;
		assert model instanceof AbstractPolyElement : "model instanceof AbstractPolyElement"; //$NON-NLS-1$
		assert bounds != null;
		assert feedbackFigure != null;
		assert feedbackFigure instanceof PolyFeedbackFigureWithRectangle : "feedbackFigure instanceof AbstractPolyFeedbackFigure"; //$NON-NLS-1$
		assert request != null;

		PolyFeedbackFigureWithRectangle figure = (PolyFeedbackFigureWithRectangle) feedbackFigure;

		figure.translateToRelative(bounds);

		// try to get a point list from the request (this happens only, when
		// poly point handles are dragged arround)
		PointList points = (PointList) request.getExtendedData().get("points");

		// otherwise take the points from the model
		if (points == null) {
			points = ((AbstractPolyElement) model).getPoints();
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
	public Shape createSizeOnDropFeedback(final CreateRequest createRequest) {
		assert createRequest != null;

		// Polyline polyline = new Polyline();

		// the request should contain a point list, because the creation is done
		// by a special creation tool
		PointList points = (PointList) createRequest.getExtendedData().get(
				"points");

		assert points != null;

		// polyline.setPoints(points);

		Polyline feedbackFigure = createFeedbackFigure();
		feedbackFigure.setPoints(points);
		
		return feedbackFigure;
	}

	/**
	 * {@inheritDoc}
	 */
	public void showSizeOnDropFeedback(final CreateRequest createRequest,
			final IFigure feedbackFigure, final Insets insets) {
		assert createRequest != null;
		assert feedbackFigure instanceof Polyline : "feedbackFigure instanceof Polyline";
		Polyline polyline = (Polyline) feedbackFigure;

		// the request should contain a point list, because the creation is done
		// by a special creation tool
		PointList points = ((PointList) createRequest.getExtendedData().get(
				"points")).getCopy();

		assert points != null;

		// the points are viewer relative and need to be translated to reflect
		// the zoom level, scrollbar occurence etc.
		polyline.translateToRelative(points);

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
		assert modelElement instanceof AbstractPolyElement : "modelElement instanceof AbstractPolyElement"; //$NON-NLS-1$
		assert request != null;
		assert bounds != null;

		AbstractPolyElement AbstractPolyElement = (AbstractPolyElement) modelElement;

		PointList points = (PointList) request.getExtendedData().get("points");
		assert points != null;

		// the points are viewer relative and need to be translated to the
		// specified bounds, to reflect zoom level, scrollbar occurence etc.
		PointList scaledPoints = PointListHelper.scaleTo(points, bounds);

		return new ChangePolyPointsCommand(AbstractPolyElement, scaledPoints);
	}

	/**
	 * {@inheritDoc}
	 */
	public Command createChangeBoundsCommand(final DisplayModelElement model,
			final ChangeBoundsRequest request, final Rectangle targetBounds) {
		assert model instanceof AbstractPolyElement : "model instanceof AbstractPolyElement"; //$NON-NLS-1$

		AbstractPolyElement AbstractPolyElement = (AbstractPolyElement) model;

		// try to get a point list from the request (this happens only, when
		// poly point handles are dragged arround)
		PointList points = (PointList) request.getExtendedData().get("points");

		// otherwise take the points from the model
		if (points == null) {
			points = ((AbstractPolyElement) model).getPoints();
		}

		assert points != null;

		// the points are viewer relative and need to be translated to the
		// specified bounds, to reflect zoom level, scrollbar occurence etc.
		points = PointListHelper.scaleTo(points, targetBounds);

		return new ChangePolyPointsCommand(AbstractPolyElement, points);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Handle> createCustomHandles(final GraphicalEditPart hostEP) {
		assert hostEP != null;
		assert hostEP.getModel() instanceof AbstractPolyElement : "hostEP.getModel() instanceof AbstractPolyElement"; //$NON-NLS-1$

		// create some custom handles, which enable the user to drag arround
		// single points of the polyline
		List<Handle> handles = new ArrayList<Handle>();

		AbstractPolyElement AbstractPolyElement = (AbstractPolyElement) hostEP
				.getModel();

		int pointCount = AbstractPolyElement.getPoints().size();

		for (int i = 0; i < pointCount; i++) {
			PolyPointHandle myHandle = new PolyPointHandle(hostEP, i);
			handles.add(myHandle);
		}

		return handles;
	}

	// /**
	// * Custom feedback figure for polyglines. The figure shows a rectangle,
	// * which does also include the shape of the polyline.
	// *
	// * @author Sven Wende
	// *
	// */
	// class RectangleWithPolyLineFigure extends RectangleFigure {
	// /**
	// * The "included" polyline.
	// */
	// private Polyline _polyLine;
	//
	// /**
	// * Constructor.
	// *
	// * @param points
	// * the polygon points
	// */
	// public RectangleWithPolyLineFigure(final PointList points) {
	// _polyLine = new Polyline();
	// add(_polyLine);
	// setPoints(points);
	// }
	//
	// /**
	// * Gets the point list for the polyline part of this figure.
	// *
	// * @return a point list
	// */
	// public PointList getPoints() {
	// return _polyLine.getPoints();
	// }
	//
	// /**
	// * Sets the point list for the polyline part.
	// *
	// * @param points
	// * the point list
	// */
	// public void setPoints(final PointList points) {
	// _polyLine.setPoints(points);
	// setBounds(points.getBounds());
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void paint(final Graphics graphics) {
	// // enable tranparency
	// graphics.setAlpha(120);
	// super.paint(graphics);
	// }
	//
	// }

}
