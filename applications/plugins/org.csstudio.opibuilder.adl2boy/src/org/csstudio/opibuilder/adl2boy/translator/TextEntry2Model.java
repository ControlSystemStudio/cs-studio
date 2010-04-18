package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextEntryWidget;
import org.eclipse.swt.graphics.RGB;

public class TextEntry2Model extends AbstractADL2Model {
	TextInputModel textInModel = new TextInputModel();

	public TextEntry2Model(ADLWidget adlWidget, RGB[] colorMap) {
		super(adlWidget, colorMap);
		TextEntryWidget textEntryWidget = new TextEntryWidget(adlWidget);
		if (textEntryWidget != null) {
			setADLObjectProps(textEntryWidget, textInModel);
			setADLControlProps(textEntryWidget, textInModel);
		}
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return textInModel;
	}

}
