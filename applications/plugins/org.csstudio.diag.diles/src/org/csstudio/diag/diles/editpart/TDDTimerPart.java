package org.csstudio.diag.diles.editpart;

import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.figures.TDDTimerFigure;
import org.csstudio.diag.diles.model.TDDTimer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;

public class TDDTimerPart extends ActivityPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createTDDTimerFigure();
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

		int seconds = ((TDDTimer) getActivity()).getDelay();
		((TDDTimerFigure) getActivityFigure()).setDelay(seconds);
		((TDDTimerFigure) getActivityFigure()).repaint();

	}
}
