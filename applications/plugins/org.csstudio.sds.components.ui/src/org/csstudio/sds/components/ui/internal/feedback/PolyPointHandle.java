package org.csstudio.sds.components.ui.internal.feedback;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.SquareHandle;
import org.eclipse.swt.graphics.Color;

/**
 * A handle, used to move points of a polyline or polygon.
 * 
 * @author Sven Wende
 * 
 */
public final class PolyPointHandle extends SquareHandle {
	/**
	 * Index of the polygon point, that should be moved.
	 */
	private int _pointIndex;

	/**
	 * Creates a new Handle for the given GraphicalEditPart.
	 * 
	 * @param owner
	 *            owner of the ResizeHandle
	 * @param pointIndex
	 *            index of the polygon point, that should be moved
	 */
	public PolyPointHandle(final GraphicalEditPart owner, final int pointIndex) {
		super();
		_pointIndex = pointIndex;
		setOwner(owner);

		PolyPointLocator locator = new PolyPointLocator((Polyline) owner
				.getFigure(), pointIndex);
		setLocator(locator);

		setCursor(Cursors.CROSS);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DragTracker createDragTracker() {
		return new PolyPointDragTracker(getOwner(), _pointIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Color getBorderColor() {
		return ColorConstants.darkGray;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Color getFillColor() {
		return ColorConstants.yellow;
	}

}
