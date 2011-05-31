/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

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
    public static IValue transformTimestampToNow(final IValue value)
    {
        return transformTimestamp(value, TimestampFactory.now());
    }

    /** @return Copy of given value with updated timestamp,
     *          or <code>null</code> if value is not handled
     */
    public static IValue transformTimestamp(final IValue value,
                                            final ITimestamp time)
    {
        final ISeverity severity = value.getSeverity();
        final String status = value.getStatus();
        final Quality quality = value.getQuality();
        final IMetaData meta = value.getMetaData();
        if (value instanceof IDoubleValue)
            return ValueFactory.createDoubleValue(time, severity, status,
                            (INumericMetaData)meta, quality,
                            ((IDoubleValue)value).getValues());
        if (value instanceof IStringValue)
            return ValueFactory.createStringValue(time, severity, status,
                            quality, ((IStringValue)value).getValues());
        if (value instanceof ILongValue)
            return ValueFactory.createLongValue(time, severity, status,
                            (INumericMetaData)meta, quality,
                            ((ILongValue)value).getValues());
        if (value instanceof IEnumeratedValue)
            return ValueFactory.createEnumeratedValue(time, severity, status,
                            (IEnumeratedMetaData)meta, quality,
                            ((IEnumeratedValue)value).getValues());
        return null;
    }

    /** @return Info value to indicate disabled state */
    public static IValue createDisabled()
    {
        return createInfoSample(DISABLED);
    }

    /** @return Info value to indicate disconnected state */
    public static IValue createDisconnected()
    {
        return createInfoSample(DISCONNECTED);
    }

    /** @return Info value to indicate that archive was turned off */
    public static IValue createOff()
    {
        return createInfoSample(OFF);
    }

    /** @return Info value to indicate write error */
    public static IValue createWriteError()
    {
        return createInfoSample(WRITE_ERROR);
    }

    /** Create sample with status set to some info */
    private static IValue createInfoSample(final String info)
    {
        return ValueFactory.createStringValue(
                        TimestampFactory.now(),
                        ValueFactory.createInvalidSeverity(),
                        info,
                        Quality.Original,
                        new String[] { info });
    }
}
