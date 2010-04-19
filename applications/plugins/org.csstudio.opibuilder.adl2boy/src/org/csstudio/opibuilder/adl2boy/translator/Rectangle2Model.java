package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.RectangleModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Rectangle;
import org.eclipse.swt.graphics.RGB;

public class Rectangle2Model extends AbstractADL2Model {
	RectangleModel rectangleModel = new RectangleModel();

	public Rectangle2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(rectangleModel, true);
		Rectangle rectWidget = new Rectangle(adlWidget);
		if (rectWidget != null) {
			setADLObjectProps(rectWidget, rectangleModel);
			setADLBasicAttributeProps(rectWidget, rectangleModel, true);
		}
		//check fill parameters
		if ( rectWidget.hasADLBasicAttribute() ) {
			System.out.println("RECTANGLE has basic attributes");
			if (rectWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				System.out.println("RECTANGLE fill is solid");				
				rectangleModel.setPropertyValue(RectangleModel.PROP_TRANSPARENT, false);
				rectangleModel.setPropertyValue(RectangleModel.PROP_FILL_LEVEL, 100);
				rectangleModel.setPropertyValue(RectangleModel.PROP_HORIZONTAL_FILL, true);
				
			}
			else if (rectWidget.getAdlBasicAttribute().getFill().equals("outline")) {
				System.out.println("RECTANGLE has fill is outline");
				rectangleModel.setPropertyValue(RectangleModel.PROP_TRANSPARENT, true);
				OPIColor fColor = (OPIColor)rectangleModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
				rectangleModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
				if ( rectWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
					rectangleModel.setPropertyValue(RectangleModel.PROP_LINE_STYLE, "Solid");
				}
				if ( rectWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
					rectangleModel.setPropertyValue(RectangleModel.PROP_LINE_STYLE, "Dash");
					
				}
			}
			else {
				
			}
		}
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return rectangleModel;
	}

}
