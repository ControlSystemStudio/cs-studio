/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
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
			setADLBasicAttributeProps(arcWidget, widgetModel, true);
			setADLDynamicAttributeProps(arcWidget, widgetModel);
		}
		widgetModel.setPropertyValue(ArcModel.PROP_START_ANGLE, (float)arcWidget.get_begin()/64);
		widgetModel.setPropertyValue(ArcModel.PROP_TOTAL_ANGLE, (float)arcWidget.get_path()/64);

		//check fill parameters
		if ( arcWidget.hasADLBasicAttribute() ) {
			setShapesColorFillLine(arcWidget);
			if (arcWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				widgetModel.setPropertyValue(ArcModel.PROP_FILL, true);
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
