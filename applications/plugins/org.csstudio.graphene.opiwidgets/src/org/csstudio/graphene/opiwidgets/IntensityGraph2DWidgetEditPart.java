/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.IntensityGraph2DWidget;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class IntensityGraph2DWidgetEditPart extends AbstractGraph2DWidgetEditpart<IntensityGraph2DWidgetFigure, IntensityGraph2DWidgetModel> {

	@Override
	protected IFigure doCreateFigure() {
		IntensityGraph2DWidgetFigure figure = new IntensityGraph2DWidgetFigure(this);
		configure(figure, getWidgetModel());
		return figure;
	}
	
	@Override
	protected void configure(IntensityGraph2DWidgetFigure figure, IntensityGraph2DWidgetModel model) {
		super.configure(figure, model);
		IntensityGraph2DWidget widget = figure.getSWTWidget();
		if (figure.isRunMode()) {
		}
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
	}

}
