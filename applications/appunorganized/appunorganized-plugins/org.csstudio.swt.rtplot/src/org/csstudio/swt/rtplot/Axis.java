/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;


/** Public Interface for X and Y axes.
 *
 *  <p>Handles the basic screen-to-value transformation.
 *
 *  @param <T> Data type of this axis
 *  @author Kay Kasemir
 */
public interface Axis<T extends Comparable<T>>
{
    /** @return Axis name */
    public String getName();

    /** @return Axis name */
    public void setName(final String name);

    /** @return Color to use for this axis */
    public RGB getColor();

    /** @param color Color to use for this axis */
    public void setColor(final RGB color);

    /** @return Color to use for this axis */
    public Font getLabelFont();

    /** @param color Color to use for this axis */
    public void setLabelFont(final FontData fontData);

    /** @return Color to use for this axis */
    public Font getScaleFont();

    /** @param color Color to use for this axis */
    public void setScaleFont(final FontData font);

    /** @return <code>true</code> if grid lines are drawn */
    public boolean isGridVisible();

    /** @param visible Should grid be visible? */
    public void setGridVisible(final boolean grid);

    /** @return <code>true</code> if indicator line is drawn */
    public boolean isIndicatorLineVisible();

    /** @param visible Should indicator line be visible? */
    public void setIndicatorLineVisible(final boolean state);

    /** @return <code>true</code> if axis is visible */
    public boolean isVisible();

    /** @param visible Should axis be visible? */
    public void setVisible(final boolean visible);

    /** Get the screen coordinates of the given value.
     *  <p>
     *  Values are mapped from value to screen coordinates via
     *  'transform', except for infinite values, which get mapped
     *  to the edge of the screen range.
     *
     *  @return Returns the value transformed in screen coordinates.
     */
    public int getScreenCoord(final T value);

    /** @return Returns screen coordinate transformed into a value. */
    public T getValue(final int coord);

    /** @return Returns value range. */
    public AxisRange<T> getValueRange();

    /** Set the new value range.
     *  @param low Low end of range
     *  @param high High end of range
     *  @return <code>true</code> if this actually did something.
     */
    public boolean setValueRange(final T low, final T high);

    /** @param visible Should axis name/title be visible? */
    void setAxisNameVisible(boolean visible);

    /** @return <code>true</code> if axis name/title is visible */
    boolean isAxisNameVisible();
}
