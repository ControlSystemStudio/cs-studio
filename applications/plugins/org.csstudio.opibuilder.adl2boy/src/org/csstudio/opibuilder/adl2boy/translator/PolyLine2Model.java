package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.PolyLine;
import org.eclipse.swt.graphics.RGB;

public class PolyLine2Model extends AbstractADL2Model {
	PolyLineModel polylineModel = new PolyLineModel();

	public PolyLine2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(polylineModel, true);
		PolyLine polylineWidget = new PolyLine(adlWidget);
		if (polylineWidget != null) {
			setADLObjectProps(polylineWidget, polylineModel);
			setADLBasicAttributeProps(polylineWidget, polylineModel, false);
		}
		polylineModel.setPoints(polylineWidget.getAdlPoints().getPointsList(), true);
		//TODO Add basic properties to PolyLine2Model
		//TODO Add dynamic properties to PolyLine2Model
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return polylineModel;
	}

}
