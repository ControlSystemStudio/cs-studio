package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.utility.pvmanager.widgets.ServicePanel;
import org.eclipse.draw2d.IFigure;

public class ServicePanelEditPart extends AbstractWidgetEditPart {

	/**
	 * Create and initialize figure.
	 */
	@Override
	protected ServicePanelFigure doCreateFigure() {
		ServicePanelFigure figure = new ServicePanelFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(ServicePanel widget, ServicePanelModel model,
			boolean runMode) {
		if (runMode) {
			widget.setServiceName(model.getServiceMethodName());
			widget.configureArgumentMap(model.getPvArgumentPrefix());
			widget.configureResultMap(model.getPvResultPrefix());
		}
	}

	@Override
	public ServicePanelModel getWidgetModel() {
		ServicePanelModel widgetModel = (ServicePanelModel) super
				.getWidgetModel();
		return widgetModel;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			@SuppressWarnings("unchecked")
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				configure(
						((AbstractSWTWidgetFigure<ServicePanel>) getFigure())
								.getSWTWidget(), getWidgetModel(),
						((ServicePanelFigure) getFigure()).isRunMode());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
	}

}
