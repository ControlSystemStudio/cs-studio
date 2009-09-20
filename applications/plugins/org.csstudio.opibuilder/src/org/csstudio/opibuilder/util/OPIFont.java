package org.csstudio.opibuilder.util;

import org.eclipse.swt.graphics.FontData;

/**The dedicated font type which supports predefined font name in OPI builder font file.
 * If the font name doesn't exist in the color file, the system font will be adopted.
 * @author Xihui Chen
 *
 */
public class OPIFont{


	private String fontName;
	
	private FontData fontData;
	
	private boolean preDefined;	
	
	public OPIFont(String fontName) {
		this.fontName = fontName;
		this.fontData = MediaService.getInstance().getFontData(fontName);
		preDefined = true;
	}
	
	public OPIFont(FontData fontData){
		this.fontName = fontData.toString();
		setFontData(fontData);
	}
	
	
	
	public OPIFont(String name, FontData fontData) {
		this.fontName = name;
		this.fontData = fontData;
		preDefined = true;
	}

	public String getFontName() {
		return fontName;
	}
	
	/**
	 * @return the rgb value of the color. null if the predefined color does not exist.
	 */
	public FontData getFontData() {
		return fontData;
	}
	
	/**
	 * @return true if this color is predefined in color file, false otherwise.
	 */
	public boolean isPreDefined() {
		return preDefined;
	}
	
	public void setFontName(String fontName) {
		this.fontName = fontName;
		this.fontData = MediaService.getInstance().getFontData(fontName);
		preDefined = true;
	}
	
	public void setFontData(FontData fontdata) {
		this.fontData = fontdata;
		preDefined = false;
	}

	
	@Override
	public String toString() {
		return fontName;
	}
	
}
