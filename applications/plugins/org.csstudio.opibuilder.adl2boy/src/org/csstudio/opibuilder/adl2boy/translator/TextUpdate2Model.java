package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.TextIndicatorModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextUpdateWidget;
import org.eclipse.swt.graphics.RGB;

public class TextUpdate2Model extends AbstractADL2Model {
	TextIndicatorModel textInModel = new TextIndicatorModel();

	public TextUpdate2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		TextUpdateWidget textUpdateWidget = new TextUpdateWidget(adlWidget);
		if (textUpdateWidget != null) {
			setADLObjectProps(textUpdateWidget, textInModel);
			setADLMonitorProps(textUpdateWidget, textInModel);
		}
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return textInModel;
	}

}
