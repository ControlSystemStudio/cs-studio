/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
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

	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				configure(getFigure(), getWidgetModel());
				return false;
			}
		};
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_DATA_FORMULA, reconfigure);
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_X_FORMULA, reconfigure);
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_Y_FORMULA, reconfigure);
//		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_TOOLTIP_FORMULA, reconfigure);
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.CONFIGURABLE,
				reconfigure);
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_RESIZABLE_AXIS,
				reconfigure);

	}

}
