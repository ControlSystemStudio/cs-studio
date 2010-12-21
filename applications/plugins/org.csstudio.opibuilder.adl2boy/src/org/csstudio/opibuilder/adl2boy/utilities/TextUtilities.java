/**
 * 
 */
package org.csstudio.opibuilder.adl2boy.utilities;

import org.csstudio.opibuilder.adl2boy.translator.TranslatorUtils;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextIndicatorModel;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
import org.eclipse.swt.graphics.FontData;

/**
 * @author hammonds
 *
 */
public class TextUtilities {

	/**
	 * 
	 */
	public static void setWidgetFont(LabelModel textModel) {
		OPIFont font = textModel.getFont();
		int fontSize = TranslatorUtils.convertTextHeightToFontSize(textModel.getHeight());
		FontData fontData = font.getFontData();
		FontData newFontData = new FontData(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		newFontData.setHeight(fontSize);
		textModel.setPropertyValue(LabelModel.PROP_FONT, newFontData);
	}

	/**
	 * @param textModel TODO
	 * @param adlTextWidget TODO
	 * 
	 */
	public static void setAlignment(LabelModel textModel, ADLAbstractWidget adlTextWidget) {
		//TODO Add Alignment to TextUpdate2Model
		TranslatorUtils.printNotHandledWarning("TextUtilites", "Text alingnment" );
	}

	/**
	 * @param textModel TODO
	 * @param adlTextWidget TODO
	 * 
	 */
	public static void setFormat(TextIndicatorModel textModel, ADLAbstractWidget adlTextWidget) {
		//TODO Add format to TextEntry2Model
		TranslatorUtils.printNotHandledWarning("", "format" );
	}

}
