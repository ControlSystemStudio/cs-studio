/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.ScatterGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 */
public class ScatterGraph2DWidgetFigure extends
	AbstractSWTWidgetFigure<ScatterGraph2DWidget> {

    private ISelectionProvider selectionProvider;

    public ScatterGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
	super(editpart);
	selectionProvider = retrieveSelectionProvider(getSWTWidget());
    }
    

    @Override
    protected ScatterGraph2DWidget createSWTWidget(Composite parent, int style) {
	return new ScatterGraph2DWidget(parent, style);
    }

    private ISelectionProvider retrieveSelectionProvider(
	    ScatterGraph2DWidget widget) {
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
