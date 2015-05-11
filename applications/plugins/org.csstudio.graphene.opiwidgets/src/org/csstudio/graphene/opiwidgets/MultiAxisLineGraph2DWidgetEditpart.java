/**
 *
 */
package org.csstudio.graphene.opiwidgets;

import static org.csstudio.graphene.PropertyConstants.PROP_INTERPOLATION_SCHEME;
import static org.csstudio.graphene.PropertyConstants.PROP_SEPARATE_AREAS;

import org.csstudio.graphene.MultiAxisLineGraph2DWidget;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 *
 */
public class MultiAxisLineGraph2DWidgetEditpart extends AbstractPointDatasetGraph2DWidgetEditpart<MultiAxisLineGraph2DWidgetFigure, MultiAxisLineGraph2DWidgetModel> {

    @Override
    protected IFigure doCreateFigure() {
        MultiAxisLineGraph2DWidgetFigure figure = new MultiAxisLineGraph2DWidgetFigure(this);
        configure(figure, getWidgetModel());
        return figure;
    }

    @Override
    protected void configure(MultiAxisLineGraph2DWidgetFigure figure, MultiAxisLineGraph2DWidgetModel model) {
        super.configure(figure, model);
        MultiAxisLineGraph2DWidget widget = figure.getSWTWidget();
        if (figure.isRunMode()) {
            widget.setSeparateAreas(model.isSeparateAreas());
            widget.setInterpolation(model.getInterpolation());
        }
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        super.registerPropertyChangeHandlers();
        setPropertyChangeHandler(PROP_SEPARATE_AREAS, getReconfigureWidgetPropertyChangeHandler());
        setPropertyChangeHandler(PROP_INTERPOLATION_SCHEME, getReconfigureWidgetPropertyChangeHandler());
    }

}
