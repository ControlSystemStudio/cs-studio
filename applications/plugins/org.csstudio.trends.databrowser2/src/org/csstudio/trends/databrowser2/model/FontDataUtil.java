package org.csstudio.trends.databrowser2.model;

import org.eclipse.swt.SWT;

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
	
}
