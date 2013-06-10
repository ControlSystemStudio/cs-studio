/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.BubbleGraph2DWidget;
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
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(BubbleGraph2DWidget widget,
			BubbleGraph2DWidgetModel model, boolean runMode) {
		if (runMode) {
			widget.setDataFormula(model.getDataFormula());
			widget.setXColumnFormula(model.getXColumnFormula());
			widget.setYColumnFormula(model.getYColumnFormula());
//			widget.setTooltipColumnFormula(model.getTooltipFormula());
			widget.setResizableAxis(model.isResizableAxis());
			//widget.setConfigurable(model.isConfigurable());
		}
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				configure(getFigure().getSWTWidget(), getWidgetModel(),
						getFigure().isRunMode());
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
