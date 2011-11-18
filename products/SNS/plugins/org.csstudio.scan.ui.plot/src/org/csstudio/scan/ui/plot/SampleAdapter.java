/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.scan.data.DataFormatter;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.swt.xygraph.dataprovider.ISample;

/** Adapter from a {@link ScanSample} pair (x, y)
 *  to an {@link ISample} as used by the XYGraph
 *  @author Kay Kasemir
 */
public class SampleAdapter implements ISample
{
    final private ScanSample x, y;
    
    /** Initialize
     *  @param x
     *  @param y
     */
    public SampleAdapter(final ScanSample x, final ScanSample y)
    {
        this.x = x;
        this.y = y;
    }

    /** {@inheritDoc} */
    @Override
    public double getXValue()
    {
        return DataFormatter.toDouble(x);
    }

    /** {@inheritDoc} */
    @Override
    public double getYValue()
    {
        return DataFormatter.toDouble(y);
    }

    /** {@inheritDoc} */
    @Override
    public double getXPlusError()
    {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public double getYPlusError()
    {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public double getXMinusError()
    {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public double getYMinusError()
    {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public String getInfo()
    {
        final String xinfo = x == null ? "X" : x.getDeviceName();
        final String yinfo = y == null ? "Y" : y.getDeviceName();
        return yinfo + " over " + xinfo;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return x + ", " + y;
    }
}
