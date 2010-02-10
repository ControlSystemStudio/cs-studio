package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.model.XYGraphModel;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.draw2d.IFigure;

public class XYGraphEditPart extends AbstractWidgetEditPart {

	@Override
	protected IFigure doCreateFigure() {
		XYGraphModel model = (XYGraphModel) getWidgetModel();
		ToolbarArmedXYGraph figure = new ToolbarArmedXYGraph();
		
		figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(model.getBackgroundColor()));
		figure.setTransparent(model.isTransparent());
		figure.setShowToolbar(model.isToolbarVisible());
		figure.getXYGraph().setShowTitle(model.isTitleVisible());
		figure.getXYGraph().setTitle(model.getTitle());
		figure.getXYGraph().setTitleFont(CustomMediaFactory.getInstance().getFont(model.getTitleFont()));
		figure.getXYGraph().setTitleColor(CustomMediaFactory.getInstance().getColor(model.getTitleColor()));
		figure.getXYGraph().setShowLegend(model.isLegendVisible());
		
		return figure;
	}

	@Override
	protected void registerPropertyChangeHandlers() {

	}

}
