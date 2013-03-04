/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.Scatter2DPlotWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 */
public class Scatter2DPlotWidgetFigure extends
	AbstractSWTWidgetFigure<Scatter2DPlotWidget> {

    private ISelectionProvider selectionProvider;

    public Scatter2DPlotWidgetFigure(AbstractBaseEditPart editpart) {
	super(editpart);
	selectionProvider = retrieveSelectionProvider(getSWTWidget());
    }
    

    @Override
    protected Scatter2DPlotWidget createSWTWidget(Composite parent, int style) {
	return new Scatter2DPlotWidget(parent, style);
    }

    private ISelectionProvider retrieveSelectionProvider(
	    Scatter2DPlotWidget widget) {
	if (widget instanceof ISelectionProvider) {
	    return (ISelectionProvider) widget;
	}
	return null;
    }

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
