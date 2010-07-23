package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.EllipseModel;
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
			setADLBasicAttributeProps(ovalWidget, ellipseModel, true);
		}
		//check fill parameters
		if ( ovalWidget.hasADLBasicAttribute() ) {
			if (ovalWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				System.out.println("RECTANGLE fill is solid");				
				ellipseModel.setPropertyValue(EllipseModel.PROP_TRANSPARENT, false);
				ellipseModel.setPropertyValue(EllipseModel.PROP_FILL_LEVEL, 100);
				ellipseModel.setPropertyValue(EllipseModel.PROP_HORIZONTAL_FILL, true);
				
			}
			else if (ovalWidget.getAdlBasicAttribute().getFill().equals("outline")) {
				ellipseModel.setPropertyValue(EllipseModel.PROP_TRANSPARENT, true);
				OPIColor fColor = (OPIColor)ellipseModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
				ellipseModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
				if ( ovalWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
					ellipseModel.setPropertyValue(EllipseModel.PROP_LINE_STYLE, "Solid");
				}
				if ( ovalWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
					ellipseModel.setPropertyValue(EllipseModel.PROP_LINE_STYLE, "Dash");
					
				}
				ellipseModel.setPropertyValue(EllipseModel.PROP_LINE_WIDTH, ovalWidget.getAdlBasicAttribute().getWidth());
			}
			
		}
		//TODO Add dynamic properties to Oval2Model
		
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return ellipseModel;
	}

}
