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
import org.csstudio.opibuilder.widgets.model.EllipseModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Oval;
import org.eclipse.swt.graphics.RGB;

public class Oval2Model extends AbstractADL2Model {

	public Oval2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		Oval ovalWidget = new Oval(adlWidget);
		if (ovalWidget != null) {
			setADLObjectProps(ovalWidget, widgetModel);
			setADLBasicAttributeProps(ovalWidget, widgetModel, true);
			setADLDynamicAttributeProps(ovalWidget, widgetModel);
		}
		//check fill parameters
		if ( ovalWidget.hasADLBasicAttribute() ) {
			if (ovalWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				System.out.println("Oval fill is solid");				
				widgetModel.setPropertyValue(EllipseModel.PROP_TRANSPARENT, false);
				widgetModel.setPropertyValue(EllipseModel.PROP_FILL_LEVEL, 100);
				widgetModel.setPropertyValue(EllipseModel.PROP_HORIZONTAL_FILL, true);
				
			}
			else if (ovalWidget.getAdlBasicAttribute().getFill().equals("outline")) {
				widgetModel.setPropertyValue(EllipseModel.PROP_TRANSPARENT, true);
				OPIColor fColor = (OPIColor)widgetModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
				widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
				if ( ovalWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
					widgetModel.setPropertyValue(EllipseModel.PROP_LINE_STYLE, "Solid");
				}
				if ( ovalWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
					widgetModel.setPropertyValue(EllipseModel.PROP_LINE_STYLE, "Dash");
					
				}
				widgetModel.setPropertyValue(EllipseModel.PROP_LINE_WIDTH, ovalWidget.getAdlBasicAttribute().getWidth());
			}
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new EllipseModel();
		parentModel.addChild(widgetModel, true);
	}
}
