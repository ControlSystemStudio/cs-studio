package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;

public class EllipseEditpart extends AbstractWidgetEditPart {

	
	@Override
	protected IFigure doCreateFigure() {
		IFigure figure = new Ellipse();
		return figure;
	}	
	

	@Override
	protected void registerPropertyChangeHandlers() {

	}


}
