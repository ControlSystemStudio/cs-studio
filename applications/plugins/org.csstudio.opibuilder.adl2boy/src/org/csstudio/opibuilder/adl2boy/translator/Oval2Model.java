package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.EllipseModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Oval;
import org.eclipse.swt.graphics.RGB;

public class Oval2Model extends AbstractADL2Model {
	EllipseModel ellipseModel = new EllipseModel();

	public Oval2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		Oval ovalWidget = new Oval(adlWidget);
		if (ovalWidget != null) {
			setADLObjectProps(ovalWidget, ellipseModel);
			setADLBasicAttributeProps(ovalWidget, ellipseModel, false);
		}
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return ellipseModel;
	}

}
