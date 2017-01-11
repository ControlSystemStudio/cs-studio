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

/**The dedicated font type which supports predefined font name in OPI builder font file.
 * If the font name doesn't exist in the color file, the system font will be adopted.
 * @author Xihui Chen
 *
 */
public class OPIFont{


    private String fontName;

    private FontData fontData;

    private boolean preDefined;

    OPIFont(String fontName) {
        this.fontName = fontName;
        this.fontData = MediaService.getInstance().getOPIFont(fontName).getFontData();
        preDefined = true;
    }

    OPIFont(FontData fontData){
        this.fontName = fontData.toString();
        setFontData(fontData);
    }



    OPIFont(String name, FontData fontData) {
        this.fontName = name;
        this.fontData = fontData;
        preDefined = true;
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
        return fontData.getName();
    }

    /**
     * Returns the height of the font in points.
     *
     * @return the height of the font.
     *
     */
    public int getHeight(){
        return fontData.getHeight();
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
        return fontData.getStyle();
    }


    /**
     * @return the fontData of the Font. null if the predefined color does not exist.
     */
    public FontData getFontData() {
        return fontData;
    }

    public Font getSWTFont(){
        return CustomMediaFactory.getInstance().getFont(fontData);
    }

    /**
     * @return true if this font is predefined in font file, false otherwise.
     */
    public boolean isPreDefined() {
        return preDefined;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
        this.fontData = MediaService.getInstance().getOPIFont(fontName).getFontData();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fontData == null) ? 0 : fontData.hashCode());
        result = prime * result + ((fontName == null) ? 0 : fontName.hashCode());
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
        if (fontData == null) {
            if (other.fontData != null)
                return false;
        } else if (!fontData.equals(other.fontData))
            return false;
        if (fontName == null) {
            if (other.fontName != null)
                return false;
        } else if (!fontName.equals(other.fontName))
            return false;
        return true;
    }



}
