/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.TimestampHelper;
import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/** Base for ValueIterators that read from the RDB
 *  @author Kay Kasemir
 *  @author Lana Abadie (PostgreSQL)
 */
@SuppressWarnings("nls")
abstract public class AbstractRDBValueIterator  implements ValueIterator
{
    final protected RDBArchiveReader reader;
    final protected int channel_id;

    protected Display display = null;
    protected List<String> labels = null;

    /** SELECT ... for the array samples. */
    private PreparedStatement sel_array_samples = null;

    /** Before version 3.1.0, we would look for array
     *  values in the array_val table until we are sure
     *  that there are no array samples.
     *  Than we stopped looking for array samples
     *  to speed things up.
     *
     *  Since version 3.1.0, there is the option of using a
     *  BLOB and the sample table contains an is_array indicator,
     *  so we know for sure right away.
     *
     *  To remain compatible with old data, we still assume
     *  there are array values until we know otherwise.
     */
    protected boolean is_an_array = true;


    /** @param reader RDBArchiveReader
     *  @param channel_id ID of channel
     *  @throws Exception on error
     */
    AbstractRDBValueIterator(final RDBArchiveReader reader,
            final int channel_id) throws Exception
    {
        this.reader = reader;
        this.channel_id = channel_id;
        try
        {
            this.display = determineDisplay();
            this.labels = determineLabels();
        }
        catch (final Exception ex)
        {
            // Set iterator to empty
            close();
            if (! RDBArchiveReader.isCancellation(ex))
                throw ex;
            // Else: Not a real error, return empty iterator
        }
        if (labels == null  &&  display == null)
            display = ValueFactory.newDisplay(0.0, 0.0, 0.0, "", NumberFormats.format(0), 0.0, 0.0, 10.0, 0.0, 10.0);
    }

    /** @return Numeric meta data information for the channel or <code>null</code>
     *  @throws Exception on error
     */
    private Display determineDisplay() throws Exception
    {
        // Try numeric meta data
        final PreparedStatement statement =
            reader.getConnection().prepareStatement(reader.getSQL().numeric_meta_sel_by_channel);
        try
        {
            statement.setInt(1, channel_id);
            final ResultSet result = statement.executeQuery();
            if (result.next())
            {
                final NumberFormat format = NumberFormats.format(result.getInt(7));   // prec
                return ValueFactory.newDisplay(
                        result.getDouble(1),  // lowerDisplayLimit
                        result.getDouble(5),  // lowerAlarmLimit
                        result.getDouble(3),  // lowerWarningLimit
                        result.getString(8),   // units
                        format,               // numberFormat
                        result.getDouble(4),  // upperWarningLimit
                        result.getDouble(6),  // upperAlarmLimit
                        result.getDouble(2),  // upperDisplayLimit
                        result.getDouble(1),  // lowerCtrlLimit
                        result.getDouble(2)); // upperCtrlLimit

            }
        }
        finally
        {
            statement.close();
        }
        // No numeric display meta data
        return null;
    }

    /** @return Numeric meta data information for the channel or <code>null</code>
     *  @throws Exception on error
     */
    private List<String> determineLabels() throws Exception
    {
        // Try enumerated meta data
        List<String> labels = null;
        final PreparedStatement statement = reader.getConnection().prepareStatement(
                                reader.getSQL().enum_sel_num_val_by_channel);
        try
        {
            statement.setInt(1, channel_id);
            final ResultSet result = statement.executeQuery();
            if (result.next())
            {
                labels = new ArrayList<String>();
                do
                {
                    final int id = result.getInt(1);
                    final String val = result.getString(2);
                    // Expect vals for ids 0, 1, 2, ...
                    if (id != labels.size())
                        throw new Exception("Enum IDs for channel with ID "
                                + channel_id + " not in sequential order");
                    labels.add(val);
                }
                while (result.next());
            }
        }
        finally
        {
            statement.close();
        }
        // Anything found?
        if (labels == null  ||  labels.size() <= 0)
            return null; // Nothing found
        return labels;
    }

    /** Extract value from SQL result
     *  @param result ResultSet that must contain contain time, severity, ..., value
     *  @param handle_array Try to read array elements, or only a scalar value?
     *  @return IValue Decoded IValue
     *  @throws Exception on error, including cancellation
     */
    protected VType decodeSampleTableValue(final ResultSet result, final boolean handle_array) throws Exception
    {
        // Get time stamp
        final java.sql.Timestamp stamp = result.getTimestamp(1);
        // Oracle has nanoseconds in TIMESTAMP, other RDBs in separate column
        if (!reader.isOracle())
            stamp.setNanos(result.getInt(7));
        final Instant time = TimestampHelper.fromSQLTimestamp(stamp);

        // Get severity/status
        final String status = reader.getStatus(result.getInt(3));
        final AlarmSeverity severity = filterSeverity(reader.getSeverity(result.getInt(2)), status);

        // Determine the value type
        // Try double
        final double dbl0 = result.getDouble(5);
        if (! result.wasNull())
        {
            // Is it an error to have enumeration strings for double samples?
            // In here, we handle it by returning enumeration samples,
            // because the meta data would be wrong for double values.
            if (labels != null)
                return new ArchiveVEnum(time, severity, status, labels, (int) dbl0);
            // Double data.
            if (handle_array)
            {   // Get array elements - if any.
                final double data[] = reader.useArrayBlob()
                    ? readBlobArrayElements(dbl0, result)
                    : readArrayElements(time, dbl0, severity);
                if (data.length == 1)
                    return new ArchiveVNumber(time, severity, status, display, data[0]);
                else
                    return new ArchiveVNumberArray(time, severity, status, display, data);
            }
            else
                return new ArchiveVNumber(time, severity, status, display, dbl0);
        }

        // Try integer
        final int num = result.getInt(4);
        if (! result.wasNull())
        {   // Enumerated integer?
            if (labels != null)
                return new ArchiveVEnum(time, severity, status, labels, num);
            return new ArchiveVNumber(time, severity, status, display, num);
        }

        // Default to string
        final String txt = result.getString(6);
        return new ArchiveVString(time, severity, status, txt);
    }

