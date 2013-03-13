/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.Line2DPlotWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 */
public class Line2DPlotWidgetFigure extends
	AbstractSWTWidgetFigure<Line2DPlotWidget> {

    public Line2DPlotWidgetFigure(AbstractBaseEditPart editpart) {
	super(editpart);
	selectionProvider = retrieveSelectionProvider(getSWTWidget());
    }

    @Override
    protected Line2DPlotWidget createSWTWidget(Composite parent, int style) {
	return new Line2DPlotWidget(parent, style);
    }

    /**
     * Returns the selection provider to be used for pop-ups. By default, if the
     * widget is itself an ISelectionProvider, the widget is returned.
     * 
     * @param widget
     *            the widget
     * @return the selection provider or null
     */
    protected ISelectionProvider retrieveSelectionProvider(Line2DPlotWidget widget) {
	if (widget instanceof ISelectionProvider) {
	    return (ISelectionProvider) widget;
	}
	return null;
    }

    private final ISelectionProvider selectionProvider;

    /**
     * The selection provider to be used for the pop-up.
     * 
     * @return the selection provider or null
     */
    public ISelectionProvider getSelectionProvider() {
	return selectionProvider;
    }

    public boolean isRunMode() {
	return runmode;
    }

}
