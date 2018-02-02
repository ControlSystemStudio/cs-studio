/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.ScrollBarModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Valuator;
import org.eclipse.swt.graphics.RGB;

/**
 *
 * @author John Hammonds, Argonne National Laboratory
 *
 */
public class Valuator2Model extends AbstractADL2Model {

    public Valuator2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
        super(adlWidget, colorMap, parentModel);
    }

    @Override
    public void processWidget(ADLWidget adlWidget) {
        className = "Valuator2Model";
        Valuator valuatorWidget = new Valuator(adlWidget);
        if (valuatorWidget != null) {
            setADLObjectProps(valuatorWidget, widgetModel);
            setADLControlProps(valuatorWidget, widgetModel);
        }
        //TODO Add PV Limits to Valuator2Model
        TranslatorUtils.printNotHandledWarning(className, "Limits");
        //TODO Add Label info to Valuator2Model
        TranslatorUtils.printNotHandledWarning(className, "Label");
        //TODO Add ColorMode to Valuator2Model
        TranslatorUtils.printNotHandledWarning(className, "Color Mode");

        widgetModel.setPropertyValue(ScrollBarModel.PROP_BAR_LENGTH, 1.0);
        widgetModel.setPropertyValue(ScrollBarModel.PROP_STEP_INCREMENT, Double.valueOf(valuatorWidget.getIncrement()));
        widgetModel.setPropertyValue(ScrollBarModel.PROP_PAGE_INCREMENT, Double.valueOf(valuatorWidget.getIncrement()));
        widgetModel.setPropertyValue(ScrollBarModel.PROP_HORIZONTAL, valuatorWidget.getDirection().equals("right"));
    }

    @Override
    public void makeModel(ADLWidget adlWidget,
            AbstractContainerModel parentModel) {
        widgetModel = new ScrollBarModel();
        parentModel.addChild(widgetModel, true);
    }
}
