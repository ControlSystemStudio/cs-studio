/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.archive.vtype.MetaDataHelper;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.archive.writer.ArchiveWriter;
import org.csstudio.archive.writer.WriteChannel;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;

/** ArchiveWriter implementation for RDB
 *  @author Kay Kasemir
 *  @author Lana Abadie - PostgreSQL for original RDBArchive code. Disable autocommit as needed.
 *  @author Laurent Philippe (Use read-only connection when possible for MySQL load balancing)
 */
@SuppressWarnings("nls")
public class RDBArchiveWriter implements ArchiveWriter
{
    /** Status string for <code>Double.NaN</code> samples */
    final private static String NOT_A_NUMBER_STATUS = "NaN";

    final private int SQL_TIMEOUT_SECS = RDBArchivePreferences.getSQLTimeoutSecs();

    final private int MAX_TEXT_SAMPLE_LENGTH = Preferences.getMaxStringSampleLength();

    final private boolean use_array_blob;

    /** RDB connection */
    final private RDBUtil rdb;

    /** SQL statements */
    final private SQL sql;

    /** Cache of channels by name */
    final private Map<String, RDBWriteChannel> channels = new HashMap<String, RDBWriteChannel>();

    /** Severity (ID, name) cache */
    private SeverityCache severities;

    /** Status (ID, name) cache */
    private StatusCache stati;

    /** Prepared statement for inserting 'double' samples */
    private PreparedStatement insert_double_sample = null;

    /** Prepared statement for inserting array samples */
    private PreparedStatement insert_array_sample = null;

    /** Prepared statement for inserting 'long' samples */
    private PreparedStatement insert_long_sample = null;

    /** Prepared statement for inserting 'String' samples */
    private PreparedStatement insert_txt_sample = null;

    /** Counter for accumulated samples in 'double' batch */
    private int batched_double_inserts = 0;

    /** Counter for accumulated samples in 'double array' batch */
    private int batched_double_array_inserts = 0;

    /** Counter for accumulated samples in 'long' batch */
    private int batched_long_inserts = 0;

    /** Counter for accumulated samples in 'String' batch */
    private int batched_txt_inserts = 0;

    /** Copy of batched samples, used to display batch errors */
    private final List<RDBWriteChannel> batched_channel = new ArrayList<RDBWriteChannel>();
    private final List<VType> batched_samples = new ArrayList<VType>();

    /** Initialize from preferences.
     *  This constructor will be invoked when an {@link ArchiveWriter}
     *  is created via the extension point.
     *  @throws Exception on error, for example RDB connection error
     */
    public RDBArchiveWriter() throws Exception
    {
        this(RDBArchivePreferences.getURL(), RDBArchivePreferences.getUser(),
                RDBArchivePreferences.getPassword(), RDBArchivePreferences.getSchema(),
                RDBArchivePreferences.useArrayBlob());
    }

    /** Initialize
     *  @param url RDB URL
     *  @param user .. user name
     *  @param password .. password
     *  @param schema Schema/table prefix, not including ".". May be empty
     *  @param use_array_blob Use BLOB for array elements?
     *  @throws Exception on error, for example RDB connection error
     */
    public RDBArchiveWriter(final String url, final String user, final String password,
            final String schema, boolean use_array_blob) throws Exception
    {
        this.use_array_blob = use_array_blob;
        rdb = RDBUtil.connect(url, user, password, false);
        sql = new SQL(rdb.getDialect(), schema);
        severities = new SeverityCache(rdb, sql);
        stati = new StatusCache(rdb, sql);

        // JDBC and RDBUtil default to auto-commit being on.
        //
        // The batched submission of samples, however, requires
        // auto-commit to be off, so this code assumes that
        // auto-commit is off, then enables it briefly as needed,
        // and otherwise commits/rolls back.
        rdb.getConnection().setAutoCommit(false);
    }

