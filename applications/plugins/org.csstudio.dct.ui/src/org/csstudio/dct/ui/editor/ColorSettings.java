package org.csstudio.dct.ui.editor;

import org.eclipse.swt.graphics.RGB;

/**
 * Contains constants for colors and fonts in the DCT application.
 *
 * @author Sven Wende
 *
 */
public final class ColorSettings {
    private ColorSettings() {
    }

    public static final RGB OVERRIDDEN_VALUE = new RGB(6, 0, 238);
    public static final RGB INHERITED_VALUE = new RGB(128, 128, 128);

    public static final RGB INHERITED_PARAMETER_VALUE = new RGB(128, 128, 128);
    public static final RGB OVERRIDDEN_PARAMETER_VALUE = new RGB(6, 0, 238);

    public static final RGB MODIFYABLE = new RGB(0, 0, 0);
    public static final RGB UNMODIFYABLE = new RGB(215, 215, 0);
}
