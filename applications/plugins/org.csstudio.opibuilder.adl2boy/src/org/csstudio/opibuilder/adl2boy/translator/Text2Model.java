/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextWidget;
import org.eclipse.swt.graphics.FontData;
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
		OPIFont font = ((LabelModel)widgetModel).getFont();
		int fontSize = TranslatorUtils.convertTextHeightToFontSize(widgetModel.getHeight());
		FontData fontData = font.getFontData();
		FontData newFontData = new FontData(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		newFontData.setHeight(fontSize);
		widgetModel.setPropertyValue(LabelModel.PROP_FONT, newFontData);
		
		//TODO Add Alignment to Text2Model
		TranslatorUtils.printNotHandledWarning(className, "Text alingnment" );
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new LabelModel();
		parentModel.addChild(widgetModel, true);
	}
}