    @Override
    public WriteChannel getChannel(final String name) throws Exception
    {
        // Check cache
        RDBWriteChannel channel = channels.get(name);
        if (channel == null)
        {    // Get channel information from RDB
            final Connection connection = rdb.getConnection();
            // connection.setReadOnly(true);
            connection.setAutoCommit(true);
            final PreparedStatement statement = connection.prepareStatement(sql.channel_sel_by_name);
            try
            {
                statement.setString(1, name);
                final ResultSet result = statement.executeQuery();
                if (!result.next())
                    throw new Exception("Unknown channel " + name);
                channel = new RDBWriteChannel(name, result.getInt(1));
                result.close();
                channels.put(name, channel);
            }
            finally
            {
                statement.close();
                // connection.setReadOnly(false);
                connection.setAutoCommit(false);
            }
        }
        return channel;
    }

    @Override
    public void addSample(final WriteChannel channel, final VType sample) throws Exception
    {
        final RDBWriteChannel rdb_channel = (RDBWriteChannel) channel;
        writeMetaData(rdb_channel, sample);
        batchSample(rdb_channel, sample);
        batched_channel.add(rdb_channel);
        batched_samples.add(sample);
    }

    /** Write meta data if it was never written or has changed
     *  @param channel Channel for which to write the meta data
     *  @param sample Sample that may have meta data to write
     */
    private void writeMetaData(final RDBWriteChannel channel, final VType sample) throws Exception
    {
        // Note that Strings have no meta data. But we don't know at this point
        // if it's really a string channel, or of this is just a special
        // string value like "disconnected".
        // In order to not delete any existing meta data,
        // we just do nothing for strings

        if (sample instanceof Display)
        {
            final Display display = (Display)sample;
            if (MetaDataHelper.equals(display, channel.getMetadata()))
                return;

            // Clear enumerated meta data, replace numeric
            EnumMetaDataHelper.delete(rdb, sql, channel);
            NumericMetaDataHelper.delete(rdb, sql, channel);
            NumericMetaDataHelper.insert(rdb, sql, channel, display);
            rdb.getConnection().commit();
            channel.setMetaData(display);
        }
        else if (sample instanceof VEnum)
        {
            final List<String> labels = ((VEnum)sample).getLabels();
            if (MetaDataHelper.equals(labels, channel.getMetadata()))
                return;

            // Clear numeric meta data, set enumerated in RDB
            NumericMetaDataHelper.delete(rdb, sql, channel);
            EnumMetaDataHelper.delete(rdb, sql, channel);
            EnumMetaDataHelper.insert(rdb, sql, channel, labels);
            rdb.getConnection().commit();
            channel.setMetaData(labels);
        }
    }

    /**
     * Create a new prepared statement. For PostgreSQL connections, this method
     * create a PGCopyPreparedStatement to improve insert speed using COPY
     * insetad of INSERT.
     *
     * @param sqlQuery
     * @return
     * @throws SQLException
     * @throws Exception
     */
    @SuppressWarnings("resource")
    private PreparedStatement createInsertPrepareStatement(String sqlQuery)
            throws SQLException, Exception {
        PreparedStatement statement = null;
        if (rdb.getDialect() == Dialect.PostgreSQL
                && Preferences.isUsePostgresCopy()) {
            statement = new PGCopyPreparedStatement(rdb.getConnection(),
                    sqlQuery);
        } else {
            statement = rdb.getConnection().prepareStatement(sqlQuery);
        }
        if (SQL_TIMEOUT_SECS > 0)
            statement.setQueryTimeout(SQL_TIMEOUT_SECS);
        return statement;
    }

