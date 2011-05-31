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
import org.csstudio.opibuilder.widgets.model.RectangleModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Rectangle;
import org.eclipse.swt.graphics.RGB;

public class Rectangle2Model extends AbstractADL2Model {

	public Rectangle2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		Rectangle rectWidget = new Rectangle(adlWidget);
		if (rectWidget != null) {
			setADLObjectProps(rectWidget, widgetModel);
			setADLBasicAttributeProps(rectWidget, widgetModel, true);
			setADLDynamicAttributeProps(rectWidget, widgetModel);
		}
		//check fill parameters
		if ( rectWidget.hasADLBasicAttribute() ) {
			if (rectWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				widgetModel.setPropertyValue(RectangleModel.PROP_TRANSPARENT, false);
				widgetModel.setPropertyValue(RectangleModel.PROP_FILL_LEVEL, 100);
				widgetModel.setPropertyValue(RectangleModel.PROP_HORIZONTAL_FILL, true);
				
			}
			else if (rectWidget.getAdlBasicAttribute().getFill().equals("outline")) {
				widgetModel.setPropertyValue(RectangleModel.PROP_TRANSPARENT, true);
				OPIColor fColor = (OPIColor)widgetModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
				widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
				if ( rectWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
					widgetModel.setPropertyValue(RectangleModel.PROP_LINE_STYLE, "Solid");
				}
				if ( rectWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
					widgetModel.setPropertyValue(RectangleModel.PROP_LINE_STYLE, "Dash");
					
				}
				widgetModel.setPropertyValue(RectangleModel.PROP_LINE_WIDTH, rectWidget.getAdlBasicAttribute().getWidth());
			}
			
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new RectangleModel();
		parentModel.addChild(widgetModel, true);
	}
}
