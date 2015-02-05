/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelViewerWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class ChannelViewerEditPart extends
		AbstractChannelWidgetEditPart<ChannelViewerFigure, ChannelViewerModel> {

	@Override
	protected ChannelViewerFigure doCreateFigure() {
		ChannelViewerFigure figure = new ChannelViewerFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(ChannelViewerWidget channelViewerWidget,
			ChannelViewerModel channelViewerModel, boolean runMode) {
		if (runMode) {
			channelViewerWidget.setChannelQuery(channelViewerModel.getChannelQuery());
		}
		channelViewerWidget.setConfigurable(channelViewerModel.isConfigurable());
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
		setPropertyChangeHandler(ChannelViewerModel.CHANNEL_QUERY, reconfigure);
		setPropertyChangeHandler(ChannelViewerModel.CONFIGURABLE, reconfigure);
	}

}