    /** Perform 'batched' insert for sample.
     *  <p>Needs eventual flush()
     *  @param channel Channel
     *  @param sample Sample to insert
     *  @throws Exception on error
     */
    private void batchSample(final RDBWriteChannel channel, final VType sample) throws Exception
    {
        final Timestamp stamp = TimestampHelper.toSQLTimestamp(VTypeHelper.getTimestamp(sample));
        final int severity = severities.findOrCreate(VTypeHelper.getSeverity(sample));
        final Status status = stati.findOrCreate(VTypeHelper.getMessage(sample));

        // Severity/status cache may enable auto-commit
        if (rdb.getConnection().getAutoCommit() == true)
            rdb.getConnection().setAutoCommit(false);

        // Start with most likely cases and highest precision: Double, ...
        // Then going down in precision to integers, finally strings...
        if (sample instanceof VDouble)
            batchDoubleSamples(channel, stamp, severity, status, ((VDouble)sample).getValue(), null);
        else if (sample instanceof VNumber)
        {    // Write as double or integer?
            final Number number = ((VNumber)sample).getValue();
            if (number instanceof Double)
                batchDoubleSamples(channel, stamp, severity, status, number.doubleValue(), null);
            else
                batchLongSample(channel, stamp, severity, status, number.longValue());
        }
        else if (sample instanceof VNumberArray)
        {
            final ListNumber data = ((VNumberArray)sample).getData();
            if (data.size() > 0)
                batchDoubleSamples(channel, stamp, severity, status, data.getDouble(0), data);
            else
                batchDoubleSamples(channel, stamp, severity, status, Double.NaN, data);
        }
        else if (sample instanceof VEnum)
            batchLongSample(channel, stamp, severity, status, ((VEnum)sample).getIndex());
        else if (sample instanceof VString)
            batchTextSamples(channel, stamp, severity, status, ((VString)sample).getValue());
        else // Handle possible other types as strings
            batchTextSamples(channel, stamp, severity, status, sample.toString());
    }

    /** Helper for batchSample: Add double sample(s) to batch. */
    private void batchDoubleSamples(final RDBWriteChannel channel,
            final Timestamp stamp, final int severity,
            final Status status, final double dbl, final ListNumber additional) throws Exception
    {
        if (use_array_blob)
            batchBlobbedDoubleSample(channel, stamp, severity, status, dbl, additional);
        else
            oldBatchDoubleSamples(channel, stamp, severity, status, dbl, additional);
    }

    /** Helper for batchSample: Add double sample(s) to batch, using
     *  blob to store array elements.
     */
    private void batchBlobbedDoubleSample(final RDBWriteChannel channel,
            final Timestamp stamp, int severity,
            Status status, final double dbl, final ListNumber additional) throws Exception
    {
        if (insert_double_sample == null)
        {
            insert_double_sample = createInsertPrepareStatement(sql.sample_insert_double_blob);
        }
        // Set scalar or 1st element of a waveform.
        // Catch not-a-number, which JDBC (at least Oracle) can't handle.
        if (Double.isNaN(dbl))
        {
            insert_double_sample.setDouble(5, 0.0);
            severity = severities.findOrCreate(AlarmSeverity.UNDEFINED);
            status = stati.findOrCreate(NOT_A_NUMBER_STATUS);
        }
        else
            insert_double_sample.setDouble(5, dbl);

        if (additional == null)
        {    // No more array elements, only scalar
            switch (rdb.getDialect())
            {
            case Oracle:
                insert_double_sample.setString(6, " ");
                insert_double_sample.setNull(7, Types.BLOB);
                break;
            case PostgreSQL:
                insert_double_sample.setString(7, " ");
                insert_double_sample.setBytes(8, null);
                break;
            default:
                // Types.BINARY?
                insert_double_sample.setString(7, " ");
                insert_double_sample.setNull(8, Types.BLOB);
            }
        }
        else
        {   // More array elements
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream(bout);
            // Indicate 'Double' as data type
            final int N = additional.size();
            dout.writeInt(N);
            // Write binary data for array elements
            for (int i=0; i<N; ++i)
                dout.writeDouble(additional.getDouble(i));
            dout.close();
            final byte[] asBytes = bout.toByteArray();
            if (rdb.getDialect() == Dialect.Oracle)
            {
                insert_double_sample.setString(6, "d");
                insert_double_sample.setBytes(7, asBytes);
            }
            else
            {
                insert_double_sample.setString(7, "d");
                insert_double_sample.setBytes(8, asBytes);
            }
        }
        // Batch
        completeAndBatchInsert(insert_double_sample, channel, stamp, severity, status);
        ++batched_double_inserts;
    }

