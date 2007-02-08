package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.ui.internal.figures.SliderFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.figures.IRefreshableFigure;

/**
 * EditPart controller for <code>RectangleElement</code> elements.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class SliderEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		return new SliderFigure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure figure) {
		SliderFigure rectangle = (SliderFigure) figure;
		
		return false;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// TODO Auto-generated method stub
		
	}
}
