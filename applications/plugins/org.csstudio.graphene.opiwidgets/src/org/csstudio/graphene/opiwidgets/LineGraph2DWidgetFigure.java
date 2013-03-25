/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.LineGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 */
public class LineGraph2DWidgetFigure extends
	AbstractSWTWidgetFigure<LineGraph2DWidget> {

    public LineGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
	super(editpart);
	selectionProvider = retrieveSelectionProvider(getSWTWidget());
    }

    @Override
    protected LineGraph2DWidget createSWTWidget(Composite parent, int style) {
	return new LineGraph2DWidget(parent, style);
    }

    /**
     * Returns the selection provider to be used for pop-ups. By default, if the
     * widget is itself an ISelectionProvider, the widget is returned.
     * 
     * @param widget
     *            the widget
     * @return the selection provider or null
     */
    protected ISelectionProvider retrieveSelectionProvider(LineGraph2DWidget widget) {
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
