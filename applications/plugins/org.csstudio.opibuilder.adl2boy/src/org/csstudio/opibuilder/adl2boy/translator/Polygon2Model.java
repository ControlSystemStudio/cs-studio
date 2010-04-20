package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.PolygonModel;
import org.csstudio.opibuilder.widgets.model.PolygonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Polygon;
import org.eclipse.swt.graphics.RGB;

public class Polygon2Model extends AbstractADL2Model {
	PolygonModel polygonModel = new PolygonModel();

	public Polygon2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(polygonModel, true);
		Polygon polygonWidget = new Polygon(adlWidget);
		if (polygonWidget != null) {
			setADLObjectProps(polygonWidget, polygonModel);
			setADLBasicAttributeProps(polygonWidget, polygonModel, false);
		}
		polygonModel.setPoints(polygonWidget.getAdlPoints().getPointsList(), true);

		//check fill parameters
		if ( polygonWidget.hasADLBasicAttribute() ) {
			if (polygonWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
				System.out.println("RECTANGLE fill is solid");				
				polygonModel.setPropertyValue(PolygonModel.PROP_TRANSPARENT, false);
				polygonModel.setPropertyValue(PolygonModel.PROP_FILL_LEVEL, 100);
				polygonModel.setPropertyValue(PolygonModel.PROP_HORIZONTAL_FILL, true);
				
			}
			else if (polygonWidget.getAdlBasicAttribute().getFill().equals("outline")) {
				polygonModel.setPropertyValue(PolygonModel.PROP_TRANSPARENT, true);
				OPIColor fColor = (OPIColor)polygonModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
				polygonModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
				if ( polygonWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
					polygonModel.setPropertyValue(PolygonModel.PROP_LINE_STYLE, "Solid");
				}
				if ( polygonWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
					polygonModel.setPropertyValue(PolygonModel.PROP_LINE_STYLE, "Dash");
					
				}
				polygonModel.setPropertyValue(PolygonModel.PROP_LINE_WIDTH, polygonWidget.getAdlBasicAttribute().getWidth());
			}
			
		}
		//TODO Add dynamic properties to Polygon2Model
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return polygonModel;
	}

}
