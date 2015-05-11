package org.csstudio.sds.ui.internal.editor.newproperties.colorservice;

import org.csstudio.sds.util.ColorAndFontUtil;
import org.eclipse.swt.graphics.RGB;

/**
 * Describes a named color.
 *
 * @author Sven Wende
 *
 */
public class NamedColor {
    private String name;
    private String hex;
    private String description;

    public NamedColor(String name, String description, String hex) {
        assert name != null : "name != null";
        assert name.trim().length() > 0 : "name.trim().length() > 0";
        assert description != null : "description != null";
        assert description.trim().length() > 0 : "description.trim().length() > 0";
        assert hex != null : "hex != null";
        assert hex.trim().length() > 0 : "hex.trim().length() > 0";

        this.name = name;
        this.description = description;
        this.hex = hex;
    }

    public RGB getRgb() {
        return toRgb(hex);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHex() {
        return hex;
    }

    private static RGB toRgb(String hex) {
        assert ColorAndFontUtil.isHex(hex);
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        return new RGB(r, g, b);
    }
}