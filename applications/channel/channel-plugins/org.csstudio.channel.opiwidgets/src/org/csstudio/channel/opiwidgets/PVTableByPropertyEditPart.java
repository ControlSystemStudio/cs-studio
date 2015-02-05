package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.PVTableByPropertyCell;
import org.csstudio.channel.widgets.PVTableByPropertyWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

public class PVTableByPropertyEditPart
extends AbstractChannelWidgetEditPart<PVTableByPropertyFigure, PVTableByPropertyModel> {
	
	/**
	 * Create and initialize figure.
	 */
	@Override
	protected PVTableByPropertyFigure doCreateFigure() {
		PVTableByPropertyFigure figure = new PVTableByPropertyFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}
	
	private ChannelSelectionNotification notification;
	
	private void configure(PVTableByPropertyWidget widget, PVTableByPropertyModel model, boolean runMode) {
		if (runMode) {
			widget.setChannelQuery(model.getChannelQuery());
			if (notification != null) {
				notification.close();
				notification = null;
			}
			if (model.getSelectionPv() != null && !model.getSelectionPv().isEmpty()) {
				notification = new ChannelSelectionNotification(model.getSelectionPv(), model.getSelectionExpression(), widget) {
					
					@Override
					protected String notificationFor(Object selection) {
						return getNotificationExpression().notification(((PVTableByPropertyCell) selection).getChannels());
					}
				};
			}
		}
		widget.setRowProperty(model.getRowProperty());
		widget.setColumnProperty(model.getColumnProperty());
		widget.setColumnTags(model.getColumnTags());
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
		setPropertyChangeHandler(PVTableByPropertyModel.CHANNEL_QUERY, reconfigure);
		setPropertyChangeHandler(PVTableByPropertyModel.ROW_PROPERTY, reconfigure);
		setPropertyChangeHandler(PVTableByPropertyModel.COLUMN_PROPERTY, reconfigure);
		setPropertyChangeHandler(PVTableByPropertyModel.COLUMN_TAGS, reconfigure);
		setPropertyChangeHandler(PVTableByPropertyModel.SELECTION_EXPRESSION, reconfigure);
		setPropertyChangeHandler(PVTableByPropertyModel.SELECTION_PV, reconfigure);
	}
	
}
