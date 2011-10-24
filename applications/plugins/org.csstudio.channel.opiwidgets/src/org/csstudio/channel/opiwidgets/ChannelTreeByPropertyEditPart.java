package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.channel.widgets.PVTableByPropertyWidget;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Composite;

public class ChannelTreeByPropertyEditPart extends AbstractWidgetEditPart {
	
	/**
	 * Create and initialize figure.
	 */
	@Override
	protected IFigure doCreateFigure() {
		ChannelTreeByPropertyFigure figure = new ChannelTreeByPropertyFigure((Composite) getViewer().getControl(), getWidgetModel().getParent());
		figure.setRunMode(getExecutionMode() == ExecutionMode.RUN_MODE);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}
	
	/**Get the widget model.
	 * It is recommended that all widget controller should override this method.
	 *@return the widget model.
	 */
	@Override
	public ChannelTreeByPropertyModel getWidgetModel() {
		return (ChannelTreeByPropertyModel) super.getWidgetModel();
	}
	
	private static ChannelTreeByPropertyWidget widgetOf(IFigure figure) {
		return ((ChannelTreeByPropertyFigure) figure).getSWTWidget();
	}
	
	private static boolean runMode(IFigure figure) {
		return ((ChannelTreeByPropertyFigure) figure).isRunMode();
	}
	
	private static void configure(ChannelTreeByPropertyWidget widget, ChannelTreeByPropertyModel model, boolean runMode) {
		if (runMode)
			widget.setChannelQuery(model.getChannelQuery());
		widget.setProperties(model.getTreeProperties());
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure figure) {
				configure(widgetOf(figure), getWidgetModel(), runMode(figure));
				return false;
			}
		};
		setPropertyChangeHandler(ChannelTreeByPropertyModel.CHANNEL_QUERY, reconfigure);
		setPropertyChangeHandler(ChannelTreeByPropertyModel.TREE_PROPERTIES, reconfigure);
		setPropertyChangeHandler(ChannelTreeByPropertyModel.PV_FOR_SELECTION, reconfigure);
	}
	
}
