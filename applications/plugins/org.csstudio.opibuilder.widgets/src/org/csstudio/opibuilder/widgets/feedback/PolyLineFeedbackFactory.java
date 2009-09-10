package org.csstudio.opibuilder.widgets.feedback;

import org.eclipse.draw2d.Polyline;

/**
 * Graphical feedback factory for polyline widgets.
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
