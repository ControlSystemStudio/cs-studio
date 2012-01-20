package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

public class ChannelTreeByPropertyEditPart
extends AbstractChannelWidgetEditPart<ChannelTreeByPropertyFigure, ChannelTreeByPropertyModel> {
	
	/**
	 * Create and initialize figure.
	 */
	@Override
	protected ChannelTreeByPropertyFigure doCreateFigure() {
		ChannelTreeByPropertyFigure figure = new ChannelTreeByPropertyFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		registerPopup(figure.getSWTWidget());
		return figure;
	}
	
	private static void configure(ChannelTreeByPropertyWidget widget, ChannelTreeByPropertyModel model, boolean runMode) {
		if (runMode) {
			widget.setChannelQuery(model.getChannelQuery());
			widget.setSelectionPv(model.getSelectionPvName());
		}
		widget.setProperties(model.getTreeProperties());
		widget.setConfigurable(model.getConfigurable());
		widget.setShowChannelNames(model.isShowChannelNames());
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
		setPropertyChangeHandler(ChannelTreeByPropertyModel.CHANNEL_QUERY, reconfigure);
		setPropertyChangeHandler(ChannelTreeByPropertyModel.TREE_PROPERTIES, reconfigure);
		setPropertyChangeHandler(ChannelTreeByPropertyModel.SELECTION_PV_NAME, reconfigure);
	}
	
}
