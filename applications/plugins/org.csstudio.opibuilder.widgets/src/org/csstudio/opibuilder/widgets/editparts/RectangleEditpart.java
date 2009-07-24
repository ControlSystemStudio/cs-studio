package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditpart;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;

public class RectangleEditpart extends AbstractWidgetEditpart {

	
	@Override
	protected IFigure createFigure() {
		IFigure figure = new RectangleFigure();
		return figure;
	}	
	

	@Override
	protected void registerPropertyChangeHandlers() {

	}


}
