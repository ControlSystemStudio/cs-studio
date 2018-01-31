/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.adl2boy.utilities.TextUtilities;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextUpdateModel;
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
    @Override
    public void processWidget(ADLWidget adlWidget) {
        className = "TextUpdate2Model";

        TextUpdateWidget textUpdateWidget = new TextUpdateWidget(adlWidget);
        if (textUpdateWidget != null) {
            setADLObjectProps(textUpdateWidget, widgetModel);
            setADLMonitorProps(textUpdateWidget, widgetModel);
        }
        TextUtilities.setWidgetFont((LabelModel)widgetModel);
        TextUtilities.setAlignment((LabelModel)widgetModel, textUpdateWidget);
        TextUtilities.setFormat((TextUpdateModel)widgetModel, textUpdateWidget);
        widgetModel.setPropertyValue(TextUpdateModel.PROP_SHOW_UNITS, false);

        //TODO Add limits to TextUpdate2Model
        TranslatorUtils.printNotHandledWarning(className, "limits" );
        //set color mode
        String color_mode = textUpdateWidget.getColor_mode();
        if ( color_mode.equals("static") ){
            widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
        }
        else if (color_mode.equals("alarm") ){
            widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
            // 'OK' severity uses foreground, so make that green
            widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_COLOR_FOREGROUND, new RGB(0, 255, 0));
        }
        else if (color_mode.equals("discrete") ){
            widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
            //TODO TextEntry2Model Figure out what to do if colorMode is discrete
        }
    }

    @Override
    public void makeModel(ADLWidget adlWidget,
            AbstractContainerModel parentModel) {
        widgetModel = new TextUpdateModel();
        parentModel.addChild(widgetModel, true);
    }
}
