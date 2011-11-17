package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Composite;

public class ChannelTreeByPropertyEditPart
extends AbstractChannelWidgetEditPart<ChannelTreeByPropertyFigure, ChannelTreeByPropertyModel> {
	
	/**
	 * Create and initialize figure.
	 */
	@Override
	protected ChannelTreeByPropertyFigure doCreateFigure() {
		ChannelTreeByPropertyFigure figure = new ChannelTreeByPropertyFigure((Composite) getViewer().getControl(), getWidgetModel().getParent());
		figure.setRunMode(getExecutionMode() == ExecutionMode.RUN_MODE);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		registerPopup(figure.getSWTWidget().getTree());
		return figure;
	}
	
	private static void configure(ChannelTreeByPropertyWidget widget, ChannelTreeByPropertyModel model, boolean runMode) {
		if (runMode) {
			widget.setChannelQuery(ChannelQuery.Builder.query(model.getChannelQuery()).create());
			widget.setSelectionPv(model.getSelectionPvName());
		}
		widget.setProperties(model.getTreeProperties());
		widget.setConfigurable(model.getConfigurable());
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
