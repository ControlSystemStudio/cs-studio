package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.RectangleModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Rectangle;
import org.eclipse.swt.graphics.RGB;

public class Rectangle2Model extends AbstractADL2Model {
	RectangleModel rectangleModel = new RectangleModel();

	public Rectangle2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		Rectangle rectWidget = new Rectangle(adlWidget);
		if (rectWidget != null) {
			setADLObjectProps(rectWidget, rectangleModel);
			setADLBasicAttributeProps(rectWidget, rectangleModel, false);
		}
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return rectangleModel;
	}

}
