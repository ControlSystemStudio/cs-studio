package org.csstudio.sds.components.ui.internal.feedback;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.tools.SimpleDragTracker;

/**
 * A drag tracker, that moves points of polygons or polylines.
 * 
 * @author Sven Wende
 * 
 */
public final class PolyPointDragTracker extends SimpleDragTracker {
	/**
	 * The source edit part.
	 */
	private GraphicalEditPart _owner;

	/**
	 * The index of the poly point, which should be dragged.
	 */
	private int _pointIndex;

	/**
	 * A snap helper.
	 */
	private SnapToHelper _snapToHelper;

	/**
	 * The initial point list, when a drag operation starts. Used to calculate
	 * deviations for feedback.
	 */
	private PointList _oldPoints;

	/**
	 * The request, which is currently used to communicate with the edit part.
	 */
	private Request _sourceRequest;

	/**
	 * Constructs a new DragEditPartsTracker with the given source edit part and
	 * point index.
	 * 
	 * @param owner
	 *            the source edit part
	 * @param pointIndex
	 *            the index of the poly point, which should be dragged
	 */
	public PolyPointDragTracker(final GraphicalEditPart owner,
			final int pointIndex) {
		super();
		setDisabledCursor(Cursors.NO);
		assert owner != null;
		assert owner.getFigure() instanceof Polyline : "owner.getFigure() instanceof Polyline"; //$NON-NLS-1$
		assert ((Polyline) owner.getFigure()).getPoints().size() > pointIndex : "((Polyline) owner.getFigure()).getPoints().size()>pointIndex"; //$NON-NLS-1$
		assert pointIndex >= 0 : "pointIndex>=0"; //$NON-NLS-1$
		_owner = owner;
		_pointIndex = pointIndex;
		_oldPoints = ((Polyline) _owner.getFigure()).getPoints().getCopy();

		if (getTargetEditPart() != null) {
			_snapToHelper = (SnapToHelper) getTargetEditPart().getAdapter(
					SnapToHelper.class);
		}
	}

	/**
	 * The TargetEditPart is the parent of the EditPart being resized.
	 * 
	 * @return The target EditPart; may be <code>null</code> in 2.1
	 *         applications that use the now deprecated
	 *         {@link ResizeTracker#ResizeTracker(int) constructor}.
	 */
	protected GraphicalEditPart getTargetEditPart() {
		if (_owner != null) {
			return (GraphicalEditPart) _owner.getParent();
		}
		return null;
	}

	/**
	 * Gets the source bounds of the owners figure.
	 * 
	 * @return the source bounds
	 */
	protected PrecisionRectangle getSourceBounds() {
		PrecisionRectangle sourceRect;

		IFigure figure = _owner.getFigure();
		if (figure instanceof HandleBounds) {
			sourceRect = new PrecisionRectangle(((HandleBounds) figure)
					.getHandleBounds());
		} else {
			sourceRect = new PrecisionRectangle(figure.getBounds());
		}
		figure.translateToAbsolute(sourceRect);
		return sourceRect;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getCommandName() {
		return RequestConstants.REQ_RESIZE;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Request createSourceRequest() {
		ChangeBoundsRequest request = new ChangeBoundsRequest();
		// TODO: swende: ugly

		PointList points = ((AbstractPolyElement) _owner.getModel())
				.getPoints();

		request.getExtendedData().put(AbstractPolyFeedbackFactory.PROP_POINTS, points.getCopy());
		request.setType(RequestConstants.REQ_RESIZE);

		_oldPoints = points.getCopy();
		return request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void updateSourceRequest() {
		super.updateSourceRequest();
		ChangeBoundsRequest request = (ChangeBoundsRequest) getSourceRequest();

		PrecisionPoint location = new PrecisionPoint(getLocation().x,
				getLocation().y);

		if (_snapToHelper != null) {
			_snapToHelper.snapPoint(request, PositionConstants.NORTH_WEST,
					new PrecisionPoint(getLocation().x, getLocation().y),
					location);
		}

		_owner.getFigure().translateToRelative(location);

		PointList oldPoints = ((PointList) request.getExtendedData().get(
				"points")).getCopy();
		PointList newPoints = oldPoints.getCopy();
		newPoints.setPoint(location.getCopy(), _pointIndex);
		// calculate difference
		Rectangle oldBounds = _oldPoints.getBounds();
		Rectangle newBounds = newPoints.getBounds();

		request.setLocation(getLocation());

		Dimension locationDiff = newBounds.getLocation().getDifference(
				oldBounds.getLocation());
		_owner.getFigure().translateToAbsolute(locationDiff);
		Dimension sizeDiff = newBounds.getSize().getDifference(
				oldBounds.getSize());
		_owner.getFigure().translateToAbsolute(sizeDiff);

		request
				.setMoveDelta(new Point(locationDiff.width, locationDiff.height));
		request.setSizeDelta(sizeDiff);

		request.getExtendedData().put("points", newPoints);
		request.getExtendedData().put("pointIndex", _pointIndex);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getCommand() {
		if (_owner == null) {
			return null;
		}
		return _owner.getCommand(getSourceRequest());
	}

	/**
	 * Returns the request for the source of the drag, creating it if necessary.
	 * 
	 * @return the source request
	 */
	@Override
	protected Request getSourceRequest() {

		if (_sourceRequest == null) {
			_sourceRequest = createSourceRequest();
		}
		return _sourceRequest;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void performDrag() {
		super.performDrag();
		resetRequestState();
	}

	/**
	 * Clears and resets the state of the tracker.
	 */
	private void resetRequestState() {
		_oldPoints = ((AbstractPolyElement) _owner.getModel()).getPoints()
				.getCopy();
		_sourceRequest = null;
	}
}
