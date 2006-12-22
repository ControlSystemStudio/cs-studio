package org.csstudio.sds.components.ui.internal.feedback;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.components.internal.model.AbstractPolyElement;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
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
abstract class AbstractPolyFeedbackFactory implements
		IGraphicalFeedbackFactory {
	/**
	 * An identifier which is used as key for extended data in request objects.
	 */
	public static final String PROP_POINTS = "points";

	/**
	 * Subclasses should return an appropriate feedback figure. This basically
	 * supports code inheritance for the polyline and polygon implementations.
	 * 
	 * @return a polyline or polygon figure which is used for graphical feedback
	 */
	protected abstract Polyline createFeedbackFigure();

	/**
	 * {@inheritDoc}
	 */
	public final IFigure createDragSourceFeedbackFigure(
			final AbstractElementModel model, final Rectangle initalBounds) {
		assert model != null;
		assert model instanceof AbstractPolyElement : "model instanceof AbstractPolyElement"; //$NON-NLS-1$
		assert initalBounds != null;

		// get the points from the model
		AbstractPolyElement abstractPolyElement = (AbstractPolyElement) model;
		PointList points = abstractPolyElement.getPoints();

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
	public final void showChangeBoundsFeedback(
			final AbstractElementModel model, final PrecisionRectangle bounds,
			final IFigure feedbackFigure, final ChangeBoundsRequest request) {
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
		PointList points = (PointList) request.getExtendedData().get(
				PROP_POINTS);

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
	public final Shape createSizeOnDropFeedback(
			final CreateRequest createRequest) {
		assert createRequest != null;

		// Polyline polyline = new Polyline();

		// the request should contain a point list, because the creation is done
		// by a special creation tool
		PointList points = (PointList) createRequest.getExtendedData().get(
				PROP_POINTS);

		assert points != null;

		// polyline.setPoints(points);

		Polyline feedbackFigure = createFeedbackFigure();
		feedbackFigure.setPoints(points);

		return feedbackFigure;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void showSizeOnDropFeedback(final CreateRequest createRequest,
			final IFigure feedbackFigure, final Insets insets) {
		assert createRequest != null;
		assert feedbackFigure instanceof Polyline : "feedbackFigure instanceof Polyline";
		Polyline polyline = (Polyline) feedbackFigure;

		// the request should contain a point list, because the creation is done
		// by a special creation tool
		PointList points = ((PointList) createRequest.getExtendedData().get(
				PROP_POINTS)).getCopy();

		assert points != null;

		// the points are viewer relative and need to be translated to reflect
		// the zoom level, scrollbar occurence etc.
		polyline.translateToRelative(points);

		polyline.setPoints(points);

	}

	/**
	 * {@inheritDoc}
	 */
	public final Class getCreationTool() {
		return PointListCreationTool.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Command createInitialBoundsCommand(
			final AbstractElementModel elementModel,
			final CreateRequest request, final Rectangle bounds) {
		assert elementModel instanceof AbstractPolyElement : "modelElement instanceof AbstractPolyElement"; //$NON-NLS-1$
		assert request != null;
		assert bounds != null;

		AbstractPolyElement abstractPolyElement = (AbstractPolyElement) elementModel;

		PointList points = (PointList) request.getExtendedData().get(
				PROP_POINTS);
		assert points != null;

		// the points are viewer relative and need to be translated to the
		// specified bounds, to reflect zoom level, scrollbar occurence etc.
		PointList scaledPoints = PointListHelper.scaleTo(points, bounds);

		return new ChangePolyPointsCommand(abstractPolyElement, scaledPoints);
	}

	/**
	 * {@inheritDoc}
	 */
	public final Command createChangeBoundsCommand(
			final AbstractElementModel model,
			final ChangeBoundsRequest request, final Rectangle targetBounds) {
		assert model instanceof AbstractPolyElement : "model instanceof AbstractPolyElement"; //$NON-NLS-1$

		AbstractPolyElement abstractPolyElement = (AbstractPolyElement) model;

		// try to get a point list from the request (this happens only, when
		// poly point handles are dragged arround)
		PointList points = (PointList) request.getExtendedData().get(
				PROP_POINTS);

		// otherwise take the points from the model
		if (points == null) {
			points = ((AbstractPolyElement) model).getPoints();
		}

		assert points != null;

		// the points are viewer relative and need to be translated to the
		// specified bounds, to reflect zoom level, scrollbar occurence etc.
		points = PointListHelper.scaleTo(points, targetBounds);

		return new ChangePolyPointsCommand(abstractPolyElement, points);
	}

	/**
	 * {@inheritDoc}
	 */
	public final List<Handle> createCustomHandles(final GraphicalEditPart hostEP) {
		assert hostEP != null;
		assert hostEP.getModel() instanceof AbstractPolyElement : "hostEP.getModel() instanceof AbstractPolyElement"; //$NON-NLS-1$

		// create some custom handles, which enable the user to drag arround
		// single points of the polyline
		List<Handle> handles = new ArrayList<Handle>();

		AbstractPolyElement abstractPolyElement = (AbstractPolyElement) hostEP
				.getModel();

		int pointCount = abstractPolyElement.getPoints().size();

		for (int i = 0; i < pointCount; i++) {
			PolyPointHandle myHandle = new PolyPointHandle(hostEP, i);
			handles.add(myHandle);
		}

		return handles;
	}

}
