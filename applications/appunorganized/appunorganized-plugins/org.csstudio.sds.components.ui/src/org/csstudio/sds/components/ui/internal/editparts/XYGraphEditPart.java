package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.XYGraphModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.draw2d.IFigure;

public class XYGraphEditPart extends AbstractWidgetEditPart {

    @Override
    protected IFigure doCreateFigure() {
        XYGraphModel model = (XYGraphModel) getWidgetModel();
        ToolbarArmedXYGraph figure = new ToolbarArmedXYGraph();

        figure.setBackgroundColor(getModelColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
        figure.setTransparent(model.isTransparent());
        figure.setShowToolbar(model.isToolbarVisible());
        figure.getXYGraph().setShowTitle(model.isTitleVisible());
        figure.getXYGraph().setTitle(model.getTitle());
        figure.getXYGraph().setTitleFont(getModelFont(XYGraphModel.PROP_TITLE_FONT));
        figure.getXYGraph().setTitleColor(getModelColor(XYGraphModel.PROP_TITLE_COLOR));
        figure.getXYGraph().setShowLegend(model.isLegendVisible());

        return figure;
    }

    @Override
    protected void registerPropertyChangeHandlers() {
    }

}
