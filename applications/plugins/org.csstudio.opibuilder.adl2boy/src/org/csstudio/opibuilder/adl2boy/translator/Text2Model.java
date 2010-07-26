package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextWidget;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class Text2Model extends AbstractADL2Model {
	LabelModel labelModel = new LabelModel();

	public Text2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel){
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(labelModel, true);
		TextWidget textWidget = new TextWidget(adlWidget);
		if (textWidget != null) {
			setADLObjectProps(textWidget, labelModel);
			setADLBasicAttributeProps(textWidget, labelModel, true);
			setADLDynamicAttributeProps(textWidget, labelModel);
			if (textWidget.getTextix() != null ){
				labelModel.setText(textWidget.getTextix());
			}
		}
		labelModel.setPropertyValue(LabelModel.PROP_TRANSPARENT, true);
		OPIFont font = labelModel.getFont();
		int fontSize = TranslatorUtils.convertTextHeightToFontSize(labelModel.getHeight());
		FontData fontData = font.getFontData();
		FontData newFontData = new FontData(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		newFontData.setHeight(fontSize);
		labelModel.setPropertyValue(LabelModel.PROP_FONT, newFontData);

		//TODO Add Alignment to Text2Model
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return labelModel;
	}

}
