/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.PolyLine;
import org.eclipse.swt.graphics.RGB;

public class PolyLine2Model extends AbstractADL2Model {

    public PolyLine2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
        super(adlWidget, colorMap, parentModel);
    }

    @Override
    public void processWidget(ADLWidget adlWidget) {
        PolyLine polylineWidget = new PolyLine(adlWidget);
        if (polylineWidget != null) {
            setADLObjectProps(polylineWidget, widgetModel);
            setADLBasicAttributeProps(polylineWidget, widgetModel, true);
            setADLDynamicAttributeProps(polylineWidget, widgetModel);
        }
        ((PolyLineModel)widgetModel).setPoints(polylineWidget.getAdlPoints().getPointsList(), true);
        ((PolyLineModel)widgetModel).setLineWidth(polylineWidget.getAdlBasicAttribute().getWidth());
        widgetModel.setPropertyValue(AbstractShapeModel.PROP_FILL_LEVEL, 100.0);
        if ( polylineWidget.hasADLBasicAttribute() ) {
            setShapesColorFillLine(polylineWidget);
        }
    }

    @Override
    public void makeModel(ADLWidget adlWidget,
            AbstractContainerModel parentModel) {
        widgetModel = new PolyLineModel();
        parentModel.addChild(widgetModel, true);
    }
}
