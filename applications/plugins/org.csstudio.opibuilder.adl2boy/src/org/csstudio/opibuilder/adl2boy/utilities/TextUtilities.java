/**
 * 
 */
package org.csstudio.opibuilder.adl2boy.utilities;

import org.csstudio.opibuilder.adl2boy.translator.TranslatorUtils;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextUpdateModel;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ITextWidget;
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
		int borderWidth= textModel.getBorderWidth();
		int fontSize = convertTextHeightToFontSize(textModel
				.getHeight() - 2*borderWidth);
		fontSize = fontSize-((int)(fontSize/10))*3;
		FontData fontData = font.getFontData();
		FontData newFontData = new FontData(fontData.getName(),
				fontData.getHeight(), fontData.getStyle());
		newFontData.setHeight(fontSize);
		textModel.setPropertyValue(LabelModel.PROP_FONT, newFontData);
	}

	/**
	 * @param textModel
	 *            Model of BOY widget to be modified
	 * @param adlTextWidget
	 *            Model of ADL widget.  Sourve of the data
	 * 
	 */
	public static void setAlignment(LabelModel textModel,
			ADLAbstractWidget adlTextWidget) {
		if (adlTextWidget.getName().equals("text")
				|| adlTextWidget.getName().equals("text update")) {
			String alignment = ((ITextWidget)adlTextWidget).getAlignment();
			if (alignment.equals("")||
					alignment.equals("horiz. left")){
				textModel.setPropertyValue(LabelModel.PROP_ALIGN_H, 0);
			}
			else if (alignment.equals("horiz. centered")){
				textModel.setPropertyValue(LabelModel.PROP_ALIGN_H, 1);
				
			}
			else if (alignment.equals("horiz.right")){
				textModel.setPropertyValue(LabelModel.PROP_ALIGN_H, 2);

			}
		}

	}

	/**
	 * @param textModel
	 *            Model of BOY widget to be modified
	 * @param adlTextWidget
	 *            Model of ADL widget.  Sourve of the data
	 * 
	 */
	public static void setFormat(TextUpdateModel textModel,
			ADLAbstractWidget adlTextWidget) {
		if (adlTextWidget.getName().equals("text entry")
				|| adlTextWidget.getName().equals("text update")) {
			String format = ((ITextWidget)adlTextWidget).getFormat();
			if (format.equals("")||
					format.equals("decimal")){
				textModel.setPropertyValue(TextUpdateModel.PROP_FORMAT_TYPE, 1);
			}
			else if (format.equals("exponential") ||
					format.equals("engr. notation")){
				textModel.setPropertyValue(TextUpdateModel.PROP_FORMAT_TYPE, 2);
			}
			else if (format.equals("hexadecimal")){
				textModel.setPropertyValue(TextUpdateModel.PROP_FORMAT_TYPE, 3);
			}
			else if (format.equals("string")){
				textModel.setPropertyValue(TextUpdateModel.PROP_FORMAT_TYPE, 4);
			}
			else if (format.equals("octal")){
				// TODO Add format to TextUtilities handle octal format
				TranslatorUtils.printNotHandledWarning("", "format - octal");
			}			
			else if (format.equals("compact")){
				textModel.setPropertyValue(TextUpdateModel.PROP_FORMAT_TYPE, 6);
			}			
			else if (format.equals("sexagesimal")){
				// TODO Add format to TextUtilities handle sexagesimal format
				TranslatorUtils.printNotHandledWarning("", "format - sexagesimal");
			}			
			else if (format.equals("sexagesimal-hms")){
				// TODO Add format to TextUtilities handle sexagesimal-hms format
				TranslatorUtils.printNotHandledWarning("", "format - sexagesimal-hms");
			}			
			else if (format.equals("sexagesimal-dms")){
				// TODO Add format to TextUtilities handle sexagesimal-dms format
				TranslatorUtils.printNotHandledWarning("", "format - sexagesimal-dms");
			}			
		}

	}

	public static int convertTextHeightToFontSize(int h){
		if (h < 9) {
			return 6;
		}
		else if (h < 10 ){
			return 6;
		}
		else if (h < 13) {
			return 8;
		}
		else if (h < 14) {
			return 9;
		}
		else if (h < 15) {
			return 10;
		}
		else if (h < 16) {
			return 12;
		}
		else if (h < 20) {
			return 14;
		}
		else if (h < 21) {
			return 16;
		}
		else if (h < 24) {
			return 18;
		}
		else if (h < 26) {
			return 18;
		}
		else if (h < 27) {
			return 20;
		}
		else if (h < 35) {
			return 24;
		}
		else if (h < 36) {
			return 26;
		}
		else {
			return 30;
		}
	}


}
