package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.PVTableByPropertyWidget;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Composite;

public class PVTableByPropertyEditPart extends AbstractWidgetEditPart {
	
	/**
	 * Create and initialize figure.
	 */
	@Override
	protected IFigure doCreateFigure() {
		PVTableByPropertyFigure figure = new PVTableByPropertyFigure((Composite) getViewer().getControl(), getWidgetModel().getParent());
		figure.setRunMode(getExecutionMode() == ExecutionMode.RUN_MODE);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}
	
	/**Get the widget model.
	 * It is recommended that all widget controller should override this method.
	 *@return the widget model.
	 */
	@Override
	public PVTableByPropertyModel getWidgetModel() {
		return (PVTableByPropertyModel) super.getWidgetModel();
	}
	
	private static PVTableByPropertyWidget widgetOf(IFigure figure) {
		return ((PVTableByPropertyFigure) figure).getSWTWidget();
	}
	
	private static boolean runMode(IFigure figure) {
		return ((PVTableByPropertyFigure) figure).isRunMode();
	}
	
	private static void configure(PVTableByPropertyWidget widget, PVTableByPropertyModel model, boolean runMode) {
		if (runMode)
			widget.setChannelQuery(model.getChannelQuery());
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
				configure(widgetOf(figure), getWidgetModel(), runMode(figure));
				return false;
			}
		};
		setPropertyChangeHandler(PVTableByPropertyModel.CHANNEL_QUERY, reconfigure);
		setPropertyChangeHandler(PVTableByPropertyModel.ROW_PROPERTY, reconfigure);
		setPropertyChangeHandler(PVTableByPropertyModel.COLUMN_PROPERTY, reconfigure);
	}
	
}
