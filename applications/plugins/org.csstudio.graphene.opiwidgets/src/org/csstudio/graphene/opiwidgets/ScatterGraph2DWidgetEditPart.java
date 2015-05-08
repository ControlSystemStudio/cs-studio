/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 *
 */
public class ScatterGraph2DWidgetEditPart extends AbstractPointDatasetGraph2DWidgetEditpart<ScatterGraph2DWidgetFigure, ScatterGraph2DWidgetModel> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.csstudio.opibuilder.editparts.AbstractBaseEditPart#doCreateFigure()
     */
    @Override
    protected IFigure doCreateFigure() {
        ScatterGraph2DWidgetFigure figure = new ScatterGraph2DWidgetFigure(this);
        configure(figure, getWidgetModel());
        return figure;
    }

}
