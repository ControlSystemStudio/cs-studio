package org.csstudio.sds.components.ui.internal.feedback;

import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.Polyline;

/**
 * Graphical feedback factory for polygon elements.
 * 
 * @author Sven Wende
 */
public final class PolygonFeedbackFactory extends AbstractPolyFeedbackFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Polyline createFeedbackFigure() {
		return new Polygon();
	}

}
