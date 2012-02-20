/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Byte;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Enum;
import gov.aps.jca.dbr.DBR_Float;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.GR;
import gov.aps.jca.dbr.PRECISION;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/** Helper for dealing with DBR.. types.
 *  <p>
 *  JCA provides up to "...Int", returning an int/Integer.
 *  IValue uses long for future protocol support.
 *
 *  @author Kay Kasemir
 */
public class DBR_Helper
{
	/** All live samples are 'original' */
	final private static IValue.Quality quality = IValue.Quality.Original;

	/** Convert the EPICS time stamp (based on 1990) into the usual 1970 epoch.
     *  <p>
     *  In case this is called with data from a CTRL_... request,
     *  the null timestamp is replaced with the current host time.
     *  <p>
     *  The EPICS start of epoch as returned by IOCs for records that have never
     *  processed is mapped to 0 in the 1970 epoch so that ITimestamp.isValid()
     *  can be used to catch it.
     *  This "works", bad time stamps are identified. But it's not ideal because the
     *  original time stamp is lost. Better would be an implementation that passes 1990+0secs on,
     *  yet makes isValid() return false by using an EPICS-specific ITimestamp override of isValid().
     *  Not implemented at this time.
     */
    private static ITimestamp createTimeFromEPICS(final TimeStamp t)
    {
        if (t == null)
            return TimestampFactory.now();
        if (t.secPastEpoch() == 0  &&  t.nsec() == 0)
            return TimestampFactory.createTimestamp(0, 0);
        return TimestampFactory.createTimestamp(
                        t.secPastEpoch() + 631152000L, t.nsec());
    }

    /** @return CTRL_... type for this channel. */
    public static DBRType getCtrlType(final boolean plain, final DBRType type)
    {
        if (type.isDOUBLE())
            return plain ? DBRType.DOUBLE : DBRType.CTRL_DOUBLE;
        else if (type.isFLOAT())
            return plain ? DBRType.FLOAT : DBRType.CTRL_DOUBLE;
        else if (type.isINT())
            return plain ? DBRType.INT : DBRType.CTRL_INT;
        else if (type.isSHORT())
            return plain ? DBRType.SHORT : DBRType.CTRL_INT;
        else if (type.isBYTE())
            return plain ? DBRType.BYTE : DBRType.CTRL_BYTE;
        else if (type.isENUM())
            return plain ? DBRType.SHORT : DBRType.CTRL_ENUM;
        // default: get as string
        return plain ? DBRType.STRING : DBRType.CTRL_STRING;
    }

    /** @return Meta data extracted from dbr */
    public static IMetaData decodeMetaData(final DBR dbr)
    {
        if (dbr.isLABELS())
        {
            final DBR_LABELS_Enum labels = (DBR_LABELS_Enum)dbr;
            return ValueFactory.createEnumeratedMetaData(labels.getLabels());
        }
        else if (dbr instanceof GR)
        {
            final GR ctrl = (GR)dbr;
            return ValueFactory.createNumericMetaData(
                            ctrl.getLowerDispLimit().doubleValue(),
                            ctrl.getUpperDispLimit().doubleValue(),
                            ctrl.getLowerWarningLimit().doubleValue(),
                            ctrl.getUpperWarningLimit().doubleValue(),
                            ctrl.getLowerAlarmLimit().doubleValue(),
                            ctrl.getUpperAlarmLimit().doubleValue(),
                            (dbr instanceof PRECISION)? ((PRECISION)dbr).getPrecision() : 0,
                            ctrl.getUnits());
        }
        return null;
    }

    /** @return TIME_... type for this channel. */
    public static DBRType getTimeType(final boolean plain, final DBRType type)
    {
        if (type.isDOUBLE())
            return plain ? DBRType.DOUBLE : DBRType.TIME_DOUBLE;
        else if (type.isFLOAT())
            return plain ? DBRType.FLOAT : DBRType.TIME_FLOAT;
        else if (type.isINT())
            return plain ? DBRType.INT : DBRType.TIME_INT;
        else if (type.isSHORT())
            return plain ? DBRType.SHORT : DBRType.TIME_SHORT;
        else if (type.isENUM())
            return plain ? DBRType.SHORT : DBRType.TIME_ENUM;
        else if (type.isBYTE())
            return plain ? DBRType.BYTE: DBRType.TIME_BYTE;
        // default: get as string
        return plain ? DBRType.STRING : DBRType.TIME_STRING;
    }

