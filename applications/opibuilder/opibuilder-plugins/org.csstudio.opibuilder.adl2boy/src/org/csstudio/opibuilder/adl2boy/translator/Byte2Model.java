/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.ByteMonitorModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ByteMonitor;
import org.eclipse.swt.graphics.RGB;

public class Byte2Model extends AbstractADL2Model {

    public Byte2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
        super(adlWidget, colorMap, parentModel);
    }

    @Override
    public void processWidget(ADLWidget adlWidget) {
        ByteMonitor byteWidget = new ByteMonitor(adlWidget);
        if (byteWidget != null) {
            setADLObjectProps(byteWidget, widgetModel);
            setADLMonitorProps(byteWidget, widgetModel);

            widgetModel.setPropertyValue(ByteMonitorModel.PROP_HORIZONTAL, ! byteWidget.getDirection().equals("down"));
        }

        //TODO many things
        TranslatorUtils.printNotHandledWarning(className, "many things");
    }

    @Override
    public void makeModel(ADLWidget adlWidget,
            AbstractContainerModel parentModel) {
        widgetModel = new ByteMonitorModel();
        parentModel.addChild(widgetModel, true);
    }
}
