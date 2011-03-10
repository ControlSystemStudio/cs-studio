/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

import org.csstudio.swt.chart.axes.XAxisListener;
import org.csstudio.swt.chart.axes.YAxisListener;

/** Listener for a Chart.
 *  @see org.csstudio.swt.chart.Chart
 *  @author Kay Kasemir
 */
public interface ChartListener extends XAxisListener, YAxisListener
{
    /** The Chart is about to zoom or pan, so an application might want to
     *  take a snapshot of axis settings for 'undo'.
     *  @param description What kind of zoom or pan is about to happen?
     */
    public void aboutToZoomOrPan(String description);
    
    /** A coordinate was selected.
     *  Somebody clicked the mouse on the graph area.
     *  @param x, y Selected screen coordinates
     */
    public void pointSelected(int x, int y);
}
