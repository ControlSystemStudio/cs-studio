/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.widgets.model.AbstractChoiceModel;
import org.csstudio.opibuilder.widgets.model.ChoiceButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ChoiceButton;
import org.eclipse.swt.graphics.RGB;

public class ChoiceButton2Model extends AbstractADL2Model {

	public ChoiceButton2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		className = "ChoiceButton2Model";

		ChoiceButton choiceWidget = new ChoiceButton(adlWidget);
		if (choiceWidget != null) {
			setADLObjectProps(choiceWidget, widgetModel);
			setADLControlProps(choiceWidget, widgetModel);
		}
		if (choiceWidget.getStacking() != null) {
			if (choiceWidget.getStacking().equals("column")){
				((AbstractChoiceModel)widgetModel).setPropertyValue(AbstractChoiceModel.PROP_HORIZONTAL, true);
			}
			else if (choiceWidget.getStacking().equals("row")){
				((AbstractChoiceModel)widgetModel).setPropertyValue(AbstractChoiceModel.PROP_HORIZONTAL, false);
			}
		}
		else{
			((AbstractChoiceModel)widgetModel).setPropertyValue(AbstractChoiceModel.PROP_HORIZONTAL, false);
		}
		String color_mode = choiceWidget.getColor_mode();
		if ( color_mode.equals("static") ){
			((AbstractPVWidgetModel)widgetModel).setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
		}
		else if (color_mode.equals("alarm") ){
			((AbstractPVWidgetModel)widgetModel).setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
		}
		else if (color_mode.equals("discrete") ){
			((AbstractPVWidgetModel)widgetModel).setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
			//TODO Menu2Model Figure out what to do if colorMode is discrete
			TranslatorUtils.printNotHandledWarning(className, "discrete color mode");
		}
		widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_BORDER_ALARMSENSITIVE, false);
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new ChoiceButtonModel();
		parentModel.addChild(widgetModel, true);
	}
}
