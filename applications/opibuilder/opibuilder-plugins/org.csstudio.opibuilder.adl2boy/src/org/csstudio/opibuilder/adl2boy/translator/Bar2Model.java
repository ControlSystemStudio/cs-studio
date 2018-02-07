/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractMarkedWidgetModel;
import org.csstudio.opibuilder.widgets.model.TankModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.BarMonitor;
import org.eclipse.swt.graphics.RGB;

public class Bar2Model extends AbstractADL2Model {

    public Bar2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
        super(adlWidget, colorMap, parentModel);
    }

    @Override
    public void processWidget(ADLWidget adlWidget) {
        className = "Bar2Model";
        BarMonitor barWidget = new BarMonitor(adlWidget);
        if (barWidget != null) {
            setADLObjectProps(barWidget, widgetModel);
            setADLControlProps(barWidget, widgetModel);
        }
        widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
        // Decorate the tank Model
        //TODO Bar2Model cannot show value or channel at this time
        TranslatorUtils.printNotHandledWarning(className, "showing the value");
        String label = barWidget.getLabel();
        if ( label.equals("none")){
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, false);

        }
        if ( label.equals("no decorations")){
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, false);
        }
        if ( label.equals("outline")){
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
        }
        if ( label.equals("limits")){
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
        }
        if ( label.equals("channel")){
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
            widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
        }
        //TODO Add PVLimits to Bar2Model
        TranslatorUtils.printNotHandledWarning(className, "Limits");
        //TODO Add BarDirection and fill mode to Bar2Model.  Currently there does not seem to be a way to do this.
        TranslatorUtils.printNotHandledWarning(className, "bar direction");
        //set color mode
        String color_mode = barWidget.getColor_mode();
        if ( color_mode.equals("static") ){
            widgetModel.setPropertyValue(TankModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
        }
        else if (color_mode.equals("alarm") ){
            widgetModel.setPropertyValue(TankModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
            // 'OK' severity uses foreground
            widgetModel.setPropertyValue(TankModel.PROP_COLOR_FOREGROUND, new OPIColor("OK", new RGB(0, 255, 0), true));
        }
        else if (color_mode.equals("discrete") ){
            widgetModel.setPropertyValue(TankModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
            //TODO Bar2Model Figure out what to do if colorMode is discrete
            TranslatorUtils.printNotHandledWarning(className, "discrete color mode");
        }
    }

    @Override
    public void makeModel(ADLWidget adlWidget,
            AbstractContainerModel parentModel) {
        widgetModel = new TankModel();
        parentModel.addChild(widgetModel, true);
    }
}
