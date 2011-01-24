/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import org.apache.log4j.Logger;
import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.logging.CentralLogger;

/** An ArchiveChannel that stores each incoming value that differs from
 *  the previous sample by some 'delta'.
 *  @author Kay Kasemir
 */
//@SuppressWarnings("nls")
//public class DeltaArchiveChannel extends ArchiveChannel
//{
//    /** 'Delta' for value change */
//    final private double delta;
//    
//    /** Estimated period of change in seconds */
//    final private double period_estimate;
//    private Logger log;
//
//    /** @param name Name of the channel (PV)
//     *  @param enablement How channel affects its groups
//     *  @param buffer_capacity Size of sample buffer
//     *  @param last_archived_value Last value from storage, or <code>null</code>.
//     *  @param period_estimate Estimated change period [seconds]
//     *  @param delta Value changes &ge; this value will be stored
//     *  @throws Exception On error in PV setup
//     */
//    public DeltaArchiveChannel(final String name,
//                               final Enablement enablement,
//                               final int buffer_capacity,
//                               final IValue last_archived_value,
//                               final double period_estimate,
//                               final double delta) throws Exception
//    {
//        super(name, enablement, buffer_capacity, last_archived_value);
//        this.delta = delta;
//        this.period_estimate = period_estimate;
//        log = CentralLogger.getInstance().getLogger(this);
//        if (! log.isDebugEnabled())
//            log = null;
//    }
//
//    @Override
//    public String getMechanism()
//    {
//        return "on delta [" + PeriodFormat.formatSeconds(period_estimate) +
//               ", " + delta + "]";
//    }
//
//    /** Attempt to add each new value to the buffer. */
//    @Override
//    protected boolean handleNewValue(final IValue value)
//    {
//        if (super.handleNewValue(value))
//        {
//            if (log != null)
//                log.debug(getName() + " wrote first sample " + value);
//            return true;
//        }
//        if (isEnabled()  &&  isBeyondDelta(value))
//        {
//            if (log != null)
//                log.debug(getName() + " writes " + value);
//            addValueToBuffer(value);
//            return true;
//        }
//        return false;
//    }
//
//    /** @param value Value to test
//     *  @return <code>true</code> if this value is beyond 'delta' from the last value 
//     */
//    private boolean isBeyondDelta(final IValue value)
//    {
//        final double number = ValueUtil.getDouble(value);
//        // Archive NaN, Inf'ty
//        if (Double.isNaN(number)  ||  Double.isInfinite(number))
//            return true;
//        double previous;
//        synchronized (this)
//        {
//            // Anything to compare against?
//            if (last_archived_value == null)
//                return true;
//            previous = ValueUtil.getDouble(last_archived_value);
//        }
//        return Math.abs(previous - number) >= delta;
//    }
//}
