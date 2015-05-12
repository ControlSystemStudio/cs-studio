/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

/** A simple <code>Sample</code> container.
 *  <p>
 *  Users can use this to store samples, or implement the Sample interface
 *  otherwise.
 *
 *  @see ChartSample
 *  @see ChartSampleSequence
 *
 *  @author Kay Kasemir
 */
public class ChartSampleContainer implements ChartSample
{
    final private Type type;
    final private double x;
    final private double y;
    final private double y_min, y_max;
    final private String info;

    /** Construct new sample from values.
     *  @see #ChartSampleContainer(Type, double, double, String)
     */
    public ChartSampleContainer(double x, double y)
    {
        this(Type.Normal, x, y);
    }

    /** Construct new sample from values.
     *  @see #ChartSampleContainer(Type, double, double, String)
     */
    public ChartSampleContainer(Type type, double x, double y)
    {
        this(type, x, y, y, y, null);
    }

    /** Construct new sample from values.
     *  @see #ChartSampleContainer(Type, double, double, String)
     */
    public ChartSampleContainer(Type type, double x, double y, String info)
    {
        this(type, x, y, y, y, info);
    }


    /** Construct new sample from values.
     *  @param type One of the Sample.TYPE_... values
     *  @param x X coordinate
     *  @param y Y coordinate
     *  @param y_min minimum of Y range (low error)
     *  @param y_max maximum of Y range (high error)
     *  @param info Info string, e.g. for tooltip, or <code>null</code>.
     */
    public ChartSampleContainer(Type type, double x, double y,
                                double y_min, double y_max, String info)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.y_min = y_min;
        this.y_max = y_max;
        this.info = info;
    }

    /** {@inheritDoc} */
    @Override
    public Type getType()
    {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public double getX()
    {
        return x;
    }

    /** {@inheritDoc} */
    @Override
    public double getY()
    {
        return y;
    }

    /** {@inheritDoc} */
    @Override
    public boolean haveMinMax()
    {
        return y_min != y_max;
    }

    /** {@inheritDoc} */
    @Override
    public double getMinY()
    {
        return y_min;
    }

    /** {@inheritDoc} */
    @Override
    public double getMaxY()
    {
        return y_max;
    }

    /** {@inheritDoc} */
    @Override
    public String getInfo()
    {
        return info;
    }
}
