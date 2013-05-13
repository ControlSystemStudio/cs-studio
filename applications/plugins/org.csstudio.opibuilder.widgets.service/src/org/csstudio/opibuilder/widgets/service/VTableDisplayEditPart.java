package org.csstudio.opibuilder.widgets.service;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.utility.pvmanager.widgets.VTableWidget;
import org.eclipse.draw2d.IFigure;

public class VTableDisplayEditPart extends AbstractWidgetEditPart {

	/**
	 * Create and initialize figure.
	 */
	@Override
	protected VTableDisplayFigure doCreateFigure() {
		VTableDisplayFigure figure = new VTableDisplayFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(VTableWidget widget, VTableDisplayModel model,
			boolean runMode) {
		if (runMode) {
			widget.setPvFormula(model.getPvFormula());
		}
	}

	@Override
	public VTableDisplayModel getWidgetModel() {
		VTableDisplayModel widgetModel = (VTableDisplayModel) super
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
						((AbstractSWTWidgetFigure<VTableWidget>) getFigure())
								.getSWTWidget(), getWidgetModel(),
						((VTableDisplayFigure) getFigure()).isRunMode());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
	}

}
