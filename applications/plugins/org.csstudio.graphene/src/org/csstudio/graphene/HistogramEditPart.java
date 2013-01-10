package org.csstudio.graphene;

import org.csstudio.channel.opiwidgets.AbstractChannelWidgetEditPart;
import org.csstudio.channel.opiwidgets.WaterfallModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

public class HistogramEditPart
extends AbstractChannelWidgetEditPart<HistogramFigure, HistogramModel> {
	
	/**
	 * Create and initialize figure.
	 */
	@Override
	protected HistogramFigure doCreateFigure() {
		HistogramFigure figure = new HistogramFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}
	
	private static void configure(HistogramWidget widget, HistogramModel model, boolean runMode) {
		if (runMode)
			widget.setProcessVariable(model.getProcessVariable());
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure figure) {
				configure(getFigure().getSWTWidget(), getWidgetModel(), getFigure().isRunMode());
				return false;
			}
		};
		setPropertyChangeHandler(WaterfallModel.CHANNEL_QUERY, reconfigure);
		setPropertyChangeHandler(WaterfallModel.VALUE_RANGE, reconfigure);
		setPropertyChangeHandler(WaterfallModel.RESOLUTION, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SCROLL_DIRECTION, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SHOW_TIME_AXIS, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SORT_PROPERTY, reconfigure);
	}
	
}
