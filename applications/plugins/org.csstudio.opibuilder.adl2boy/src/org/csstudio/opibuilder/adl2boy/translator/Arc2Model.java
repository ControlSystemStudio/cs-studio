package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.ArcModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Arc;
import org.eclipse.swt.graphics.RGB;

public class Arc2Model extends AbstractADL2Model {
	ArcModel arcModel = new ArcModel();

	public Arc2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		Arc arcWidget = new Arc(adlWidget);
		if (arcWidget != null) {
			setADLObjectProps(arcWidget, arcModel);
			setADLBasicAttributeProps(arcWidget, arcModel, false);
		}
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return arcModel;
	}

}
