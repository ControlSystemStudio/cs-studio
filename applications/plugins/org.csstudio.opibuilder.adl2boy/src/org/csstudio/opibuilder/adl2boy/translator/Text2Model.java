/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.adl2boy.utilities.TextUtilities;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextWidget;
import org.eclipse.swt.graphics.RGB;

public class Text2Model extends AbstractADL2Model {
	
	public Text2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel){
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		className = "Text2Model";
		TextWidget textWidget = new TextWidget(adlWidget);
		if (textWidget != null) {
			setADLObjectProps(textWidget, widgetModel);
			setADLBasicAttributeProps(textWidget, widgetModel, true);
			setADLDynamicAttributeProps(textWidget, widgetModel);
			if (textWidget.getTextix() != null ){
				((LabelModel)widgetModel).setText(textWidget.getTextix());
			}
		}
		widgetModel.setPropertyValue(LabelModel.PROP_TRANSPARENT, true);
		TextUtilities.setWidgetFont((LabelModel)widgetModel);
		TextUtilities.setAlignment((LabelModel)widgetModel, textWidget);

	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new LabelModel();
		parentModel.addChild(widgetModel, true);
	}
}
