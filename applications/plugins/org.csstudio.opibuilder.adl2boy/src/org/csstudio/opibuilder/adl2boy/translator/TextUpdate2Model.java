/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.adl2boy.utilities.TextUtilities;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextIndicatorModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextUpdateWidget;
import org.eclipse.swt.graphics.RGB;

public class TextUpdate2Model extends AbstractADL2Model {
	public TextUpdate2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	/**
	 * @param adlWidget
	 */
	public void processWidget(ADLWidget adlWidget) {
		className = "TextUpdate2Model";

		TextUpdateWidget textUpdateWidget = new TextUpdateWidget(adlWidget);
		if (textUpdateWidget != null) {
			setADLObjectProps(textUpdateWidget, widgetModel);
			setADLMonitorProps(textUpdateWidget, widgetModel);
		}
		TextUtilities.setWidgetFont((LabelModel)widgetModel);

		TextUtilities.setAlignment((LabelModel)widgetModel, textUpdateWidget);
		TextUtilities.setFormat((TextIndicatorModel)widgetModel, textUpdateWidget);
		//TODO Add limits to TextUpdate2Model
		TranslatorUtils.printNotHandledWarning(className, "limits" );
		
		//TODO Add color mode to TextUpdate2Model
		TranslatorUtils.printNotHandledWarning(className, "color mode" );
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new TextIndicatorModel();
		parentModel.addChild(widgetModel, true);
	}
}
