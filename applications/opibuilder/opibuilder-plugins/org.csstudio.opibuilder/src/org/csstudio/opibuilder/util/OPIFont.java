/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**The dedicated font type which supports predefined font name in OPI builder font file.
 * If the font name doesn't exist in the color file, the system font will be adopted.
 * @author Xihui Chen
 *
 */
public class OPIFont{

    public static final int POINTS_PER_INCH = 72;

    private String fontName;

    private FontData rawFontData;

    private boolean preDefined;

    /**
     * Whether to override the standard font size interpretation
     * and use the font size to determine the number of vertical
     * pixels used in rendering the text.
     */
    private boolean heightInPixels = false;

    /*
     * All constructors are package-private to allow access only by MediaService.
     * When heightInPixels is provided, ensure that the rawFontData is scaled back
     * to the equivalent in points if necessary.
     */

    OPIFont(String name, FontData fontData) {
        this.fontName = name;
        this.rawFontData = fontData;
        preDefined = true;
    }

    OPIFont(FontData fontData) {
        this.fontName = fontData.toString();
        this.rawFontData = fontData;
        preDefined = false;
    }

    OPIFont(String name, FontData fontData, boolean heightInPixels) {
        this(name, scaleFontData(fontData, heightInPixels));
        this.heightInPixels = heightInPixels;
    }

    OPIFont(FontData fontData, boolean heightInPixels) {
        this(scaleFontData(fontData, heightInPixels));
        this.heightInPixels = heightInPixels;
    }

    public OPIFont(OPIFont opiFont) {
        this(opiFont.getFontMacroName(), opiFont.rawFontData);
    }

    private static int pointsToPixels(int points) {
        return points * POINTS_PER_INCH / Display.getDefault().getDPI().y;
    }

    private static int pixelsToPoints(int pixels) {
        return pixels * Display.getDefault().getDPI().y / POINTS_PER_INCH;
    }

    /**
     * If FontData is provided with height in pixels, rescale it and return the
     * 'raw' FontData with height in points.
     * @param fontData  the provided FontData
     * @param heightInPixels whether the FontData is representing pixels or points
     * @return the rescaled FontData
     */
    private static FontData scaleFontData(FontData fontData, boolean heightInPixels) {
        if (heightInPixels) {
            return new FontData(fontData.getName(), pixelsToPoints(fontData.getHeight()), fontData.getStyle());
        } else {
            return fontData;
        }
    }

    /**Returns the Macro Name of the OPIFont.
     * @return the predefined font macro name or
     * flattened font data string if it is not predefined.
     */
    public String getFontMacroName() {
        return fontName;
    }

    /**
     * Returns the name of the Font.
     * On platforms that support font foundries, the return value will
     * be the foundry followed by a dash ("-") followed by the face name.
     *
     * @return the name of the font
     *
     */
    public String getFontName(){
        return getFontData().getName();
    }

    /**
     * Returns the height of the font in either fonts or pixels depending
     * on the value of heightInPixels.
     *
     * @return the height of the font.
     *
     */
    public int getHeight(){
        return getFontData().getHeight();
    }

    /**
     * Returns the style of the receiver which is a bitwise OR of
     * one or more of the <code>SWT</code> constants NORMAL(0), BOLD(2)
     * and ITALIC(1).
     *
     * @return the style of the font.
     *
     */
    public int getStyle(){
        return getFontData().getStyle();
    }

    /**
     * Return the FontData, scaled according to whether its size represents
     * pixels or points.
     * @return the scaled FontData
     */
    public FontData getFontData() {
        int height = rawFontData.getHeight();
        if (this.heightInPixels) {
            height = pointsToPixels(height);
        }
        return new FontData(rawFontData.getName(), height, rawFontData.getStyle());
    }

    /**
     * Return the raw FontData, not scaled.
     * @return the raw FontData.
     */
    public FontData getRawFontData() {
        return this.rawFontData;
    }

    /**
     * Return the appropriately-scaled SWT font.
     * @return scaled SWT font
     */
    public Font getSWTFont(){
        return CustomMediaFactory.getInstance().getFont(getFontData());
    }

    /**
     * @return true if this font is predefined in font file, false otherwise.
     */
    public boolean isPreDefined() {
        return preDefined;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
        OPIFont cachedFont =  MediaService.getInstance().getOPIFont(fontName);
        this.rawFontData = cachedFont.getFontData();
        this.heightInPixels = cachedFont.getFontPixels();
        preDefined = true;
    }

    @Override
    public String toString() {
        return fontName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rawFontData == null) ? 0 : rawFontData.hashCode());
        result = prime * result + ((fontName == null) ? 0 : fontName.hashCode());
        result = prime * result + ((this.heightInPixels) ? 0 : 1);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OPIFont other = (OPIFont) obj;
        if (other.heightInPixels != heightInPixels) {
            return false;
        }
        if (rawFontData == null) {
            if (other.rawFontData != null)
                return false;
        } else if (!rawFontData.equals(other.rawFontData))
            return false;
        if (fontName == null) {
            if (other.fontName != null)
                return false;
        } else if (!fontName.equals(other.fontName))
            return false;
        return true;
    }

    public void setFontPixels(boolean heightInPixels) {
        this.heightInPixels = heightInPixels;
    }

    public boolean getFontPixels() {
        return heightInPixels;
    }

}
