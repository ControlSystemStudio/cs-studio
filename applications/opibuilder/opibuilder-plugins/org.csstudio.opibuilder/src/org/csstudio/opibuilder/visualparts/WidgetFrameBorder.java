/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.ui.util.SWTConstants;
import org.csstudio.ui.util.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.LabeledBorder;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.swt.graphics.Font;

/**
 * Provides for a frame-like border which contains a title bar for holding the title of a
 * Figure.
 * @author Xihui Chen
 */
public class WidgetFrameBorder
    extends CompoundBorder
    implements LabeledBorder
{


{
    createBorders();
}

/**
 * Constructs a FrameBorder with its label set to the name of the {@link TitleBarBorder}
 * class.
 *
 * @since 2.0
 */
public WidgetFrameBorder() { }

/**
 * Constructs a FrameBorder with the title set to the passed String.
 *
 * @param label  label or title of the frame.
 * @since 2.0
 */
public WidgetFrameBorder(String label) {
    setLabel(label);
}

/**
 * Creates the necessary borders for this FrameBorder. The inner border is a
 * {@link TitleBarBorder}. The outer border is a {@link SchemeBorder}.
 *
 * @since 2.0
 */
protected void createBorders() {
    inner = new TitleBarBorder();
    outer = new VersatileLineBorder(ColorConstants.black, 1, SWTConstants.LINE_SOLID);
}

/**
 * Returns the inner border of this FrameBorder, which contains the label for the
 * FrameBorder.
 *
 * @return  the border holding the label.
 * @since 2.0
 */
protected LabeledBorder getLabeledBorder() {
    return (LabeledBorder)inner;
}

/**
 * @return the label for this border
 */
public String getLabel() {
    return getLabeledBorder().getLabel();
}

/**
 * Sets the label for this border.
 * @param label the label
 */
public void setLabel(String label) {
    getLabeledBorder().setLabel(label);
}

/**
 * Sets the font for this border's label.
 * @param font the font
 */
public void setFont(Font font) {
    getLabeledBorder().setFont(font);
}

}