    /** @param severity Original severity
     *  @param status Status text
     *  @return If the status indicates that there is no actual value,
     *          provide the special 'no value' severity
     */
    protected AlarmSeverity filterSeverity(final AlarmSeverity severity, final String status)
    {
        // Hard-coded knowledge:
        // When the status indicates
        // that the archive is off or channel was disconnected,
        // we use the special severity that marks a sample
        // without a value.
        if (status.equalsIgnoreCase("Archive_Off") ||
            status.equalsIgnoreCase("Disconnected") ||
            status.equalsIgnoreCase("Write_Error"))
            return AlarmSeverity.UNDEFINED;
        return severity;
    }

    /** Given the time and first element of the  sample, see if there
     *  are more array elements.
     *  @param stamp Time stamp of the sample
     *  @param dbl0 Value of the first (maybe only) array element
     *  @param severity Severity of the sample
     *  @return Array with given element and maybe more.
     *  @throws Exception on error, including 'cancel'
     */
    private double[] readArrayElements(final Instant stamp,
            final double dbl0,
            final AlarmSeverity severity) throws Exception
    {
        // For performance reasons, only look for array data until we hit a scalar sample.
        if (is_an_array==false)
            return new double [] { dbl0 };

        // See if there are more array elements
        if (sel_array_samples == null)
        {   // Lazy initialization
            sel_array_samples = reader.getConnection().prepareStatement(
                    reader.getSQL().sample_sel_array_vals);
        }
        sel_array_samples.setInt(1, channel_id);
        sel_array_samples.setTimestamp(2, TimestampHelper.toSQLTimestamp(stamp));
        // MySQL keeps nanoseconds in designated column, not TIMESTAMP
        if (! reader.isOracle())
            sel_array_samples.setInt(3, stamp.getNano());

        // Assemble array of unknown size in ArrayList ....
        final ArrayList<Double> vals = new ArrayList<Double>();
        reader.addForCancellation(sel_array_samples);
        try
        {
            final ResultSet res = sel_array_samples.executeQuery();
            vals.add(new Double(dbl0));
            while (res.next())
                vals.add(res.getDouble(1));
            res.close();
        }
        finally
        {
            reader.removeFromCancellation(sel_array_samples);
        }
        // Convert to plain double array
        final int N = vals.size();
        final double ret[] = new double[N];
        for (int i = 0; i < N; i++)
            ret[i] = vals.get(i).doubleValue();
        // Check if it's in fact just a scalar, and a valid one
        if (N == 1  &&  severity != AlarmSeverity.UNDEFINED)
        {   // Found a perfect non-array sample:
            // Assume that the data is scalar, skip the array check from now on
            is_an_array = false;
        }
        return ret;
    }

    /** See if there are array elements.
     *  @param dbl0 Value of the first (maybe only) array element
     *  @param result ResultSet for the sample table with blob
     *  @return Array with given element and maybe more.
     *  @throws Exception on error, including 'cancel'
     */
    private double[] readBlobArrayElements(final double dbl0, final ResultSet result) throws Exception
    {
        final String datatype;
        if (reader.isOracle())
            datatype = result.getString(7);
        else
            datatype = result.getString(8);

        // ' ' or NULL indicate: Scalar, not an array
        if (datatype == null || " ".equals(datatype) || result.wasNull())
            return new double [] { dbl0 };

        // Decode BLOB
        final byte[] bytes = result.getBytes(reader.isOracle() ? 8 : 9);
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final DataInputStream data = new DataInputStream(stream);
        if ("d".equals(datatype))
        {    // Read Double typed array elements
            final int nelm = data.readInt();
            final double[] array = new double[nelm];
            for (int i = 0; i < nelm; i++)
                array[i] = data.readDouble();
            data.close();
            return array;
        }
        // TODO Decode 'l' Long and 'i' Integer?
        else
        {
            throw new Exception("Sample BLOBs of type '" + datatype + "' are not decoded");
        }
    }

    /** @param result ResultSet positioned on row to dump to console
     *  @throws Exception on error
     */
    protected void dumpResultSet(final ResultSet result) throws Exception
    {
        final ResultSetMetaData meta = result.getMetaData();
        final int N = meta.getColumnCount();
        for (int i=1; i<=N; ++i)
        {
            if (i > 1)
                System.out.print(", ");
            if (meta.getColumnName(i).equals("SMPL_TIME"))
                System.out.print(meta.getColumnName(i) + ": " + TimestampHelper.fromSQLTimestamp(result.getTimestamp(i)));
            else
                System.out.print(meta.getColumnName(i) + ": " + result.getString(i));
        }
        System.out.println();
    }

    /** Release all database resources.
     *  OK to call more than once.
     */
    @Override
    public void close()
    {
        if (sel_array_samples != null)
        {
            try
            {
                sel_array_samples.close();
            }
            catch (Exception ex)
            {
                // Ignore
            }
            sel_array_samples = null;
        }
    }
}
