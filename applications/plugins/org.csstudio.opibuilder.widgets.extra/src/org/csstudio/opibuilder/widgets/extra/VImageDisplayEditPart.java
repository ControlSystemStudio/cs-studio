package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.utility.pvmanager.widgets.VImageWidget;
import org.eclipse.draw2d.IFigure;

public class VImageDisplayEditPart extends AbstractSelectionWidgetEditpart<VImageDisplayFigure, VImageDisplayModel> {

	/**
	 * Create and initialize figure.
	 */
	@Override
	protected VImageDisplayFigure doCreateFigure() {
		VImageDisplayFigure figure = new VImageDisplayFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(VImageWidget widget, VImageDisplayModel model,
			boolean runMode) {
		if (runMode) {
			widget.setPvFormula(model.getPvFormula());
		}
	}

	@Override
	public VImageDisplayModel getWidgetModel() {
		VImageDisplayModel widgetModel = (VImageDisplayModel) super
				.getWidgetModel();
		return widgetModel;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				configure(
						((AbstractSWTWidgetFigure<VImageWidget>) getFigure())
								.getSWTWidget(), getWidgetModel(),
						((VImageDisplayFigure) getFigure()).isRunMode());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
	}

}
