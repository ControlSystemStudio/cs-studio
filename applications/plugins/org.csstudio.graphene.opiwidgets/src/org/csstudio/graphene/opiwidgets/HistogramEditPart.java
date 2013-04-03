package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.HistogramWidget;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.draw2d.IFigure;

public class HistogramEditPart extends AbstractWidgetEditPart {

    /**
     * Create and initialize figure.
     */
    @Override
    protected HistogramFigure doCreateFigure() {
	HistogramFigure figure = new HistogramFigure(this);
	configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
	return figure;
    }

    private static void configure(HistogramWidget widget, HistogramModel model,
	    boolean runMode) {
	if (runMode)
	    widget.setProcessVariable(model.getProcessVariable());
    }

    @Override
    public HistogramModel getWidgetModel() {
	HistogramModel widgetModel = (HistogramModel) super.getWidgetModel();
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
			((AbstractSWTWidgetFigure<HistogramWidget>) getFigure())
				.getSWTWidget(), getWidgetModel(),
			((HistogramFigure) getFigure()).isRunMode());
		return false;
	    }
	};
	setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
    }

}
