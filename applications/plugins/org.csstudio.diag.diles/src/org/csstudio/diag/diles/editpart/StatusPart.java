package org.csstudio.diag.diles.editpart;

import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.figures.StatusFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class StatusPart extends ActivityPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new FigureFactory().createStatusFigure();
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

		boolean result = (getActivity()).getResult();
		if (result == true) {
			((StatusFigure) getActivityFigure()).setBackgroundColor(new Color(
					Display.getDefault(), 128, 185, 255));
			((StatusFigure) getActivityFigure()).setVisualStatus(1);
			((StatusFigure) getActivityFigure()).repaint();
			refreshSourceConnections();
		} else if (result == false) {
			((StatusFigure) getActivityFigure()).setBackgroundColor(new Color(
					Display.getDefault(), 255, 90, 90));
			((StatusFigure) getActivityFigure()).setVisualStatus(0);
			((StatusFigure) getActivityFigure()).repaint();
			refreshSourceConnections();
		}
	}

}
