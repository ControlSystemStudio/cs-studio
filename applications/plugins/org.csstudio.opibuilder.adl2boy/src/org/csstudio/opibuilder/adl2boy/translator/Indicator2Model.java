package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.eclipse.swt.graphics.RGB;

public class Indicator2Model extends AbstractADL2Model {

	public Indicator2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
