/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.ui.util.CSSSchemeBorder;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.SWTConstants;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.SchemeBorder.Scheme;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**The factory to create borders for {@link IFigure}
 * @author Xihui Chen
 *
 */
public class BorderFactory {

    public static AbstractBorder createBorder(BorderStyle style, int width, RGB rgbColor,
            String text){
        Color color = CustomMediaFactory.getInstance().getColor(rgbColor);

        switch (style) {
        case LINE:
            return createLineBorder(SWTConstants.LINE_SOLID, width, color);
        case RAISED:
            return createSchemeBorder(CSSSchemeBorder.SCHEMES.RAISED);
        case LOWERED:
            return createSchemeBorder(CSSSchemeBorder.SCHEMES.LOWERED);
        case ETCHED:
            return createSchemeBorder(CSSSchemeBorder.SCHEMES.ETCHED);
        case RIDGED:
            return createSchemeBorder(CSSSchemeBorder.SCHEMES.RIDGED);
        case BUTTON_RAISED:
            return createSchemeBorder(CSSSchemeBorder.SCHEMES.BUTTON_CONTRAST);
        case BUTTON_PRESSED:
            return createSchemeBorder(CSSSchemeBorder.SCHEMES.BUTTON_PRESSED);
        case DASH_DOT:
            return createLineBorder(SWTConstants.LINE_DASHDOT, width, color);
        case DASHED:
            return createLineBorder(SWTConstants.LINE_DASH, width, color);
        case DOTTED:
            return createLineBorder(SWTConstants.LINE_DOT, width, color);
        case DASH_DOT_DOT:
            return createLineBorder(SWTConstants.LINE_DASHDOTDOT, width, color);
        case GROUP_BOX:
            return createGroupBoxBorder(text, color);
        case TITLE_BAR:
            return createTitleBarBorder(text, color);
        case ROUND_RECTANGLE_BACKGROUND:
            return createRoundRectangleBorder(width, color);
        case EMPTY:
            return createEmptyBorder(width);
        case NONE:
        default:
            return null;
        }
    }

    /**
     * Creates an empty border.
     * @param width width of the border.
     * @return AbstractBorder The requested Border
     */
    private static AbstractBorder createEmptyBorder(final int width) {
        if (width>0) {
            return new AbstractBorder() {

                public Insets getInsets(IFigure figure) {
                    return new Insets(width);
                }

                public void paint(IFigure figure, Graphics graphics, Insets insets) {
                }
            };
        }
        return null;
    }

    /**
     * Creates a LineBorder.
     *
     * @return AbstractBorder The requested Border
     */
    private static AbstractBorder createLineBorder(int style, int width, Color color) {
        if (width>0) {
            LineBorder border = new VersatileLineBorder(color, width, style);
            return border;
        }
        return null;
    }
    /**
     * Creates a SchemeBorder.
     * @param scheme the scheme for the {@link SchemeBorder}
     * @return AbstractBorder The requested Border
     */
    private static AbstractBorder createSchemeBorder(final Scheme scheme) {
        SchemeBorder border = new SchemeBorder(scheme);
        return border;
    }

    private static AbstractBorder createGroupBoxBorder(String text, Color textColor) {
        AbstractLabeledBorder border = new GroupBoxBorder(text);
        border.setTextColor(textColor);
        return border;
    }

    private static AbstractBorder createTitleBarBorder(String text, Color color) {
        WidgetFrameBorder border = new WidgetFrameBorder(text);
        ((TitleBarBorder)border.getInnerBorder()).setBackgroundColor(color);
        return border;
    }

    private static AbstractBorder createRoundRectangleBorder(int width, Color color){
        RoundRectangleBackgroundBorder border = new RoundRectangleBackgroundBorder(color, width);
        return border;
    }

}
