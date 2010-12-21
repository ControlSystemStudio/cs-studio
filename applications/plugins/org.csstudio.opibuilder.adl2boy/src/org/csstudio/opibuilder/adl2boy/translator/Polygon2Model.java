/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.PolygonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Polygon;
import org.eclipse.swt.graphics.RGB;

public class Polygon2Model extends AbstractADL2Model {

	public Polygon2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		Polygon polygonWidget = new Polygon(adlWidget);
		if (polygonWidget != null) {
			setADLObjectProps(polygonWidget, widgetModel);
			setADLBasicAttributeProps(polygonWidget, widgetModel, true);
			setADLDynamicAttributeProps(polygonWidget, widgetModel);
		}
		((PolygonModel)widgetModel).setPoints(polygonWidget.getAdlPoints().getPointsList(), true);

		//check fill parameters
		if ( polygonWidget.hasADLBasicAttribute() ) {
			if (polygonWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				widgetModel.setPropertyValue(PolygonModel.PROP_TRANSPARENT, false);
				widgetModel.setPropertyValue(PolygonModel.PROP_FILL_LEVEL, 100);
				widgetModel.setPropertyValue(PolygonModel.PROP_HORIZONTAL_FILL, true);
				
			}
			else if (polygonWidget.getAdlBasicAttribute().getFill().equals("outline")) {
				widgetModel.setPropertyValue(PolygonModel.PROP_TRANSPARENT, true);
				OPIColor fColor = (OPIColor)widgetModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
				widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
				if ( polygonWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
					widgetModel.setPropertyValue(PolygonModel.PROP_LINE_STYLE, "Solid");
				}
				if ( polygonWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
					widgetModel.setPropertyValue(PolygonModel.PROP_LINE_STYLE, "Dash");
					
				}
				widgetModel.setPropertyValue(PolygonModel.PROP_LINE_WIDTH, polygonWidget.getAdlBasicAttribute().getWidth());
			}
			
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new PolygonModel();
		parentModel.addChild(widgetModel, true);
	}
}
