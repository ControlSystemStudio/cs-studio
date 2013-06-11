/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class BubbleGraph2DWidgetEditPart extends AbstractPointDatasetGraph2DWidgetEditpart<BubbleGraph2DWidgetFigure, BubbleGraph2DWidgetModel> {

	@Override
	protected IFigure doCreateFigure() {
		BubbleGraph2DWidgetFigure figure = new BubbleGraph2DWidgetFigure(this);
		configure(figure, getWidgetModel());
		return figure;
	}

}