    /** Convert short array to int array. */
    private static int[] short2int(final short[] v)
    {
        int result[] = new int[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }

    /** Convert short array to long array. */
    private static long[] short2long(final short[] v)
    {
        long result[] = new long[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }

    /** Convert int array to long array. */
    private static long[] int2long(final int[] v)
    {
        long result[] = new long[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }

    /** Convert byte array to long array. */
    private static long[] byte2long(final byte[] v)
    {
        long result[] = new long[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }

    /** Convert float array to a double array. */
    private static double[] float2double(final float[] v)
    {
        double result[] = new double[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }

    /** @return Value extracted from dbr */
    @SuppressWarnings("nls")
    public static IValue decodeValue(final boolean plain,
                           final IMetaData meta, final DBR dbr) throws Exception
    {
        final ITimestamp time;
        final ISeverity severity;
        final String status;

        if (dbr instanceof TIME)
        {
            final TIME tss = (TIME) dbr;
            time = createTimeFromEPICS(tss.getTimeStamp());
            severity = SeverityUtil.forCode(tss.getSeverity().getValue());
            status = decodeStatus(tss.getStatus());
        }
        else
        {
            time = TimestampFactory.now();
            severity = SeverityUtil.forCode(0);
            status = "";
        }

        if (dbr.isDOUBLE())
        {
            final double v[] = ((DBR_Double)dbr).getDoubleValue();
            return ValueFactory.createDoubleValue(time, severity,
                        status, (INumericMetaData)meta, quality, v);
        }
        else if (dbr.isFLOAT())
        {
            final float v[] = ((DBR_Float)dbr).getFloatValue();
            return ValueFactory.createDoubleValue(time, severity,
                            status, (INumericMetaData)meta, quality,
                            float2double(v));
        }
        else if (dbr.isINT())
        {
            final int v[] = ((DBR_Int)dbr).getIntValue();
            return ValueFactory.createLongValue(time, severity,
                            status, (INumericMetaData)meta, quality,
                            int2long(v));
        }
        else if (dbr.isSHORT())
        {
            final short v[] = ((DBR_Short)dbr).getShortValue();
            return ValueFactory.createLongValue(time, severity,
                                status, (INumericMetaData)meta, quality,
                                short2long(v));
        }
        else if (dbr.isSTRING())
        {
            final String v[] = ((DBR_String)dbr).getStringValue();
            return ValueFactory.createStringValue(time, severity,
                                status, quality, v);
        }
        else if (dbr.isENUM())
        {
            final short v[] = ((DBR_Enum)dbr).getEnumValue();
            return ValueFactory.createEnumeratedValue(time, severity,
                                status, (IEnumeratedMetaData)meta, quality,
                                short2int(v));
        }
        else if (dbr.isBYTE())
        {
            final byte[] v = ((DBR_Byte)dbr).getByteValue();
            return ValueFactory.createLongValue(time, severity,
                                status, (INumericMetaData)meta, quality,
                                byte2long(v));
        }
        else
            // handle many more types!!
            throw new Exception("Cannot decode " + dbr);
    }

    /** @return String for Status */
    final private static String decodeStatus(final Status status)
    {
        if (status.getValue() == 0)
            return "OK"; //$NON-NLS-1$
        return status.getName();
    }

    /** Create value with updated meta data
     *  @param value Original value
     *  @param meta New meta data
     *  @return Updated value or <code>null</code> if update is not possible
     */
    public static IValue updateMetadata(final IValue value, final IMetaData meta)
    {
        if (value instanceof IDoubleValue  &&  meta instanceof INumericMetaData)
        {
            final IDoubleValue v = (IDoubleValue) value;
            return ValueFactory.createDoubleValue(v.getTime(), v.getSeverity(), v.getStatus(),
                    (INumericMetaData) meta, v.getQuality(), v.getValues());
        }
        else if (value instanceof IEnumeratedValue  &&  meta instanceof IEnumeratedMetaData)
        {
            final IEnumeratedValue v = (IEnumeratedValue) value;
            return ValueFactory.createEnumeratedValue(v.getTime(), v.getSeverity(), v.getStatus(),
                    (IEnumeratedMetaData) meta, v.getQuality(), v.getValues());
        }
        else if (value instanceof ILongValue  &&  meta instanceof INumericMetaData)
        {
            final ILongValue v = (ILongValue) value;
            return ValueFactory.createLongValue(v.getTime(), v.getSeverity(), v.getStatus(),
                    (INumericMetaData) meta, v.getQuality(), v.getValues());
        }
        return null;
    }
}
