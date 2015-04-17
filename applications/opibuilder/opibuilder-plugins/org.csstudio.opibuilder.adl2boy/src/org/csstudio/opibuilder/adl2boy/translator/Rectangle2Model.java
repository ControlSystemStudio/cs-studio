/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.RectangleModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Rectangle;
import org.eclipse.swt.graphics.RGB;

public class Rectangle2Model extends AbstractADL2Model {

	public Rectangle2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		ADLAbstractWidget rectWidget = new Rectangle(adlWidget);
		if (rectWidget != null) {
			setADLObjectProps(rectWidget, widgetModel);
			setADLBasicAttributeProps(rectWidget, widgetModel, true);
			setADLDynamicAttributeProps(rectWidget, widgetModel);
		}
		//check fill parameters
		if ( rectWidget.hasADLBasicAttribute() ) {
			setShapesColorFillLine(rectWidget);
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new RectangleModel();
		parentModel.addChild(widgetModel, true);
	}
}
