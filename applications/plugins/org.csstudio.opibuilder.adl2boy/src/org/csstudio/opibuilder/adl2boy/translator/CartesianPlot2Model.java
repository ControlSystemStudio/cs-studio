package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.XYGraphModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.CartesianPlot;
import org.eclipse.swt.graphics.RGB;

public class CartesianPlot2Model extends AbstractADL2Model {
	XYGraphModel graphModel = new XYGraphModel();

	public CartesianPlot2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		CartesianPlot plotWidget = new CartesianPlot(adlWidget);
		parentModel.addChild(graphModel, true);
		if (plotWidget != null) {
			setADLObjectProps(plotWidget, graphModel);
		}
		//TODO Add Point Style to CartesianPlot2Model
		//TODO Add PlotMode to CartesianPlot2Model
		//TODO Add Trace data to CartesianPlot2Model
		//TODO Add Count Num or Channel to CartesianPlot2Model
		//TODO Add TriggerChannel to CartesianPlot2Model
		//TODO Add EraseChannel to CartesianPlot2Model
		//TODO Add EraseMode to CartesianPlot2Model
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return graphModel;
	}

}
