/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel.ResizeBehaviour;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Composite;
import org.eclipse.swt.graphics.RGB;

@SuppressWarnings("nls")
public class Composite2Model extends AbstractADL2Model {
//    AbstractContainerModel containerModel;
    AbstractContainerModel parentModel;

    public Composite2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
        super(adlWidget, colorMap, parentModel);
    }

    @Override
    public void makeModel(ADLWidget adlWidget, AbstractContainerModel parentModel){
        Composite compositeWidget = new Composite(adlWidget);

        if (compositeWidget.hasCompositeFile()) {
            widgetModel = new LinkingContainerModel();
        }
        else {
            widgetModel = new GroupingContainerModel();
            widgetModel.setPropertyValue(GroupingContainerModel.PROP_TRANSPARENT,true);
        }
        parentModel.addChild(widgetModel, true);
    }

    @Override
    public void processWidget(ADLWidget adlWidget) {
        className = "Composite2Model";
        Composite compositeWidget = new Composite(adlWidget);

        widgetModel.setBackgroundColor(widgetModel.getParent().getBackgroundColor());
        widgetModel.setForegroundColor(widgetModel.getParent().getForegroundColor());
        if (compositeWidget != null) {
            setADLObjectProps(compositeWidget, widgetModel);
            if (compositeWidget != null) {
                setADLObjectProps(compositeWidget, widgetModel);
                setADLDynamicAttributeProps(compositeWidget, widgetModel);
            }
        }
        if (compositeWidget.hasCompositeFile()) {
            // Expect "path_to_file;macros"
            String[] compositeFile = compositeWidget.get_compositeFile().replaceAll("\"", "").split(";");
            if (compositeFile.length > 0)
            {
                widgetModel.setPropertyValue(LinkingContainerModel.PROP_OPI_FILE, compositeFile[0].replace(".adl", ".opi"));

                if (compositeFile.length > 1 && compositeFile[1].length() > 0)
                    widgetModel.setPropertyValue(AbstractContainerModel.PROP_MACROS, makeMacros(compositeFile[1]));
            }
            else {
                TranslatorUtils.printNotHandledWarning(className, "composite file");
            }
            // Don't resize, no border to avoid unexpected growth/shrinkage
            widgetModel.setPropertyValue(LinkingContainerModel.PROP_RESIZE_BEHAVIOUR, ResizeBehaviour.CROP_OPI.ordinal());
            widgetModel.setPropertyValue(LinkingContainerModel.PROP_BORDER_STYLE, BorderStyle.NONE.ordinal());
        }
        else {
            TranslatorUtils.ConvertChildren(compositeWidget.getChildWidgets(), (AbstractContainerModel)widgetModel, colorMap);
            ((GroupingContainerModel)(widgetModel)).setPropertyValue(GroupingContainerModel.PROP_SHOW_SCROLLBAR, false);
            FixChildPositions();
        }
    }

    private void FixChildPositions() {
        int compositeX = widgetModel.getX();
        int compositeY = widgetModel.getY();

        for (AbstractWidgetModel model : ((AbstractContainerModel)widgetModel).getChildren()){
            model.setX(model.getX() - compositeX);
            model.setY(model.getY() - compositeY);
        }
    }
}
