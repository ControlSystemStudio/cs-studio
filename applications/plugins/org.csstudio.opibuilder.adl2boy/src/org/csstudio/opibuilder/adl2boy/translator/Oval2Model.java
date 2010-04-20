package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.EllipseModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Oval;
import org.eclipse.swt.graphics.RGB;

public class Oval2Model extends AbstractADL2Model {
	EllipseModel ellipseModel = new EllipseModel();

	public Oval2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(ellipseModel, true);
		Oval ovalWidget = new Oval(adlWidget);
		if (ovalWidget != null) {
			setADLObjectProps(ovalWidget, ellipseModel);
			setADLBasicAttributeProps(ovalWidget, ellipseModel, false);
		}
		//TODO Add basic properties to Oval2Model
		//TODO Add dynamic properties to Oval2Model
		
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return ellipseModel;
	}

}
