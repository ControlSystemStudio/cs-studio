package org.csstudio.sds.components.ui.internal.feedback;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

/**
 * 
 * Places a handle for a polygon or polyline point. The placement is determined by
 * indicating the polyline figure to which the placement is relative, and a value
 * indicating a point index.
 * 
 * @author Sven Wende
 */
public final class PolyPointLocator implements Locator {
	/**
	 * The reference figure.
	 */
	private Polyline _referenceFigure;

	/**
	 * Index of the point, the handle should be placed for.
	 */
	private int _pointIndex;

	/**
	 * Constructs a poly point handle locator.
	 * @param referenceFigure the reference figure ({@link Polyline} or subclasses of it)
	 * @param pointIndex the index of the polygon point for which a handle should be placed
	 */
	public PolyPointLocator(final Polyline referenceFigure, final int pointIndex) {
		assert referenceFigure != null;
		assert pointIndex>0 : "pointIndex>0"; //$NON-NLS-1$
		assert referenceFigure.getPoints().size()>pointIndex : "referenceFigure.getPoints().size()>pointIndex"; //$NON-NLS-1$
		_pointIndex = pointIndex;
		_referenceFigure = referenceFigure;
	}

	/**
	 * Returns the Reference Box in the Reference Figure's coordinate system.
	 * The returned Rectangle may be by reference, and should <b>not</b> be
	 * modified.
	 * 
	 * @return the reference box
	 * @since 2.0
	 */
	protected Rectangle getReferenceBox() {
		if (_referenceFigure instanceof HandleBounds) {
			return ((HandleBounds)_referenceFigure).getHandleBounds();
		}
		return _referenceFigure.getBounds();
	}

	/**
	 * {@inheritDoc}
	 */
	public void relocate(final IFigure target) {
		Point p = _referenceFigure.getPoints().getPoint(_pointIndex);

		_referenceFigure.translateToAbsolute(p);
		target.translateToRelative(p);
		Rectangle relativeBounds = new PrecisionRectangle(getReferenceBox()
				.getResized(-1, -1));
		Dimension targetSize = target.getPreferredSize();

		relativeBounds.x = p.x - ((targetSize.width + 1) / 2);
		relativeBounds.y = p.y - ((targetSize.height + 1) / 2);

		relativeBounds.setSize(targetSize);
		target.setBounds(relativeBounds);

	}

}
