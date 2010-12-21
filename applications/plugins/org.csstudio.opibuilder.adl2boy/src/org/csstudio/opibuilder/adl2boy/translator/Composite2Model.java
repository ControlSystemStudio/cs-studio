/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Composite;
import org.eclipse.swt.graphics.RGB;

public class Composite2Model extends AbstractADL2Model {
//	AbstractContainerModel containerModel;
	AbstractContainerModel parentModel;
	
	public Composite2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	public void makeModel(ADLWidget adlWidget, AbstractContainerModel parentModel){
		Composite compositeWidget = new Composite(adlWidget);

		if (compositeWidget.hasCompositeFile()) {
			widgetModel = new LinkingContainerModel();
		}
		else {
			widgetModel = new GroupingContainerModel();
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
			//Add Composite File to Composite2Model
			TranslatorUtils.printNotHandledWarning(className, "composite file");
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
