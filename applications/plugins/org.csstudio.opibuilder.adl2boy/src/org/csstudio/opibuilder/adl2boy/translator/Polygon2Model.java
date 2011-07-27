/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.PolygonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Polygon;
import org.eclipse.swt.graphics.RGB;

public class Polygon2Model extends AbstractADL2Model {

	public Polygon2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		ADLAbstractWidget polygonWidget = new Polygon(adlWidget);
		if (polygonWidget != null) {
			setADLObjectProps(polygonWidget, widgetModel);
			setADLBasicAttributeProps(polygonWidget, widgetModel, true);
			setADLDynamicAttributeProps(polygonWidget, widgetModel);
		}
		((PolygonModel)widgetModel).setPoints(polygonWidget.getAdlPoints().getPointsList(), true);

		//check fill parameters
		if ( polygonWidget.hasADLBasicAttribute() ) {
			setShapesColorFillLine(polygonWidget);
			
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new PolygonModel();
		parentModel.addChild(widgetModel, true);
	}
}
