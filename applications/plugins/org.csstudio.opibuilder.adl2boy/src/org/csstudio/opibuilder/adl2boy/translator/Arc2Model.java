package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.ArcModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Arc;
import org.eclipse.swt.graphics.RGB;

public class Arc2Model extends AbstractADL2Model {
	ArcModel arcModel = new ArcModel();

	public Arc2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		Arc arcWidget = new Arc(adlWidget);
		parentModel.addChild(arcModel, true);
		if (arcWidget != null) {
			setADLObjectProps(arcWidget, arcModel);
			setADLBasicAttributeProps(arcWidget, arcModel, false);
		}
		//TODO Add Dynamic Properties to Arc2Model
		//TODO Add Basic Properties to Arc2Model
		//TODO Add Begin and Path Angle to Arc2Model
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return arcModel;
	}

}
