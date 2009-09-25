package org.csstudio.diag.diles.editpart;

import org.csstudio.diag.diles.figures.FigureFactory;
import org.eclipse.draw2d.IFigure;

public class FlipFlopPart extends ActivityPart {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createFlipFlopFigure();
	}
}
