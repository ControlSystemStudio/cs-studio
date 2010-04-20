package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.TextEntryWidget;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class TextEntry2Model extends AbstractADL2Model {
	TextInputModel textInModel = new TextInputModel();

	public TextEntry2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(textInModel, true);
		TextEntryWidget textEntryWidget = new TextEntryWidget(adlWidget);
		if (textEntryWidget != null) {
			setADLObjectProps(textEntryWidget, textInModel);
			setADLControlProps(textEntryWidget, textInModel);
		}
		OPIFont font = textInModel.getFont();
		int fontSize = TranslatorUtils.convertTextHeightToFontSize(textInModel.getHeight());
		FontData fontData = font.getFontData();
		FontData newFontData = new FontData(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		newFontData.setHeight(fontSize);
		textInModel.setPropertyValue(LabelModel.PROP_FONT, newFontData);
		//TODO Add limits to TextEntry2Model
		//TODO Add format to TextEntry2Model
		//TODO Add color mode to TextEntry2Model
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return textInModel;
	}

}
