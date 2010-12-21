/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextEntryWidget;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class TextEntry2Model extends AbstractADL2Model {

	public TextEntry2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		className = "TextEntry2Model";
		TextEntryWidget textEntryWidget = new TextEntryWidget(adlWidget);
		if (textEntryWidget != null) {
			setADLObjectProps(textEntryWidget, widgetModel);
			setADLControlProps(textEntryWidget, widgetModel);
		}
		OPIFont font = ((TextInputModel)widgetModel).getFont();
		int fontSize = TranslatorUtils.convertTextHeightToFontSize(widgetModel.getHeight());
		FontData fontData = font.getFontData();
		FontData newFontData = new FontData(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		newFontData.setHeight(fontSize);
		widgetModel.setPropertyValue(LabelModel.PROP_FONT, newFontData);
		//TODO Add limits to TextEntry2Model
		TranslatorUtils.printNotHandledWarning(className, "Text alingnment" );
		//TODO Add format to TextEntry2Model
		TranslatorUtils.printNotHandledWarning(className, "format" );
		//TODO Add color mode to TextEntry2Model
		TranslatorUtils.printNotHandledWarning(className, "color mode" );
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new TextInputModel();
		parentModel.addChild(widgetModel, true);
	}
}
