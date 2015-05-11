package org.csstudio.sds.util;

import java.util.regex.Pattern;

/**
 * Utility for color and font conversions.
 *
 * @author Sven Wende
 *
 */
public class ColorAndFontUtil {
    private static final Pattern COLOR_SYNTAX_PATTERN = Pattern.compile("#[0123456789abcdefABCDEF]{6}");
    private static final Pattern VARIABLE_SYNTAX_PATTERN = Pattern.compile("\\$\\{[a-zA-Z0-9.-]+\\}");

    public static String toFontString(String font, int size) {
        return toFontString(font, size, false, false);

    }

    public static String toFontString(String font, boolean bold, boolean italic) {
        return toFontString(font, -1, bold, italic);
    }

    public static String toFontString(String font, int size, boolean bold, boolean italic) {
        StringBuffer sb = new StringBuffer();
        sb.append(font);

        if (size > 0) {
            sb.append(", ");
            sb.append(size);
        }

        if (bold) {
            sb.append(", bold");
        }

        if (italic) {
            sb.append(", italic");
        }

        return sb.toString();
    }

    /**
     * Converts the specified rgb values to a hexadecimal representation.
     *
     * @param r
     *            red
     * @param g
     *            green
     * @param b
     *            blue
     * @return hexadecimal representation of the color
     */
    public static String toHex(int r, int g, int b) {
        return "#" + number2hex(r) + number2hex(g) + number2hex(b);
    }

    /**
     * Returns true, if the specified String is a hexadecimal color
     * representation, like #FF0000.
     *
     * @param hex
     *            the raw String
     *
     * @return true if the specified raw String is a hexadecimal color
     *         representation
     */
    public static boolean isHex(String hex) {
        if (hex != null) {
            return COLOR_SYNTAX_PATTERN.matcher(hex).matches();
        } else {
            return false;
        }
    }

    /**
     * Returns true if the specified String is a variable that can be used for
     * colors or fonts, like ${colorId}.
     *
     * @param s
     *            the raw String
     *
     * @return true if the specified String is a variable that can be used for
     *         colors or fonts
     */
    public static boolean isVariable(String s) {
        return VARIABLE_SYNTAX_PATTERN.matcher(s).matches();
    }

    private static String number2hex(int nr) {
        String hex = Integer.toHexString(nr);

        String result = hex;

        while (result.length() < 2) {
            result = '0' + result;
        }

        return result;
    }
}