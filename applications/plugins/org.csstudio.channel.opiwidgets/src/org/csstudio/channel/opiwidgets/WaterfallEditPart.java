package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.WaterfallWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

public class WaterfallEditPart
extends AbstractChannelWidgetEditPart<WaterfallFigure, WaterfallModel> {
	
	/**
	 * Create and initialize figure.
	 */
	@Override
	protected WaterfallFigure doCreateFigure() {
		WaterfallFigure figure = new WaterfallFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}
	
	private static void configure(WaterfallWidget widget, WaterfallModel model, boolean runMode) {
		if (runMode)
			widget.setInputText(model.getChannelQuery());
		widget.setShowRange(model.isShowRange());
		widget.setAdaptiveRange(model.isAdaptiveRange());
		widget.setPixelDuration(model.getPixelDuration());
		widget.setScrollDown(model.isScrollDown());
		widget.setSortProperty(model.getSortProperty());
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
		setPropertyChangeHandler(WaterfallModel.ADAPTIVE_RANGE, reconfigure);
		setPropertyChangeHandler(WaterfallModel.PIXEL_DURATION, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SCROLL_DOWN, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SHOW_RANGE, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SORT_PROPERTY, reconfigure);
	}
	
}
