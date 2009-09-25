package org.csstudio.diag.diles.editpart;

import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.figures.TDETimerFigure;
import org.csstudio.diag.diles.model.TDETimer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;

public class TDETimerPart extends ActivityPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createTDETimerFigure();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.diag.diles.editpart.ActivityPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		Point loc = getActivity().getLocation();
		Dimension size = getActivity().getSize();
		Rectangle r = new Rectangle(loc, size);
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);

		int seconds = ((TDETimer) getActivity()).getDelay();
		((TDETimerFigure) getActivityFigure()).setDelay(seconds);
		((TDETimerFigure) getActivityFigure()).repaint();

	}
}
