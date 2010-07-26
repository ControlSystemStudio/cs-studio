package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Composite;
import org.eclipse.swt.graphics.RGB;

public class Composite2Model extends AbstractADL2Model {
	AbstractContainerModel containerModel;

	public Composite2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		Composite compositeWidget = new Composite(adlWidget);

		if (compositeWidget.hasCompositeFile()) {
			containerModel = new LinkingContainerModel();
		}
		else {
			containerModel = new GroupingContainerModel();
		}
		parentModel.addChild(containerModel, true);
		containerModel.setBackgroundColor(parentModel.getBackgroundColor());
		containerModel.setForegroundColor(parentModel.getForegroundColor());
		if (compositeWidget != null) {
			setADLObjectProps(compositeWidget, containerModel);
			if (compositeWidget != null) {
				setADLObjectProps(compositeWidget, containerModel);
				setADLDynamicAttributeProps(compositeWidget, containerModel);
			}
		}
		if (compositeWidget.hasCompositeFile()) {
		}
		else {
			TranslatorUtils.ConvertChildren(compositeWidget.getChildWidgets(), containerModel, colorMap);
			((GroupingContainerModel)(containerModel)).setPropertyValue(GroupingContainerModel.PROP_SHOW_SCROLLBAR, false);
			FixChildPositions();
		}
		//Add Composite File to Composite2Model
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return containerModel;
	}

	private void FixChildPositions() {
		int compositeX = containerModel.getX();
		int compositeY = containerModel.getY();
		
		for (AbstractWidgetModel model : containerModel.getChildren()){
			model.setX(model.getX() - compositeX);
			model.setY(model.getY() - compositeY);
		}
	}
}