    /** Add 'insert' for double samples to batch, handling arrays
     *  via the original array_val table
     */
    private void oldBatchDoubleSamples(final RDBWriteChannel channel,
            final Timestamp stamp, final int severity,
            final Status status, final double dbl, final ListNumber additional) throws Exception
    {
        if (insert_double_sample == null)
        {
            insert_double_sample = createInsertPrepareStatement(sql.sample_insert_double);
        }
        // Catch not-a-number, which JDBC (at least Oracle) can't handle.
        if (Double.isNaN(dbl))
        {
            insert_double_sample.setDouble(5, 0.0);
            completeAndBatchInsert(insert_double_sample,
                    channel, stamp,
                    severities.findOrCreate(AlarmSeverity.UNDEFINED),
                    stati.findOrCreate(NOT_A_NUMBER_STATUS));
        }
        else
        {
            insert_double_sample.setDouble(5, dbl);
            completeAndBatchInsert(insert_double_sample, channel, stamp, severity, status);
        }
        ++batched_double_inserts;
        // More array elements?
        if (additional != null)
        {
            if (insert_array_sample == null)
                insert_array_sample =
                    rdb.getConnection().prepareStatement(
                        sql.sample_insert_double_array_element);
            final int N = additional.size();
            for (int i = 1; i < N; i++)
            {
                insert_array_sample.setInt(1, channel.getId());
                insert_array_sample.setTimestamp(2, stamp);
                insert_array_sample.setInt(3, i);
                // Patch NaN.
                // Conundrum: Should we set the status/severity to indicate NaN?
                // Would be easy if we wrote the main sample with overall
                // stat/sevr at the end.
                // But we have to write it first to avoid index (key) errors
                // with the array sample time stamp....
                // Go back and update the main sample after the fact??
                if (Double.isNaN(additional.getDouble(i)))
                    insert_array_sample.setDouble(4, 0.0);
                else
                    insert_array_sample.setDouble(4, additional.getDouble(i));
                // MySQL nanosecs
                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
                    insert_array_sample.setInt(5, stamp.getNanos());
                // Batch
                insert_array_sample.addBatch();
                ++batched_double_array_inserts;
            }
        }
    }

    /** Helper for batchSample: Add long sample to batch.  */
    private void batchLongSample(final RDBWriteChannel channel,
            final Timestamp stamp, final int severity,
            final Status status, final long num) throws Exception
    {
        if (insert_long_sample == null)
        {
            insert_long_sample = createInsertPrepareStatement(sql.sample_insert_int);
        }
        insert_long_sample.setLong(5, num);
        completeAndBatchInsert(insert_long_sample, channel, stamp, severity, status);
        ++batched_long_inserts;
    }

    /** Helper for batchSample: Add text sample to batch. */
    private void batchTextSamples(final RDBWriteChannel channel,
            final Timestamp stamp, final int severity,
            final Status status, String txt) throws Exception
    {
        if (insert_txt_sample == null)
        {
            insert_txt_sample = createInsertPrepareStatement(sql.sample_insert_string);
        }
        if (txt.length() > MAX_TEXT_SAMPLE_LENGTH)
        {
            Activator.getLogger().log(Level.INFO,
                "Value of {0} exceeds {1} chars: {2}",
                new Object[] { channel.getName(), MAX_TEXT_SAMPLE_LENGTH, txt });
            txt = txt.substring(0, MAX_TEXT_SAMPLE_LENGTH);
        }
        insert_txt_sample.setString(5, txt);
        completeAndBatchInsert(insert_txt_sample, channel, stamp, severity, status);
        ++batched_txt_inserts;
    }

