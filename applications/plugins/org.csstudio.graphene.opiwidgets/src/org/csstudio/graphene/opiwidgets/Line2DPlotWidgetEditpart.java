/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.Line2DPlotWidget;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class Line2DPlotWidgetEditpart extends AbstractWidgetEditPart {

    @Override
    protected IFigure doCreateFigure() {
	Line2DPlotWidgetFigure figure = new Line2DPlotWidgetFigure(this);
	configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
	return figure;
    }

    private static void configure(Line2DPlotWidget widget,
	    Line2DPlotWidgetModel model, boolean runMode) {
	if (runMode)
	    widget.setProcessVariable(model.getProcessVariable());
    }

    @Override
    public Line2DPlotWidgetModel getWidgetModel() {
	Line2DPlotWidgetModel widgetModel = (Line2DPlotWidgetModel) super
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
			((AbstractSWTWidgetFigure<Line2DPlotWidget>) getFigure())
				.getSWTWidget(), getWidgetModel(),
			((Line2DPlotWidgetFigure) getFigure()).isRunMode());
		return false;
	    }
	};
	setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
    }

}
