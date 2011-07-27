/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.EllipseModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Oval;
import org.eclipse.swt.graphics.RGB;

public class Oval2Model extends AbstractADL2Model {

	public Oval2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		ADLAbstractWidget ovalWidget = new Oval(adlWidget);
		if (ovalWidget != null) {
			setADLObjectProps(ovalWidget, widgetModel);
			setADLBasicAttributeProps(ovalWidget, widgetModel, true);
			setADLDynamicAttributeProps(ovalWidget, widgetModel);
		}
		//check fill parameters
		if ( ovalWidget.hasADLBasicAttribute() ) {
			setShapesColorFillLine(ovalWidget);
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new EllipseModel();
		parentModel.addChild(widgetModel, true);
	}
}
