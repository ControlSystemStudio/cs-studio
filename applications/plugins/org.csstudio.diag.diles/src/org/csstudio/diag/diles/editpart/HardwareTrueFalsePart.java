package org.csstudio.diag.diles.editpart;

import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.figures.HardwareTrueFalseFigure;
import org.csstudio.diag.diles.model.Logic;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Color;

public class HardwareTrueFalsePart extends ActivityPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createTrueFigure();
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

		boolean result = ((Logic) getActivity()).getResult();
		if (result == true) {
			((HardwareTrueFalseFigure) getActivityFigure())
					.setBackgroundColor(new Color(null, 159, 203, 255));
			((HardwareTrueFalseFigure) getActivityFigure()).repaint();
		} else if (result == false) {
			((HardwareTrueFalseFigure) getActivityFigure())
					.setBackgroundColor(new Color(null, 255, 140, 140));
			((HardwareTrueFalseFigure) getActivityFigure()).repaint();
		}

	}
}
