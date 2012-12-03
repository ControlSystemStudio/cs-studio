/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import org.csstudio.archive.vtype.ArchiveVDoubleArray;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVString;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.data.VNumberArray;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VType;
import org.epics.util.array.ListNumber;
import org.epics.util.time.Timestamp;

/** Helper that does various unspeakable things to values.
 *  @author Kay Kasemir
 */
public class ValueButcher
{
    // These strings match the text representation of the
    // ChannelArchiver's informational severity codes
    // - if they existed in previous versions.

    /** Status string for 'disabled' state */
    private static final String DISABLED = "Archive_Disabled"; //$NON-NLS-1$

    /** Status string for 'disconnected' state */
    private static final String DISCONNECTED = "Disconnected"; //$NON-NLS-1$

    /** Status string for 'off' state */
    private static final String OFF = "Archive_Off"; //$NON-NLS-1$

    /** Status string for 'write error' state */
    private static final String WRITE_ERROR = "Write_Error"; //$NON-NLS-1$

    /** @return Copy of given value with timestamp set to 'now',
     *          or <code>null</code> if value is not handled
     */
    public static VType transformTimestampToNow(final VType value)
    {
        return transformTimestamp(value, Timestamp.now());
    }

    /** @return Copy of given value with updated timestamp,
     *          or <code>null</code> if value is not handled
     */
    public static VType transformTimestamp(final VType value,
                                           final Timestamp time)
    {
        if (value instanceof VNumber)
        {
        	final VNumber number = (VNumber) value;
            return new ArchiveVNumber(time, number.getAlarmSeverity(), number.getAlarmName(), number, number.getValue());
        }
        if (value instanceof VString)
        {
        	final VString string = (VString) value;
            return new ArchiveVString(time, string.getAlarmSeverity(), string.getAlarmName(), string.getValue());
        }
        if (value instanceof VDoubleArray)
        {
        	final VDoubleArray number = (VDoubleArray) value;
            return new ArchiveVDoubleArray(time, number.getAlarmSeverity(), number.getAlarmName(), number, number.getData());
        }
        if (value instanceof VNumberArray)
        {
        	final VNumberArray number = (VNumberArray) value;
        	final ListNumber data = number.getData();
        	final double[] dbl = new double[data.size()];
        	for (int i=0; i<dbl.length; ++i)
        		dbl[i] = data.getDouble(i);
            return new ArchiveVDoubleArray(time, number.getAlarmSeverity(), number.getAlarmName(), number, dbl);
        }
        if (value instanceof VEnum)
        {
        	final VEnum labelled = (VEnum) value;
            return new ArchiveVEnum(time, labelled.getAlarmSeverity(), labelled.getAlarmName(), labelled.getLabels(), labelled.getIndex());
        }
        return null;
    }

    /** @return Info value to indicate disabled state */
    public static VType createDisabled()
    {
        return createInfoSample(DISABLED);
    }

    /** @return Info value to indicate disconnected state */
    public static VType createDisconnected()
    {
        return createInfoSample(DISCONNECTED);
    }

    /** @return Info value to indicate that archive was turned off */
    public static VType createOff()
    {
        return createInfoSample(OFF);
    }

    /** @return Info value to indicate write error */
    public static VType createWriteError()
    {
        return createInfoSample(WRITE_ERROR);
    }

    /** Create sample with status set to some info */
    private static VType createInfoSample(final String info)
    {
        return new ArchiveVString(Timestamp.now(), AlarmSeverity.INVALID, info, info);
    }
}
