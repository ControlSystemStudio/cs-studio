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
import org.csstudio.opibuilder.widgets.model.ArcModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Arc;
import org.eclipse.swt.graphics.RGB;

public class Arc2Model extends AbstractADL2Model {

	public Arc2Model(ADLWidget adlWidget, RGB[] colorMap,
			AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}
	
	@Override
	public void processWidget(ADLWidget adlWidget) {
		Arc arcWidget = new Arc(adlWidget);
		if (arcWidget != null) {
			setADLObjectProps(arcWidget, widgetModel);
			setADLBasicAttributeProps(arcWidget, widgetModel, false);
			setADLDynamicAttributeProps(arcWidget, widgetModel);
		}
		widgetModel.setPropertyValue(ArcModel.PROP_START_ANGLE, (float)arcWidget.get_begin()/64);
		widgetModel.setPropertyValue(ArcModel.PROP_TOTAL_ANGLE, (float)arcWidget.get_path()/64);

		//check fill parameters
		if ( arcWidget.hasADLBasicAttribute() ) {
			if (arcWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				System.out.println("RECTANGLE fill is solid");				
				widgetModel.setPropertyValue(ArcModel.PROP_FILL, true);
				
			}
			else if (arcWidget.getAdlBasicAttribute().getFill().equals("outline")) {
				OPIColor fColor = (OPIColor)widgetModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
				widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
				if ( arcWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
					widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_STYLE, "Solid");
				}
				if ( arcWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
					widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_STYLE, "Dash");
					
				}
				widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_WIDTH, arcWidget.getAdlBasicAttribute().getWidth());
			}
			
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new ArcModel();
		parentModel.addChild(widgetModel, true);
		
	}
}
