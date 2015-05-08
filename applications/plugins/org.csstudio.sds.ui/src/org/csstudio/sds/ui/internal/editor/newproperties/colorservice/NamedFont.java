package org.csstudio.sds.ui.internal.editor.newproperties.colorservice;

import org.csstudio.sds.util.ColorAndFontUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * Describes a named color.
 *
 * @author Sven Wende
 *
 */
public class NamedFont {
    private String name;
    private String description;

    private String fontName;
    private int fontSize;
    private boolean italic;
    private boolean bold;

    public NamedFont(String name, String description, String fontName, int fontSize, boolean bold, boolean italic) {
        assert name != null : "name != null";
        assert name.trim().length() > 0 : "name.trim().length() > 0";
        assert description != null : "description != null";
        assert description.trim().length() > 0 : "description.trim().length() > 0";
        assert fontName != null : "fontName != null";
        assert fontName.trim().length() > 0 : "fontName.trim().length() > 0";
        assert fontSize > 0 : "fontSize > 0";

        this.name = name;
        this.description = description;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.bold = bold;
        this.italic = italic;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public FontData getFontData(){
        FontData fd = new FontData();

        // .. font type
        fd.setName(fontName);

        // .. height
        if(fontSize>0) {
            fd.setHeight(fontSize);
        }

        // .. style
        int style = SWT.None;

        if(bold) {
            style|=SWT.BOLD;
        }
        if(italic) {
            style|=SWT.ITALIC;
        }
        fd.setStyle(style);

        return fd;
    }

    public String toFontString() {
        return ColorAndFontUtil.toFontString(fontName, fontSize, bold, italic);
    }
}