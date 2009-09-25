package org.csstudio.diag.diles.editpart;

import org.csstudio.diag.diles.figures.AnalogInputFigure;
import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.model.AnalogInput;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Color;

public class AnalogInputPart extends ActivityPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createAnalogInputFigure();
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

		double result = ((AnalogInput) getActivity()).getDoubleResult();

		((AnalogInputFigure) getActivityFigure()).setBackgroundColor(new Color(
				null, 255, 224, 210));
		((AnalogInputFigure) getActivityFigure()).setText(String
				.valueOf(result));
		((AnalogInputFigure) getActivityFigure()).repaint();
		refreshSourceConnections();
		refreshTargetConnections();

	}

}
