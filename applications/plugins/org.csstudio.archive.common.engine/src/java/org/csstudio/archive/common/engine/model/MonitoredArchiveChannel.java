/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.domain.desy.alarm.IHasAlarm;
import org.csstudio.domain.desy.types.ICssValueType;
import org.csstudio.platform.data.IValue;

/** An ArchiveChannel that stores each incoming value.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MonitoredArchiveChannel<V,
                                     T extends ICssValueType<V> & IHasAlarm> extends ArchiveChannel<V, T> {

//    private static final Logger LOG =
//            CentralLogger.getInstance().getLogger(MonitoredArchiveChannel.class);

    /** Estimated period of change in seconds */
    //final private double period_estimate;

    /** @throws Exception
     * @see ArchiveChannel#ArchiveChannel(String, int, IValue) */
    public MonitoredArchiveChannel(final String name,
//                                   final Enablement enablement,
//                                   final int buffer_capacity,
//                                   final IValue last_archived_value,
//                                   final double period_estimate,
                                   final ArchiveChannelId channelId) throws Exception {
        super(name, channelId);
    }


    @Override
    public String getMechanism() {
        //return "on change [" + PeriodFormat.formatSeconds(period_estimate) + "]";
        return "MONITOR (on change)";// + PeriodFormat.formatSeconds(period_estimate) + "]";
    }

    /** Attempt to add each new value to the buffer. */
//    @Override
//    protected boolean handleNewValue(final ICssAlarmValueType<V> value)
//    {
//        if (super.handleNewValue(value))
//        {
//            LOG.debug(getName() + " wrote first sample " + value);
//            return true;
//        }
//        if (isEnabled())
//        {
//            LOG.debug(getName() + " writes " + value);
//            addValueToBuffer(value);
//            return true;
//        }
//        return false;
//    }
}
