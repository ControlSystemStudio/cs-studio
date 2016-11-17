/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.swt.graphics.GC;

/** Tick marks of an X or Y Axis.
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
public interface Ticks<XTYPE>
{
    /** How many percent of the available space should be used for labels? */
    final public static int FILL_PERCENTAGE = 60;

    /** Used to check if a requested new range
     *  can be handled
     *  @param low Desired low limit of the axis range.
     *  @param high Desired high limit of the axis range.
     *  @return <code>true</code> if that range can be handled,
     *          <code>false</code> if that range should be avoided.
     */
    public boolean isSupportedRange(XTYPE low, XTYPE high);

    /** Compute tick information.
     *
     *  @param low Low limit of the axis range.
     *  @param high High limit of the axis range.
     *  @param gc GC for determining width of labels.
     *  @param screen_width Width of axis on screen.
     */
    public void compute(XTYPE low, XTYPE high, GC gc, int screen_width);

    /** @return Returns the value of the start tick. */
    public XTYPE getStart();

    /** @return Returns the previous tick, before a given tick mark. */
    public XTYPE getPrevious(XTYPE tick);

    /** @return Returns the next tick, following a given tick mark. */
    public XTYPE getNext(XTYPE tick);

    /** @return Number of minor tick marks */
    public int getMinorTicks();

    /** @return Returns the tick formatted as text. */
    public String format(XTYPE tick);

    /** @return Returns the tick formatted as text. */
    public String formatDetailed(XTYPE tick);
}
