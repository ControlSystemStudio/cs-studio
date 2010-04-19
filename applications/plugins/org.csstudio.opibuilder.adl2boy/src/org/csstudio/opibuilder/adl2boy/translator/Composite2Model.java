package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Composite;
import org.eclipse.swt.graphics.RGB;

public class Composite2Model extends AbstractADL2Model {
	AbstractContainerModel containerModel;

	public Composite2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		Composite compositeWidget = new Composite(adlWidget);
		if (!compositeWidget.hasCompositeFile()) {
			containerModel = new LinkingContainerModel();
		}
		else {
			containerModel = new GroupingContainerModel();
			if (compositeWidget != null) {
				setADLObjectProps(compositeWidget, containerModel);
			}
			TranslatorUtils.ConvertChildren(compositeWidget.getChildren(), containerModel, colorMap);
			((GroupingContainerModel)(containerModel)).setPropertyValue(GroupingContainerModel.PROP_SHOW_SCROLLBAR, false);
			FixChildPositions();
		}
		if (compositeWidget != null) {
			setADLObjectProps(compositeWidget, containerModel);
			if (compositeWidget != null) {
				setADLObjectProps(compositeWidget, containerModel);
			}
		}

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
