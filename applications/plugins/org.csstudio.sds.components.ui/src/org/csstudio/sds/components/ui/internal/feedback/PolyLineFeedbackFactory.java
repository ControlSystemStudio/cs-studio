package org.csstudio.sds.components.ui.internal.feedback;

import org.eclipse.draw2d.Polyline;

/**
 * Graphical feedback factory for polyline elements.
 * 
 * @author Sven Wende
 */
public final class PolyLineFeedbackFactory extends AbstractPolyFeedbackFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Polyline createFeedbackFigure() {
		return new Polyline();
	}

}
