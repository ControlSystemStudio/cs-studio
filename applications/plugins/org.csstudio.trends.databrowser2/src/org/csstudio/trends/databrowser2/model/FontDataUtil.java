package org.csstudio.trends.databrowser2.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

public class FontDataUtil {

	public static String fixFontInfo(String fontInfo) {
		StringBuilder fixedFontInfo = new StringBuilder();
		int start = 0;
		int end = fontInfo.indexOf('|');
		int index = 0;
		while (end >= 0) {
			if (index == 2) {
				try {
					double height = Double.parseDouble(fontInfo.substring(
							start, end));
					fixedFontInfo.append((int) height);
				} catch (NumberFormatException e) {
					SWT.error(SWT.ERROR_INVALID_ARGUMENT);
				}
			} else {
				fixedFontInfo.append(fontInfo.substring(start, end));
			}
			fixedFontInfo.append("|");
			start = end + 1;
			end = fontInfo.indexOf('|', start);

			index++;
		}

		return fixedFontInfo.toString();
	}
	
	/**
	 * Extract the name of the font from the font info. The name is stored in the
	 * second token, where individual tokens are separated by &#124;.
	 * 
	 * @param fontInfo the info to extract the font name from
	 * @return the font name if it was found or null if not found.
	 */
	public static String getFontName(String fontInfo) {
		int start = fontInfo.indexOf('|');
		int end = fontInfo.indexOf('|', start+1);
		if (end > start+1) {
			return fontInfo.substring(start+1,end);
		} else {
			return null;
		}
	}	
	
	/**
	 * Converts the fontInfo string into FontData.
	 * 
	 * @param fontInfo the font info to be converted
	 * @return the FontData representing the font info
	 */
	public static FontData getFontData(String fontInfo) {
		if (fontInfo == null) return null;
		FontData data = new FontData(fixFontInfo(fontInfo));
		//fix for Windows, which doesn't respect the name if given in the constructor
		String fontName = getFontName(fontInfo);
		if (fontName != null) data.setName(fontName);
		return data;
	}
}
