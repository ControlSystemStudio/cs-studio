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
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.csstudio.opibuilder.widgets.model.TextUpdateModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextEntryWidget;
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
        TextUtilities.setWidgetFont((LabelModel)widgetModel);


        TextUtilities.setAlignment((LabelModel)widgetModel, textEntryWidget);
        TextUtilities.setFormat((TextUpdateModel)widgetModel, textEntryWidget);
        widgetModel.setPropertyValue(TextInputModel.PROP_SHOW_UNITS, false);
        //set color mode
        String color_mode = textEntryWidget.getColor_mode();
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
        widgetModel = new TextInputModel();
        parentModel.addChild(widgetModel, true);
    }
}
