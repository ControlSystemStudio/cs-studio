package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextWidget;
import org.eclipse.swt.graphics.RGB;

public class Text2Model extends AbstractADL2Model {
	LabelModel labelModel = new LabelModel();

	public Text2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		TextWidget textWidget = new TextWidget(adlWidget);
		if (textWidget != null) {
			setADLObjectProps(textWidget, labelModel);
			setADLBasicAttributeProps(textWidget, labelModel, true);
			if (textWidget.getTextix() != null ){
				labelModel.setText(textWidget.getTextix());
			}
		}
		labelModel.setPropertyValue(LabelModel.PROP_TRANSPARENT, true);
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return labelModel;
	}

}
