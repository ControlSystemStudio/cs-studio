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
		return figure;
	}
	
	private ChannelTreeByPropertySelectionNotification notification;
	private void configure(ChannelTreeByPropertyWidget widget, ChannelTreeByPropertyModel model, boolean runMode) {
		if (runMode) {
			widget.setChannelQuery(model.getChannelQuery());
			if (notification != null) {
				notification.close();
				notification = null;
			}
			if (model.getSelectionPv() != null && !model.getSelectionPv().isEmpty()) {
				notification = new ChannelTreeByPropertySelectionNotification(model.getSelectionPv(),
					model.getSelectionExpression(), widget);
			}
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
		setPropertyChangeHandler(ChannelTreeByPropertyModel.SELECTION_PV, reconfigure);
		setPropertyChangeHandler(ChannelTreeByPropertyModel.SELECTION_EXPRESSION, reconfigure);
	}
	
}
