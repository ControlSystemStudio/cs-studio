/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class HistogramGraph2DWidgetEditPart extends AbstractPointDatasetGraph2DWidgetEditpart<HistogramGraph2DWidgetFigure, HistogramGraph2DWidgetModel> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.opibuilder.editparts.AbstractBaseEditPart#doCreateFigure()
	 */
	@Override
	protected IFigure doCreateFigure() {
		HistogramGraph2DWidgetFigure figure = new HistogramGraph2DWidgetFigure(this);
		configure(figure, getWidgetModel());
		return figure;
	}

}
