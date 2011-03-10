/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

/** Linear Transformation from x1..x2 into y1..y2 range.
 *  @author Kay Kasemir
 */
public class LinearTransform implements ITransform
{
    private double a, b;

    /** Create default 1:1 transformation. */
    public LinearTransform()
    {
        a = 1.0;
        b = 0.0;
    }

    /* @see org.csstudio.swt.chart.axes.ITransform#transform(double)
     */
    @Override
    public double transform(double x)
    {
        return a*x + b;
    }

    /* @see org.csstudio.swt.chart.axes.ITransform#inverse(double)
     */
    @Override
    public double inverse(double y)
    {
        return (y-b)/a;
    }

    /* @see org.csstudio.swt.chart.axes.ITransform#config(double, double, double, double)
     */
    @Override
    public void config(double x1, double x2, double y1, double y2)
    {
        double d = x2 - x1;
        if (d != 0.0)
        {
            a = (y2 - y1) / d;
            b = (x2*y1 - x1*y2) / d;
            if (a != 0.0)
                return;
        }
        a = 1.0;
        b = 0.0;
    }
}
