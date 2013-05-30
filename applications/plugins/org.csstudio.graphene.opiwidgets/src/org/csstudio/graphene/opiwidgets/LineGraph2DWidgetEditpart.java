/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.LineGraph2DWidget;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class LineGraph2DWidgetEditpart extends AbstractPointDatasetGraph2DWidgetEditpart<LineGraph2DWidgetFigure, LineGraph2DWidgetModel> {

	@Override
	protected IFigure doCreateFigure() {
		LineGraph2DWidgetFigure figure = new LineGraph2DWidgetFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(LineGraph2DWidget widget,
			LineGraph2DWidgetModel model, boolean runMode) {
		if (runMode) {
			widget.setDataFormula(model.getDataFormula());
			widget.setXColumnFormula(model.getXColumnFormula());
			widget.setYColumnFormula(model.getYColumnFormula());
//			widget.setTooltipColumnFormula(model.getTooltipFormula());
			widget.setShowAxis(model.getShowAxis());
			widget.setConfigurable(model.isConfigurable());
			widget.setHighlightFocusValue(model.isHighlightFocusValue());
			widget.setFocusValuePv(model.getFocusValuePv());
		}
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				configure(getFigure().getSWTWidget(), getWidgetModel(),
						getFigure().isRunMode());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_DATA_FORMULA, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_X_FORMULA, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_Y_FORMULA, reconfigure);
//		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_TOOLTIP_FORMULA, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.CONFIGURABLE, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_SHOW_AXIS, reconfigure);
		setPropertyChangeHandler(LineGraph2DWidgetModel.PROP_HIGHLIGHT_FOCUS_VALUE, reconfigure);
	}

}
