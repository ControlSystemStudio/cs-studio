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
		configure(figure, getWidgetModel(), figure.isRunMode());
		return figure;
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
				configure(getFigure(), getWidgetModel(),
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
