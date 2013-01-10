/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.Line2DPlotWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class Line2DPlotEditPart extends
		AbstractChannelWidgetEditPart<Line2DPlotFigure, Line2DPlotModel> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.channel.opiwidgets.AbstractChannelWidgetEditPart#doCreateFigure
	 * ()
	 */
	@Override
	protected Line2DPlotFigure doCreateFigure() {
		Line2DPlotFigure figure = new Line2DPlotFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
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
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				configure(getFigure().getSWTWidget(), getWidgetModel(),
						getFigure().isRunMode());
				return false;
			}

		};
		setPropertyChangeHandler(Line2DPlotModel.CHANNEL_QUERY, reconfigure);
		setPropertyChangeHandler(Line2DPlotModel.CONFIGURABLE, reconfigure);
	}

	private void configure(Line2DPlotWidget line2dPlotWidget,
			Line2DPlotModel line2dPlotModel, boolean runMode) {
		if (runMode) {
			line2dPlotWidget.setChannelQuery(line2dPlotModel.getChannelQuery());
		}
		line2dPlotWidget.setConfigurable(line2dPlotModel.isConfigurable());
		
	}

}
