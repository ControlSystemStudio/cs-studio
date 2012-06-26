/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFormatter;
import org.csstudio.swt.xygraph.dataprovider.ISample;

/** Adapter from a {@link ScanSample} pair (x, y)
 *  to an {@link ISample} as used by the XYGraph
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SampleAdapter implements ISample
{
    final private String device;
    final private ScanSample x, y;

    /** Initialize
     *  @param x
     *  @param y
     */
    public SampleAdapter(final String device, final ScanSample x, final ScanSample y)
    {
        this.device = device;
        this.x = x;
        this.y = y;
    }

    /** {@inheritDoc} */
    @Override
    public double getXValue()
    {
        return ScanSampleFormatter.asDouble(x);
    }

    /** {@inheritDoc} */
    @Override
    public double getYValue()
    {
        return ScanSampleFormatter.asDouble(y);
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
        return device;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return x + ", " + y;
    }
}
