package org.csstudio.sds.components.ui.internal.feedback;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
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
	 * Constructs a new DragEditPartsTracker with the given source edit part and point index.
	 * 
	 * @param owner the source edit part
	 * @param pointIndex the index of the poly point, which should be dragged
	 */
	public PolyPointDragTracker(final GraphicalEditPart owner,
			final int pointIndex) {
		super();
		setDisabledCursor(Cursors.NO);
		assert owner != null;
		assert owner.getFigure() instanceof Polyline : "owner.getFigure() instanceof Polyline"; //$NON-NLS-1$
		assert ((Polyline) owner.getFigure()).getPoints().size()>pointIndex : "((Polyline) owner.getFigure()).getPoints().size()>pointIndex"; //$NON-NLS-1$
		assert pointIndex>=0 : "pointIndex>=0"; //$NON-NLS-1$
		_owner = owner;
		_pointIndex = pointIndex;
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
	@Override
	protected Request createSourceRequest() {
		ChangeBoundsRequest request = new ChangeBoundsRequest(
				);
		request.getExtendedData().put("points", ((Polyline) _owner.getFigure()).getPoints().getCopy());
		request.setType(RequestConstants.REQ_RESIZE);
		return request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void updateSourceRequest() {
		super.updateSourceRequest();
		Point location = getLocation();
		_owner.getFigure().translateToRelative(location);
		ChangeBoundsRequest request = (ChangeBoundsRequest) getSourceRequest();

		PointList oldPoints = (PointList) request.getExtendedData().get(
				"points");
		PointList newPoints = oldPoints.getCopy();
		newPoints.setPoint(location.getCopy(), _pointIndex);
		// calculate difference
		Rectangle oldBounds = _owner.getFigure().getBounds();
		Rectangle newBounds = newPoints.getBounds();

		request.setLocation(getLocation());

		Dimension locationDiff = newBounds.getLocation().getDifference(
				oldBounds.getLocation());
		_owner.getFigure().translateToAbsolute(locationDiff);
		Dimension sizeDiff = newBounds.getSize().getDifference(
				oldBounds.getSize());
		_owner.getFigure().translateToAbsolute(sizeDiff);

		System.out.println("SIZEDIFF;" + sizeDiff);
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
}
