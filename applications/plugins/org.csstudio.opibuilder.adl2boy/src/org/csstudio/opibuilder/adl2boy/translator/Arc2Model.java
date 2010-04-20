package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
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
		arcModel.setPropertyValue(ArcModel.PROP_START_ANGLE, (float)arcWidget.get_begin()/64);
		arcModel.setPropertyValue(ArcModel.PROP_TOTAL_ANGLE, (float)arcWidget.get_path()/64);

		//check fill parameters
		if ( arcWidget.hasADLBasicAttribute() ) {
			if (arcWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				System.out.println("RECTANGLE fill is solid");				
				arcModel.setPropertyValue(ArcModel.PROP_FILL, true);
				
			}
			else if (arcWidget.getAdlBasicAttribute().getFill().equals("outline")) {
				OPIColor fColor = (OPIColor)arcModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
				arcModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
				if ( arcWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
					arcModel.setPropertyValue(AbstractShapeModel.PROP_LINE_STYLE, "Solid");
				}
				if ( arcWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
					arcModel.setPropertyValue(AbstractShapeModel.PROP_LINE_STYLE, "Dash");
					
				}
				arcModel.setPropertyValue(AbstractShapeModel.PROP_LINE_WIDTH, arcWidget.getAdlBasicAttribute().getWidth());
			}
			
		}

		//TODO Add Dynamic Properties to Arc2Model
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return arcModel;
	}

}
