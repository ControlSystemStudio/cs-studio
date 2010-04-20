package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextIndicatorModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextUpdateWidget;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class TextUpdate2Model extends AbstractADL2Model {
	TextIndicatorModel textInModel = new TextIndicatorModel();

	public TextUpdate2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(textInModel, true);

		TextUpdateWidget textUpdateWidget = new TextUpdateWidget(adlWidget);
		if (textUpdateWidget != null) {
			setADLObjectProps(textUpdateWidget, textInModel);
			setADLMonitorProps(textUpdateWidget, textInModel);
		}
		OPIFont font = textInModel.getFont();
		int fontSize = TranslatorUtils.convertTextHeightToFontSize(textInModel.getHeight());
		FontData fontData = font.getFontData();
		FontData newFontData = new FontData(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		newFontData.setHeight(fontSize);
		textInModel.setPropertyValue(LabelModel.PROP_FONT, newFontData);
		//TODO Add Alignment to TextUpdate2Model
		//TODO Add limits to TextUpdate2Model
		//TODO Add format to TextUpdate2Model
		//TODO Add color mode to TextUpdate2Model
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return textInModel;
	}

}
