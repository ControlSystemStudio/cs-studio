/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;

/**The line border which allows versatile line style:
 * SWT.LINE_SOLID, SWT.LINE_DASH, SWT.LINE_DOT, SWT.LINE_DASHDOT or
 * SWT.LINE_DASHDOTDOT.
 * @author Xihui Chen
 *
 */
public class VersatileLineBorder extends LineBorder {

    private int lineStyle;

    /**
     *
     * @param borderColor the border color
     * @param lineWidth the line width in pixels
     * @param lineStyle the line style, which must be one of the constants
     * SWT.LINE_SOLID, SWT.LINE_DASH, SWT.LINE_DOT, SWT.LINE_DASHDOT or
     * SWT.LINE_DASHDOTDOT.
     */
    public VersatileLineBorder(Color borderColor, int lineWidth, int lineStyle) {
        super(borderColor, lineWidth);
        this.lineStyle = lineStyle;
    }

    /**
     * @see org.eclipse.draw2d.Border#paint(IFigure, Graphics, Insets)
     */
    @Override
    public void paint(IFigure figure, Graphics graphics, Insets insets) {
        tempRect.setBounds(getPaintRectangle(figure, insets));
        if ((getWidth() & 1) == 1) {
            tempRect.width--;
            tempRect.height--;
        }
        tempRect.shrink(getWidth() / 2, getWidth() / 2);
        graphics.setLineWidth(getWidth());
        graphics.setLineStyle(lineStyle);
        if (getColor() != null)
            graphics.setForegroundColor(getColor());
        graphics.drawRectangle(tempRect);
    }


}
