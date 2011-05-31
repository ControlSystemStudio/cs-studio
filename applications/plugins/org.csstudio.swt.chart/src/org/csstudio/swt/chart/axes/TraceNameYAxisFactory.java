/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

/** Factory for the TraceNameYAxis.
 *  @author Kay Kasemir
 */
public class TraceNameYAxisFactory extends YAxisFactory
{
    /** @return Returns a new YAxis. */
    @Override
    public YAxis createYAxis(String label, YAxisListener listener)
    {
        return new TraceNameYAxis(label, listener);
    }
}