    /** Helper for batchSample:
     *  Set the parameters common to all insert statements, add to batch.
     */
    private void completeAndBatchInsert(
            final PreparedStatement insert_xx, final RDBWriteChannel channel,
            final Timestamp stamp, final int severity,
            final Status status) throws Exception
    {
        // Set the stuff that's common to each type
        insert_xx.setInt(1, channel.getId());
        insert_xx.setInt(3, severity);
        insert_xx.setInt(4, status.getId());

        if (rdb.getDialect() == Dialect.Oracle)
            insert_xx.setTimestamp(2, stamp);
        else
        {
            // Truncate the time stamp
            Timestamp truncated = Timestamp.from(stamp.toInstant());
            truncated.setNanos(0);
            insert_xx.setTimestamp(2, truncated);
            // Set the nanos in a separate column
            insert_xx.setInt(6, stamp.getNanos());
        }

        // Batch
        insert_xx.addBatch();
    }

    /** {@inheritDoc}
     *  RDB implementation completes pending batches
     */
    @Override
    public void flush() throws Exception
    {
        try
        {
            if (batched_double_inserts > 0)
            {
                try
                {
                    checkBatchExecution(insert_double_sample);
                }
                finally
                {
                    batched_double_inserts = 0;
                }
            }
            if (batched_long_inserts > 0)
            {
                try
                {
                    checkBatchExecution(insert_long_sample);
                }
                finally
                {
                    batched_long_inserts = 0;
                }
            }
            if (batched_txt_inserts > 0)
            {
                try
                {
                    checkBatchExecution(insert_txt_sample);
                }
                finally
                {
                    batched_txt_inserts = 0;
                }
            }
            if (batched_double_array_inserts > 0)
            {
                try
                {
                    checkBatchExecution(insert_array_sample);
                }
                finally
                {
                    batched_double_array_inserts = 0;
                }
            }
        }
        catch (final Exception ex)
        {
            if (ex.getMessage().contains("unique"))
            {
                System.out.println(new Date().toString() + " Unique constraint error in these samples: " + ex.getMessage()); //$NON-NLS-1$
                if (batched_samples.size() != batched_channel.size())
                    System.out.println("Inconsistent batch history");
                final int N = Math.min(batched_samples.size(), batched_channel.size());
                for (int i=0; i<N; ++i)
                    attemptSingleInsert(batched_channel.get(i), batched_samples.get(i));
            }
            throw ex;
        }
        finally
        {
            batched_channel.clear();
            batched_samples.clear();
        }
    }

    /** Submit and clear the batch, or roll back on error */
    private void checkBatchExecution(final PreparedStatement insert) throws Exception
    {
        try
        {   // Try to perform the inserts
            // In principle this could return update counts for
            // each batched insert, but Oracle 10g and 11g just throw
            // an exception
            insert.executeBatch();
            rdb.getConnection().commit();
        }
        catch (final Exception ex)
        {
            try
            {
                // On failure, roll back.
                // With Oracle 10g, the BatchUpdateException doesn't
                // indicate which of the batched commands faulted...
                insert.clearBatch();
                // Still: Commit what's committable.
                // Unfortunately no way to know what failed,
                // and no way to re-submit the 'remaining' inserts.
                rdb.getConnection().commit();
            }
            catch (Exception nested)
            {
                Activator.getLogger().log(Level.WARNING,
                        "clearBatch(), commit() error after batch issue", nested);
            }
            throw ex;
        }
    }

