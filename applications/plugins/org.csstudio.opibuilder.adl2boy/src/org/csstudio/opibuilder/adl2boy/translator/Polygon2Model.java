package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.PolygonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Polygon;
import org.eclipse.swt.graphics.RGB;

public class Polygon2Model extends AbstractADL2Model {
	PolygonModel polygonModel = new PolygonModel();

	public Polygon2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(polygonModel, true);
		Polygon polygonWidget = new Polygon(adlWidget);
		if (polygonWidget != null) {
			setADLObjectProps(polygonWidget, polygonModel);
			setADLBasicAttributeProps(polygonWidget, polygonModel, false);
		}
		polygonModel.setPoints(polygonWidget.getAdlPoints().getPointsList(), true);
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return polygonModel;
	}

}
