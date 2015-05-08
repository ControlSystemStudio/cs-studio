package org.csstudio.sds.ui.internal.editor.newproperties.colorservice;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public interface IColorAndFontService {
    Color getColor(String hexOrVariable);
    Font getFont(String fontOrVariable);
    List<NamedColor> listAvailableColors();
    List<NamedFont> listAvailableFonts();
    List<NamedStyle> getStyles();
}
