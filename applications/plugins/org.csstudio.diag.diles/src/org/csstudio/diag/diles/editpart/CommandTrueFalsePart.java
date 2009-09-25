package org.csstudio.diag.diles.editpart;

import org.csstudio.diag.diles.figures.CommandTrueFalseFigure;
import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.model.Logic;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class CommandTrueFalsePart extends ActivityPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createCommandTrueFalseFigure();
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
			((CommandTrueFalseFigure) getActivityFigure())
					.setBackgroundColor(new Color(Display.getDefault(), 128,
							185, 255));
			((CommandTrueFalseFigure) getActivityFigure()).setText("TRUE");
			((CommandTrueFalseFigure) getActivityFigure()).setVisualStatus(1);
			((CommandTrueFalseFigure) getActivityFigure()).repaint();
			refreshSourceConnections();
			refreshTargetConnections();
		} else if (result == false) {
			((CommandTrueFalseFigure) getActivityFigure())
					.setBackgroundColor(new Color(Display.getDefault(), 255,
							90, 90));
			((CommandTrueFalseFigure) getActivityFigure()).setText("FALSE");
			((CommandTrueFalseFigure) getActivityFigure()).setVisualStatus(0);
			((CommandTrueFalseFigure) getActivityFigure()).repaint();
			refreshSourceConnections();
			refreshTargetConnections();
		}
	}
}
