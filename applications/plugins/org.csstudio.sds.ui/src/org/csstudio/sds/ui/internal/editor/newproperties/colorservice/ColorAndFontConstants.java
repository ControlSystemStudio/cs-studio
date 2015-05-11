package org.csstudio.sds.ui.internal.editor.newproperties.colorservice;

public enum ColorAndFontConstants {

    STYLE_TAG("style"),
    COLOR_TAG("color"),
    FONT_TAG("font"),
    NAME_TAG("name"),
    DESCRIPTION_TAG("description"),
    COLOR_HEX_TAG("value"),
    FONT_NAME_TAG("fontName"),
    FONT_SIZE_TAG("fontSize"),
    FONT_BOLD_TAG("bold"),
    FONT_ITALIC_TAG("italic"),
    ;

    private final String _identifier;

    private ColorAndFontConstants(String identifier) {
        _identifier = identifier;
    }

    public String getIdentifier() {
        return _identifier;
    }

}
