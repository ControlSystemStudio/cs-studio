/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

/** Logarithmic Transformation from x1..x2 into y1..y2 range.
 *  @author Kay Kasemir
 */
public class LogTransform implements ITransform
{
    private LinearTransform linear = new LinearTransform();

    /* @see org.csstudio.swt.chart.axes.ITransform#transform(double)
     */
    @Override
    public double transform(double x)
    {
        return linear.transform(Log10.log10(x));
    }

    /* @see org.csstudio.swt.chart.axes.ITransform#inverse(double)
     */
    @Override
    public double inverse(double y)
    {
        return Log10.pow10(linear.inverse(y));
    }

    /* @see org.csstudio.swt.chart.axes.ITransform#config(double, double, double, double)
     */
    @Override
    public void config(double x1, double x2, double y1, double y2)
    {
        linear.config(Log10.log10(x1), Log10.log10(x2), y1, y2);
    }
}
