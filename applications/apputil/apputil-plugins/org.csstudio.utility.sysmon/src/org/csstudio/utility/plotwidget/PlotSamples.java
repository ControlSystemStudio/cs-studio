/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.plotwidget;

import org.eclipse.swt.graphics.Color;

/** Interface that describes the samples to display.
 *  @author Kay Kasemir
 */
public interface PlotSamples
{
    /** @return Number of traces. */
    public int getTraceCount();

    /** @return Color for requested trace index. */
    public Color getColor(int trace);

    /** @return Number of values. */
    public int getSampleCount();
    
    /** Get a Value.
     *  @param i Value index; 0...(numValues-1)
     *  @return Array with one value per trace.
     */
    public double[] getValues(int i);
}
