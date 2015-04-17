/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.opibuilder.widgets.model.AbstractPolyModel;
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
import org.eclipse.gef.tools.ResizeTracker;
import org.eclipse.gef.tools.SimpleDragTracker;

/**
 * A drag tracker, that moves points of polygons or polylines.
 * 
 * @author Sven Wende
 * 
 */
public final class PolyPointDragTracker extends SimpleDragTracker {
	/**
	 * Extended data key "pointIndex".
	 */
	private static final String EXT_DATA_POINT_INDEX = "pointIndex"; //$NON-NLS-1$

	/**
	 * Extended data key "points".
	 */
	private static final String EXT_DATA_POINTS = "points"; //$NON-NLS-1$

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

		request.setEditParts(getTargetEditPart());
		PointList points = ((AbstractPolyModel) _owner.getModel())
				.getPoints();

		request.getExtendedData().put(AbstractPolyFeedbackFactory.PROP_POINTS,
				points.getCopy());
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
				EXT_DATA_POINTS)).getCopy();
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

		request.getExtendedData().put(EXT_DATA_POINTS, newPoints);
		request.getExtendedData().put(EXT_DATA_POINT_INDEX, _pointIndex);
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
		_oldPoints = ((AbstractPolyModel) _owner.getModel()).getPoints().getCopy();
		_sourceRequest = null;
	}
}
