/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import org.csstudio.swt.rtplot.data.PlotDataItem;

/** Listener to changes in the plot
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
public interface PlotListener<XTYPE extends Comparable<XTYPE>>
{
    /** Invoked when the X axis range has changed */
    public void changedXAxis(Axis<XTYPE> x_axis);

    /** Invoked when the Y axis range has changed */
    public void changedYAxis(YAxis<XTYPE> y_axis);

    /** Invoked when Annotations have been changed */
    public void changedAnnotations();

    /** Invoked when Cursors changed */
    public void changedCursors();

    /** Invoked when toolbar displayed/hidden */
    public void changedToolbar(boolean visible);

    /** Invoked when legend displayed/hidden */
    public void changedLegend(boolean visible);    
}
