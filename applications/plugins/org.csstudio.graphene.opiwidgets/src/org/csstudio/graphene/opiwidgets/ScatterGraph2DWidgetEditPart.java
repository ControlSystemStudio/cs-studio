/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.ScatterGraph2DWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class ScatterGraph2DWidgetEditPart extends AbstractPointDatasetGraph2DWidgetEditpart<ScatterGraph2DWidgetFigure, ScatterGraph2DWidgetModel> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.opibuilder.editparts.AbstractBaseEditPart#doCreateFigure()
	 */
	@Override
	protected IFigure doCreateFigure() {
		ScatterGraph2DWidgetFigure figure = new ScatterGraph2DWidgetFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(ScatterGraph2DWidget widget,
			ScatterGraph2DWidgetModel model, boolean runMode) {
		if (runMode) {
			widget.setDataFormula(model.getDataFormula());
			widget.setXColumnFormula(model.getXColumnFormula());
			widget.setYColumnFormula(model.getYColumnFormula());
//			widget.setTooltipColumnFormula(model.getTooltipFormula());
			widget.setShowAxis(model.getShowAxis());
			//widget.setConfigurable(model.isConfigurable());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#
	 * registerPropertyChangeHandlers()
	 */
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
		setPropertyChangeHandler(ScatterGraph2DWidgetModel.PROP_SHOW_AXIS,
				reconfigure);

	}

}
