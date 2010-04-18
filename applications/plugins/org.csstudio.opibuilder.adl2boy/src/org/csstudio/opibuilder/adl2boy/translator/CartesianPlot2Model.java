package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.XYGraphModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.CartesianPlot;
import org.eclipse.swt.graphics.RGB;

public class CartesianPlot2Model extends AbstractADL2Model {
	XYGraphModel graphModel = new XYGraphModel();

	public CartesianPlot2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		CartesianPlot plotWidget = new CartesianPlot(adlWidget);
		if (plotWidget != null) {
			setADLObjectProps(plotWidget, graphModel);
		}
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return graphModel;
	}

}
