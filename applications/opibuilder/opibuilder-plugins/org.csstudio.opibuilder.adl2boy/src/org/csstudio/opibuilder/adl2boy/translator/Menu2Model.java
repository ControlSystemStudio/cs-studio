/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Menu;
import org.eclipse.swt.graphics.RGB;

public class Menu2Model extends AbstractADL2Model {

	public Menu2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		className = "Menu2Model";

		Menu menuWidget = new Menu(adlWidget);
		if (menuWidget != null) {
			setADLObjectProps(menuWidget, widgetModel);
			setADLControlProps(menuWidget, widgetModel);
		}
		widgetModel.setPropertyValue(MenuButtonModel.PROP_ACTIONS_FROM_PV, true);
		//set color mode
		String color_mode = menuWidget.getColor_mode();
		if ( color_mode.equals("static") ){
			widgetModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
		}
		else if (color_mode.equals("alarm") ){
			widgetModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
		}
		else if (color_mode.equals("discrete") ){
			widgetModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
			//TODO Menu2Model Figure out what to do if colorMode is discrete
			TranslatorUtils.printNotHandledWarning(className, "discrete color mode");
		}
		//TODO Menu2Model Handle Visual mode to allow display of rows or columns
		TranslatorUtils.printNotHandledWarning(className, "Visual");
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new MenuButtonModel();
		parentModel.addChild(widgetModel, true);
	}
}
