/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.WritePVAction;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.MessageButton;
import org.eclipse.swt.graphics.RGB;

public class MessageButton2Model extends AbstractADL2Model {

	public MessageButton2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		MessageButton messageButtonWidget = new MessageButton(adlWidget);
		if (messageButtonWidget != null) {
			setADLObjectProps(messageButtonWidget, widgetModel);
			setADLControlProps(messageButtonWidget, widgetModel);
		}
		widgetModel.setPropertyValue(ActionButtonModel.PROP_TEXT, messageButtonWidget.getLabel());
		int actionIndex = 0;
		String press_msg = messageButtonWidget.getPress_msg();
		if ( (press_msg != null) && !(press_msg.equals(""))){
			ActionsInput ai = widgetModel.getActionsInput();
			WritePVAction wpvAction = new WritePVAction();
			wpvAction.setPropertyValue(WritePVAction.PROP_PVNAME, messageButtonWidget.getAdlControl().getChan());
			wpvAction.setPropertyValue(WritePVAction.PROP_VALUE, press_msg);
			ai.addAction(wpvAction);
			widgetModel.setPropertyValue(ActionButtonModel.PROP_ACTION_INDEX, actionIndex);
			actionIndex++;
		}
		String release_msg = messageButtonWidget.getRelease_msg();
		if ( (release_msg != null) && !(release_msg.equals(""))){
			widgetModel.setPropertyValue(ActionButtonModel.PROP_TOGGLE_BUTTON, true);
			ActionsInput ai = widgetModel.getActionsInput();
			WritePVAction wpvAction = new WritePVAction();
			wpvAction.setPropertyValue(WritePVAction.PROP_PVNAME, messageButtonWidget.getAdlControl().getChan());
			wpvAction.setPropertyValue(WritePVAction.PROP_VALUE, release_msg);
			ai.addAction(wpvAction);
			widgetModel.setPropertyValue(ActionButtonModel.PROP_RELEASED_ACTION_INDEX, actionIndex);
			actionIndex++;
		}
		String color_mode = messageButtonWidget.getColor_mode();
		if ( color_mode.equals("static") ){
			widgetModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
		}
		else if (color_mode.equals("alarm") ){
			widgetModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
		}
		else if (color_mode.equals("discrete") ){
			widgetModel.setPropertyValue(ActionButtonModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
			//TODO MessageButton2Model Figure out what to do if colorMode is discrete
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel =  new ActionButtonModel();
		parentModel.addChild(widgetModel, true);
	}
}
