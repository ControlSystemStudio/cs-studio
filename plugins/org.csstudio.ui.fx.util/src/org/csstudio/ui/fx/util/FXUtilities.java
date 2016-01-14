package org.csstudio.ui.fx.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * <code>Utilities</code> provides a set of utility methods for various java FX features.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class FXUtilities {

    private FXUtilities() {
    }

    /**
     * Transforms the SWT colour to a hex string representing that colour.
     *
     * @param rgb the original colour to transform
     * @return hex representation of the colour
     */
    public static final String toHexColor(RGB rgb) {
        return Integer.toHexString(rgb.red) + Integer.toHexString(rgb.green)
            + Integer.toHexString(rgb.blue);
    }

    /**
     * Transforms the SWT colour to a hex string representing that colour and returns a java FX CSS style for the
     * node background using that colour.
     *
     * @param colour the original colour to transform
     * @return CSS style for a node background
     */
    public static final String toBackgroundColorStyle(Color colour) {
        return "-fx-background-color: #" + FXUtilities.toHexColor(colour.getRGB()) + ";";
    }

    /**
     * Transforms the SWT colour to a hex string representing that colour and returns a java FX CSS style for the
     * node background using that colour.
     *
     * @param colour the original colour to transform
     * @return CSS style for a node background
     */
    public static final String toBackgroundColorStyle(RGB rgb) {
        return "-fx-background-color: #" + FXUtilities.toHexColor(rgb) + ";";
    }

    /**
     * Measures the width of the string when displayed with the given font.
     *
     * @param text the text to measure the width of
     * @param font the font to use for measurement
     * @return the width of the text in pixels
     */
    public static int measureStringWidth(String text, Font font) {
        Text mText = new Text(text);
        if (font != null) {
            mText.setFont(font);
        }
        return (int) mText.getLayoutBounds().getWidth();
    }
}
