package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.ChannelQuery;

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
		registerPopup(figure.getSWTWidget());
		return figure;
	}
	
	private static void configure(PVTableByPropertyWidget widget, PVTableByPropertyModel model, boolean runMode) {
		if (runMode) {
			widget.setChannelQuery(ChannelQuery.Builder.query(model.getChannelQuery()).create());
			widget.setRowSelectionPv(model.getRowSelectionPvName());
		}
		widget.setRowProperty(model.getRowProperty());
		widget.setColumnProperty(model.getColumnProperty());
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
		setPropertyChangeHandler(PVTableByPropertyModel.ROW_SELECTION_PV_NAME, reconfigure);
	}
	
}