    /** The batched insert failed, so try to insert this channel's sample
     *  individually, mostly to debug errors
     *  @param channel
     *  @param sample
     */
    private void attemptSingleInsert(final RDBWriteChannel channel, final VType sample)
    {
        System.out.println("Individual insert of " + channel.getName() + " = " + sample.toString());
//        try
//        {
//            final Timestamp stamp = TimestampHelper.toSQLTimestamp(VTypeHelper.getTimestamp(sample));
//            final int severity = severities.findOrCreate(VTypeHelper.getSeverity(sample));
//            final Status status = stati.findOrCreate(VTypeHelper.getMessage(sample));
//            if (sample instanceof VNumber)
//            {
//                final IDoubleValue dbl = (IDoubleValue) sample;
//                if (dbl.getValues().length > 1)
//                    throw new Exception("Not checking array samples");
//                if (Double.isNaN(dbl.getValue()))
//                    throw new Exception("Not checking NaN values");
//                insert_double_sample.setInt(1, channel.getId());
//                insert_double_sample.setTimestamp(2, stamp);
//                insert_double_sample.setInt(3, severity.getId());
//                insert_double_sample.setInt(4, status.getId());
//                insert_double_sample.setDouble(5, dbl.getValue());
//                //always false as we don't insert arrays in this function
//                insert_double_sample.setBoolean(6, false);
//                // MySQL nanosecs
//                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
//                    insert_double_sample.setInt(7, stamp.getNanos());
//                insert_double_sample.executeUpdate();
//            }
//            else if (sample instanceof ILongValue)
//            {
//                final ILongValue num = (ILongValue) sample;
//                if (num.getValues().length > 1)
//                    throw new Exception("Not checking array samples");
//                insert_long_sample.setInt(1, channel.getId());
//                insert_long_sample.setTimestamp(2, stamp);
//                insert_long_sample.setInt(3, severity.getId());
//                insert_long_sample.setInt(4, status.getId());
//                insert_long_sample.setLong(5, num.getValue());
//                insert_long_sample.setBoolean(6, false);
//                // MySQL nanosecs
//                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
//                    insert_long_sample.setInt(7, stamp.getNanos());
//                insert_long_sample.executeUpdate();
//            }
//            else if (sample instanceof IEnumeratedValue)
//            {   // Enum handled just like (long) integer
//                final IEnumeratedValue num = (IEnumeratedValue) sample;
//                if (num.getValues().length > 1)
//                    throw new Exception("Not checking array samples");
//                insert_long_sample.setInt(1, channel.getId());
//                insert_long_sample.setTimestamp(2, stamp);
//                insert_long_sample.setInt(3, severity.getId());
//                insert_long_sample.setInt(4, status.getId());
//                insert_long_sample.setLong(5, num.getValue());
//                insert_long_sample.setBoolean(6, false);
//                // MySQL nanosecs
//                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
//                    insert_long_sample.setInt(7, stamp.getNanos());
//                insert_long_sample.executeUpdate();
//            }
//            else
//            {   // Handle string and possible other types as strings
//                final String txt = sample.format();
//                insert_txt_sample.setInt(1, channel.getId());
//                insert_txt_sample.setTimestamp(2, stamp);
//                insert_txt_sample.setInt(3, severity.getId());
//                insert_txt_sample.setInt(4, status.getId());
//                insert_txt_sample.setString(5, txt);
//                insert_txt_sample.setBoolean(6, false);
//                // MySQL nanosecs
//                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
//                    insert_txt_sample.setInt(7, stamp.getNanos());
//                insert_txt_sample.executeUpdate();
//            }
//            rdb.getConnection().commit();
//        }
//        catch (Exception ex)
//        {
//            System.out.println("Individual insert failed: " + ex.getMessage());
//        }
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        channels.clear();
        if (severities != null)
        {
            severities.dispose();
            severities = null;
        }
        if (stati != null)
        {
            stati.dispose();
            stati = null;
        }

        if (insert_double_sample != null) {
            try {
                insert_double_sample.close();
            } catch (SQLException e) {
                Activator.getLogger().log(Level.WARNING, "close() error", e);
            }
            insert_double_sample = null;
        }
        if (insert_array_sample != null) {
            try {
                insert_array_sample.close();
            } catch (SQLException e) {
                Activator.getLogger().log(Level.WARNING, "close() error", e);
            }
            insert_array_sample = null;
        }
        if (insert_long_sample != null) {
            try {
                insert_long_sample.close();
            } catch (SQLException e) {
                Activator.getLogger().log(Level.WARNING, "close() error", e);
            }
            insert_long_sample = null;
        }
        if (insert_txt_sample != null) {
            try {
                insert_txt_sample.close();
            } catch (SQLException e) {
                Activator.getLogger().log(Level.WARNING, "close() error", e);
            }
            insert_txt_sample = null;
        }
        rdb.close();
    }
}
