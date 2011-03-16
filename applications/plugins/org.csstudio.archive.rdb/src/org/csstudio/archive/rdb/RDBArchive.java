/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupHelper;
import org.csstudio.archive.rdb.engineconfig.RetentionHelper;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineHelper;
import org.csstudio.archive.rdb.internal.ChannelCache;
import org.csstudio.archive.rdb.internal.EnumMetaDataHelper;
import org.csstudio.archive.rdb.internal.NumericMetaDataHelper;
import org.csstudio.archive.rdb.internal.SQL;
import org.csstudio.archive.rdb.internal.SampleModeHelper;
import org.csstudio.archive.rdb.internal.SeverityCache;
import org.csstudio.archive.rdb.internal.StatusCache;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.IValue;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/**
 *  RDB Archive access
 *
 *  This was the initial attempt to have one RDB archive library for the
 *  sample engine, the engine config tool and the Data Browser.
 *
 *  The Data Browser now uses a separate 'archivereader' lib.
 *
 *  TODO Split this into 3 pieces: Archive configuration, data readout, data writer.
 *
 * @author Kay Kasemir
 * @author Lana Abadie (PostgreSQL)
 */
public class RDBArchive
{
    /** Status string for <code>Double.NaN</code> samples */
    final private static String NOT_A_NUMBER_STATUS = "NaN"; //$NON-NLS-1$

    /** Severity string for <code>Double.NaN</code> samples */
    final private static String NOT_A_NUMBER_SEVERITY = "INVALID"; //$NON-NLS-1$

    final private int MAX_TEXT_SAMPLE_LENGTH = RDBArchivePreferences.getMaxStringSampleLength();

    /** Database URL/user/password */
    final private String url, user, password;

    /** Use the 'main' tables or the 'staging' tables? */
    final private boolean use_staging;

    /** RDB connection */
    private RDBUtil rdb;

    /** SQL statements */
    private SQL sql;

    /** Channel (ID, name) cache */
    private ChannelCache channels;

    /** Severity (ID, name) cache */
    private SeverityCache severities;

    /** Status (ID, name) cache */
    private StatusCache stati;

    /** Prepared statement for inserting 'double' samples */
    private PreparedStatement insert_double_sample = null;

    /** Prepared statement for inserting 'double' array samples */
    private PreparedStatement insert_double_array_sample = null;

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

    private RetentionHelper retentions = null;

    private SampleMode sample_modes[] = null;

    /** Enable debugging of batch submission */
    final public boolean debug_batch = true;

    /** Local copy of batched samples, used to display batch errors */
    private final ArrayList<ChannelConfig> batched_channel =
        debug_batch ? new ArrayList<ChannelConfig>() : null;
    private final ArrayList<IValue> batched_samples =
        debug_batch ? new ArrayList<IValue>() : null;

    /** Initialize, connect to RDB.
     *
     *  @param url URL, where "jdbc:oracle_stage:" handled like
     *             "jdbc:oracle:" except that it switches to the "staging"
     *             tables.
     *  @param user RDB user (null if already in URL)
     *  @param password RDB password (null if already in URL)
     *  @throws Exception on error
     *  @see {@link RDBUtil} for syntax of URL
     */
    @SuppressWarnings("nls")
    private RDBArchive(final String url, final String user,
            final String password) throws Exception
    {
        this.use_staging = url.startsWith("jdbc:oracle_stage:");
        if (use_staging)
            this.url = "jdbc:oracle:" + url.substring(18);
        else
            this.url = url;
        this.user = user;
        this.password = password;
        connect();
    }

    /** Initialize, connect to RDB.
     *
     *  @param url URL, where "jdbc:oracle_stage:" handled like
     *             "jdbc:oracle:" except that it switches to the "staging"
     *             tables.
     *  @param user RDB user (null if already in URL)
     *  @param password RDB password (null if already in URL)
     *  @throws Exception on error
     *  @see {@link RDBUtil} for syntax of URL
     */
    public static RDBArchive connect(final String url, final String user,
            final String password) throws Exception
    {
        return new RDBArchive(url, user, password);
    }

    /** Initialize, connect to RDB.
     *
     *  @param url URL which must contain user/password info
     *  @throws Exception on error
     *  @see #connect(String, String, String)
     */
    public static RDBArchive connect(final String url) throws Exception
    {
        return connect(url, null, null);
    }

    /** Not meant for client access
     *  @return RDBUtil used for connection
     */
    public RDBUtil getRDB()
    {
        return rdb;
    }

    /** Not meant for client access
     *  @return SQL statements
     */
    public SQL getSQL()
    {
        return sql;
    }

    /** Connect to RDB */
    @SuppressWarnings("nls")
    private void connect() throws Exception
    {
        // Create new connection
        Activator.getLogger().fine("Connecting to '" + url + "' " +
                  (use_staging ? "(stage)" : "(main)"));
        rdb = RDBUtil.connect(url, user, password, false);
        sql = new SQL(rdb.getDialect(), use_staging);
        channels = new ChannelCache(this);
        severities = new SeverityCache(rdb, sql);
        stati = new StatusCache(rdb, sql);

        // TODO Remove Oracle test code
        if (false)
        {
            System.out.println("Enabling Oracle trace");
            final Statement stmt = rdb.getConnection().createStatement();
            stmt.execute("alter session set tracefile_identifier='KayTest_max'");
            stmt.execute("ALTER SESSION SET events " +
                         "'10046 trace name context forever, level 12'");
        }
        // In case of a re-connect after error, forget samples that caused error
        if (debug_batch)
        {
            batched_channel.clear();
            batched_samples.clear();
        }
    }

    /** Close and re-open the RDB connection.
     *  <p>
     *  Can be used in an attempt to recover from for example network errors.
     *  @throws Exception on error.
     */
    public void reconnect() throws Exception
    {
        close();
        connect();
    }

    /** List of statements to cancel in cancel() */
    private final ArrayList<Statement> cancellable_statements =
        new ArrayList<Statement>();

    /** Cancel an ongoing RDB query.
     *  Not supported by all queries, but should work for basic
     *  sample readout via RawSampleIterator
     */
    public void cancel()
    {
        synchronized (cancellable_statements)
        {
            for (final Statement statement : cancellable_statements)
            {
                try
                {
                    // Note that
                    //    statement.getConnection().close()
                    // does NOT stop an ongoing Oracle query!
                    // Only this seems to do it:
                    statement.cancel();
                }
                catch (final Exception ex)
                {
                    Activator.getLogger().log(Level.INFO,
                            "Attempt to cancel statment", ex); //$NON-NLS-1$
                }
            }
        }
    }

    /** Meant to be called only from within the RDBArchive code:
     *  Add a statement to the list of statements-to-cancel in cancel()
     *
     *  @param statement Statement to cancel
     *  @see #cancel()
     */
    public void addForCancellation(final Statement statement)
    {
        synchronized (cancellable_statements)
        {
            cancellable_statements.add(statement);
        }
    }

    /** Meant to be called only from within the RDBArchive code:
     *  Remove a statement to the list of statements-to-cancel in cancel()
     *
     *  @param statement Statement that should no longer be cancelled
     *  @see #cancel()
     */
    public void removeFromCancellation(final Statement statement)
    {
        synchronized (cancellable_statements)
        {
            cancellable_statements.remove(statement);
        }
    }

    /** Close the RDB connection.
     *  Clears all caches, deletes prepared statements etc.
     */
    @SuppressWarnings("nls")
    public void close()
    {
        Activator.getLogger().fine("Disconnecting from '" + url + "'");
        if (sample_modes != null)
            sample_modes = null;
        if (retentions != null)
        {
            retentions.dispose();
            retentions = null;
        }
        // Clear caches
        if (channels != null)
        {
            channels.dispose();
            channels = null;
        }
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
        // Close prepared statements
        if (insert_double_sample != null)
        {
            try
            {
                insert_double_sample.close();
            }
            catch (final Exception ex)
            {
                Activator.getLogger().log(Level.FINE, "'close' error", ex);
            }
            insert_double_sample = null;
        }
        if (insert_double_array_sample != null)
        {
            try
            {
                insert_double_array_sample.close();
            }
            catch (final Exception ex)
            {
                Activator.getLogger().log(Level.FINE, "'close' error", ex);
            }
            insert_double_array_sample = null;
        }
        if (insert_long_sample != null)
        {
            try
            {
                insert_long_sample.close();
            }
            catch (final Exception ex)
            {
                Activator.getLogger().log(Level.FINE, "'close' error", ex);
            }
            insert_long_sample = null;
        }
        if (insert_txt_sample != null)
        {
            try
            {
                insert_txt_sample.close();
            }
            catch (final Exception ex)
            {
                Activator.getLogger().log(Level.FINE, "'close' error", ex);
            }
            insert_txt_sample = null;
        }
        // Reset counters
        batched_double_inserts = 0;
        batched_double_array_inserts = 0;
        batched_long_inserts = 0;
        batched_txt_inserts = 0;
        // Disconnect from the database
        if (rdb != null)
        {
            rdb.close();
            rdb = null;
        }
    }

    /** @return Array of supported sample modes
     *  @throws Exception on error
     */
    public SampleMode [] getSampleModes() throws Exception
    {
        if (sample_modes == null)
            sample_modes = SampleModeHelper.getSampleModes(rdb, sql);
        return sample_modes;
    }

    /** @return Sample mode for ID */
    public SampleMode getSampleMode(final int id) throws Exception
    {
        getSampleModes();
        for (final SampleMode mode : sample_modes)
            if (mode.getId() == id)
                return mode;
        // Default to the first one
        return sample_modes[0];
    }

    /** Get channel by name.
     *  @param name
     *  @return Channel or <code>null</code> if not found.
     */
    public ChannelConfig getChannel(final String name) throws Exception
    {
        return channels.find(name);
    }

    /** Get existing or create new channel by name.
     *  @param name
     *  @return Channel or <code>null</code> if not found.
     */
    public ChannelConfig createChannel(final String name) throws Exception
    {
        return channels.findOrCreate(name);
    }

    /** Get all channels where name matches the patter.
     *  @param pattern Regular expression
     *  @return Array of Channels. May be empty, but not <code>null</code>
     *  @exception Exception on error
     */
    public ChannelConfig[] findChannels(final String pattern) throws Exception
    {
        // Does the pattern need patching of '\' into '\\' for MySQL
        // because its string parser handles the pattern before the MySQL regex?
        PreparedStatement sel =
            rdb.getConnection().prepareStatement(sql.channel_sel_by_pattern);
        sel.setString(1, pattern);
        ResultSet res = sel.executeQuery();
        final ArrayList<ChannelConfig> tmp_res = new ArrayList<ChannelConfig>();
        while (res.next())
        {   // channel_id, name, grp_id, smpl_mode_id, smpl_val, smpl_per
            final int group_id = res.getInt(3);

            final ChannelConfig channel =
                new ChannelConfig(this,
                        res.getInt(1), res.getString(2), group_id,
                        getSampleMode(res.getInt(4)),
                        res.getDouble(5), res.getDouble(6));
            channels.memorize(channel);
            tmp_res.add(channel);
        }
        res.close();
        res = null;
        sel.close();
        sel = null;
        final ChannelConfig result[] = new ChannelConfig[tmp_res.size()];
        return tmp_res.toArray(result);
    }

    /** Locate a severity via its ID.
     *  @param id Severity ID as used in RDB
     *  @return Severity or <code>null</code>
     *  @throws Exception on error
     */
    public Severity getSeverity(final int id) throws Exception
    {
        return severities.find(id);
    }

    /** Locate a status via its ID.
     *  @param id Status ID as used in RDB
     *  @return Status, never <code>null</code>
     *  @throws Exception on error
     */
    public String getStatusString(final int id) throws Exception
    {
        final Status status = stati.find(id);
        if (status != null)
            return status.getName();
        return ""; //$NON-NLS-1$
    }

    /** Add a sample to the archive.
     *  <p>
     *  For performance reasons, this call actually only adds
     *  the sample to a 'batch'.
     *  Need to follow up with 'commitBatch()' when done.
     *  @param channelId Channel id to which this sample belongs
     *  @param sample
     *  @throws Exception on error
     *  @see #commitBatch()
     */
    public void batchSample(final int channelId,
                            final IValue sample) throws Exception
    {
        final Timestamp stamp = sample.getTime().toSQLTimestamp();
        final Severity severity =
                    severities.findOrCreate(sample.getSeverity().toString());
        final Status status = stati.findOrCreate(sample.getStatus());
        if (sample instanceof IDoubleValue)
        {
            final double dbl[] = ((IDoubleValue)sample).getValues();
            batchDoubleSamples(channelId, stamp, severity, status, dbl);
        }
        else if (sample instanceof ILongValue)
        {
            final long num = ((ILongValue)sample).getValue();
            batchLongSamples(channelId, stamp, severity, status, num);
        }
        else if (sample instanceof IEnumeratedValue)
        {   // Enum handled just like (long) integer
            final long num = ((IEnumeratedValue)sample).getValue();
            batchLongSamples(channelId, stamp, severity, status, num);
        }
        else
        {   // Handle string and possible other types as strings
            final String txt = sample.format();
            batchTextSamples(channelId, stamp, severity, status, txt);
        }
    }

    public void debugBatch(final ChannelConfig channelConfig, final IValue sample)
    {
        if (debug_batch) {
            batched_channel.add(channelConfig);
            batched_samples.add(sample);
        }
    }

    /** Write the meta data of the sample, and update the channel's info. */
    public void writeMetaData(final ChannelConfig channel, final IValue sample)
	    throws Exception
    {
        if (sample instanceof IEnumeratedValue)
        {
            // Clear numeric meta data, set enumerated in RDB
            NumericMetaDataHelper.set(this, channel, null);
            final IEnumeratedMetaData meta =
                (IEnumeratedMetaData)sample.getMetaData();
            EnumMetaDataHelper.set(this, channel, meta);
            channel.setMetaData(meta);
        }
        else if (sample instanceof IStringValue)
        {
            // Strings have no meta data. But we don't know at this point
            // if it's really a string channel, or of this is just a special
            // string value like "disconnected".
            // In order to not delete any existing meta data,
            // we just do nothing.
        }
        else // One of the numeric types
        {
            // Clear enumerated meta data, set numeric
            EnumMetaDataHelper.set(this, channel, null);
            final INumericMetaData meta =
                (INumericMetaData)sample.getMetaData();
            NumericMetaDataHelper.set(this, channel, meta);
            channel.setMetaData(meta);
        }
    }

    /** Helper for batchSample: Add double sample(s) to batch. */
    private void batchDoubleSamples(final int channelId,
            final Timestamp stamp, final Severity severity,
            final Status status, final double dbl[]) throws Exception
    {
        if (insert_double_sample == null)
        {
            insert_double_sample =
                rdb.getConnection().prepareStatement(sql.sample_insert_double);
            insert_double_sample.setQueryTimeout(RDBArchivePreferences.getSQLTimeout());
        }
        // Catch not-a-number, which JDBC (at least Oracle) can't handle.
        if (Double.isNaN(dbl[0]))
        {
            insert_double_sample.setDouble(5, 0.0);
            completeAndBatchInsert(insert_double_sample,
                    channelId, stamp,
                    severities.findOrCreate(NOT_A_NUMBER_SEVERITY),
                    stati.findOrCreate(NOT_A_NUMBER_STATUS));
        }
        else
        {
            insert_double_sample.setDouble(5, dbl[0]);
            completeAndBatchInsert(insert_double_sample, channelId, stamp, severity, status);
        }
        ++batched_double_inserts;
        // More array elements?
        if (dbl.length > 1)
        {
            if (insert_double_array_sample == null)
                insert_double_array_sample =
                    rdb.getConnection().prepareStatement(
                            sql.sample_insert_double_array_element);
            for (int i = 1; i < dbl.length; i++)
            {
                insert_double_array_sample.setInt(1, channelId);
                insert_double_array_sample.setTimestamp(2, stamp);
                insert_double_array_sample.setInt(3, i);
                // Patch NaN.
                // Conundrum: Should we set the status/severity to indicate NaN?
                // Would be easy if we wrote the main sample with overall
                // stat/sevr at the end.
                // But we have to write it first to avoid index (key) errors
                // with the array sample time stamp....
                // Go back and update the main sample after the fact??
                if (Double.isNaN(dbl[i]))
                    insert_double_array_sample.setDouble(4, 0.0);
                else
                    insert_double_array_sample.setDouble(4, dbl[i]);
                // MySQL nanosecs
                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
                    insert_double_array_sample.setInt(5, stamp.getNanos());

                // Batch
                insert_double_array_sample.addBatch();
                ++batched_double_array_inserts;
            }
        }
    }

    /** Helper for batchSample: Add long sample to batch.
     *  TODO support arrays of long?
     */
    private void batchLongSamples(final int channelId,
            final Timestamp stamp, final Severity severity,
            final Status status, final long num) throws Exception
    {
       if (insert_long_sample == null)
       {
           insert_long_sample =
               rdb.getConnection().prepareStatement(sql.sample_insert_int);
           insert_long_sample.setQueryTimeout(RDBArchivePreferences.getSQLTimeout());
       }
       insert_long_sample.setLong(5, num);
       completeAndBatchInsert(insert_long_sample, channelId, stamp, severity, status);
       ++batched_long_inserts;
    }

    /** Helper for batchSample: Add text sample to batch. */
    private void batchTextSamples(final int channelId,
            final Timestamp stamp, final Severity severity,
            final Status status, final String txt) throws Exception
    {
        if (insert_txt_sample == null)
        {
            insert_txt_sample =
                rdb.getConnection().prepareStatement(sql.sample_insert_string);
            insert_txt_sample.setQueryTimeout(RDBArchivePreferences.getSQLTimeout());
        }
        if (txt.length() > MAX_TEXT_SAMPLE_LENGTH )
            insert_txt_sample.setString(5, txt.substring(0, MAX_TEXT_SAMPLE_LENGTH));
        else
            insert_txt_sample.setString(5, txt);
        completeAndBatchInsert(insert_txt_sample, channelId, stamp, severity, status);
        ++batched_txt_inserts;
    }

    /** Helper for batchSample:
     *  Set the parameters common to all insert statements, add to batch.
     */
    private void completeAndBatchInsert(
            final PreparedStatement insert_xx, final int channelId,
            final Timestamp stamp, final Severity severity,
            final Status status) throws Exception
    {
        // Set the stuff that's common to each type
        insert_xx.setInt(1, channelId);
        insert_xx.setTimestamp(2, stamp);
        insert_xx.setInt(3, severity.getId());
        insert_xx.setInt(4, status.getId());
        // MySQL nanosecs
        if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
            insert_xx.setInt(6, stamp.getNanos());
        // Batch
        insert_xx.addBatch();
    }



    /** Commit samples that might have been added to a batch.
     *  @see ChannelConfig#batchSample()
     */
    public void commitBatch() throws Exception
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
                    checkBatchExecution(insert_double_array_sample);
                }
                finally
                {
                    batched_double_array_inserts = 0;
                }
            }
        }
        catch (final Exception ex)
        {
            if (debug_batch)
            {
                if (ex.getMessage().contains("unique")) //$NON-NLS-1$
                {
                    System.out.println(new Date().toString() + " Unique constraint error in these samples: " + ex.getMessage()); //$NON-NLS-1$
                    if (batched_samples.size() != batched_channel.size())
                        System.out.println("Inconsistent batch history"); //$NON-NLS-1$
                    final int N = Math.min(batched_samples.size(), batched_channel.size());
                    for (int i=0; i<N; ++i)
                    {
                        attemptSingleInsert(batched_channel.get(i), batched_samples.get(i));
                    }
                }
            }
            throw ex;
        }
        finally
        {
            if (debug_batch)
            {
                batched_channel.clear();
                batched_samples.clear();
            }
        }
    }

    /** Submit and clear the batch, or roll back on error */
    @SuppressWarnings("nls")
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
                System.out.println("Clear batch/commit error after batch issue: "
                        + nested.getMessage());
            }
            throw ex;
        }
    }

    /** The batched insert failed, so try to insert this channel's sample
     *  individually, mostly to debug errors
     *  @param channel
     *  @param sample
     */
    @SuppressWarnings("nls")
    private void attemptSingleInsert(final ChannelConfig channel,
            final IValue sample)
    {
        System.out.println("Individual insert of " + channel.getName() + " = " + sample.toString());
        try
        {
            final Timestamp stamp = sample.getTime().toSQLTimestamp();
            final Severity severity =
                        severities.findOrCreate(sample.getSeverity().toString());
            final Status status = stati.findOrCreate(sample.getStatus());
            if (sample instanceof IDoubleValue)
            {
                final IDoubleValue dbl = (IDoubleValue) sample;
                if (dbl.getValues().length > 1)
                    throw new Exception("Not checking array samples");
                if (Double.isNaN(dbl.getValue()))
                    throw new Exception("Not checking NaN values");
                insert_double_sample.setInt(1, channel.getId());
                insert_double_sample.setTimestamp(2, stamp);
                insert_double_sample.setInt(3, severity.getId());
                insert_double_sample.setInt(4, status.getId());
                insert_double_sample.setDouble(5, dbl.getValue());
                // MySQL nanosecs
                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
                    insert_double_sample.setInt(6, stamp.getNanos());
                insert_double_sample.executeUpdate();
            }
            else if (sample instanceof ILongValue)
            {
                final ILongValue num = (ILongValue) sample;
                if (num.getValues().length > 1)
                    throw new Exception("Not checking array samples");
                insert_long_sample.setInt(1, channel.getId());
                insert_long_sample.setTimestamp(2, stamp);
                insert_long_sample.setInt(3, severity.getId());
                insert_long_sample.setInt(4, status.getId());
                insert_long_sample.setLong(5, num.getValue());
                // MySQL nanosecs
                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
                    insert_long_sample.setInt(6, stamp.getNanos());
                insert_long_sample.executeUpdate();
            }
            else if (sample instanceof IEnumeratedValue)
            {   // Enum handled just like (long) integer
                final IEnumeratedValue num = (IEnumeratedValue) sample;
                if (num.getValues().length > 1)
                    throw new Exception("Not checking array samples");
                insert_long_sample.setInt(1, channel.getId());
                insert_long_sample.setTimestamp(2, stamp);
                insert_long_sample.setInt(3, severity.getId());
                insert_long_sample.setInt(4, status.getId());
                insert_long_sample.setLong(5, num.getValue());
                // MySQL nanosecs
                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
                    insert_long_sample.setInt(6, stamp.getNanos());
                insert_long_sample.executeUpdate();
            }
            else
            {   // Handle string and possible other types as strings
                final String txt = sample.format();
                insert_txt_sample.setInt(1, channel.getId());
                insert_txt_sample.setTimestamp(2, stamp);
                insert_txt_sample.setInt(3, severity.getId());
                insert_txt_sample.setInt(4, status.getId());
                insert_txt_sample.setString(5, txt);
                // MySQL nanosecs
                if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
                    insert_txt_sample.setInt(6, stamp.getNanos());
                insert_txt_sample.executeUpdate();
            }
            rdb.getConnection().commit();
        }
        catch (Exception ex)
        {
            System.out.println("Individual insert failed: " + ex.getMessage());
        }
    }

    /** Add a sample engine config.
     *  <p>
     *  <b>Note:</b> In case that engine already exists, its current
     *  configuration is deleted (all channels and groups removed).
     *  Existing archived samples are preserved.
     * @param name Engine name (used for lookup)
     * @param description Any one-line description
     * @param url URL (host, port) where engine is supposed to run
     * @return SampleEngineInfo
     */
    public SampleEngineConfig addEngine(final String name, final String description,
            final String url) throws Exception
    {
        final SampleEngineHelper engines = new SampleEngineHelper(this);
        return engines.add(name, description, url);
    }

    /** Locate a sample engine config.
     *  @param name Engine name
     *  @return SampleEngineInfo or <code>null</code> if not found
     */
    public SampleEngineConfig findEngine(final String name) throws Exception
    {
        final SampleEngineHelper engines = new SampleEngineHelper(this);
        return engines.find(name);
    }

    /** Find engine by ID
     *  @param engine_id ID of engine to locate
     *  @return SampleEngineInfo or <code>null</code> when not found
     *  @throws Exception on error
     */
    public SampleEngineConfig findEngine(final int engine_id) throws Exception
    {
        final SampleEngineHelper engines = new SampleEngineHelper(this);
        return engines.find(engine_id);
    }

    /** Locate a certain type of retention
     *  @param description Retention name
     *  @return Retention
     */
    public Retention getRetention(final String description) throws Exception
    {
        if (retentions == null)
            retentions = new RetentionHelper(rdb, sql);
        return retentions.getRetention(description);
    }

    /** Find group by ID
     *  @param group_id Group ID
     *  @return ChannelGroup or <code>null</code>
     *  @throws Exception on error
     */
    public ChannelGroupConfig findGroup(int group_id) throws Exception
    {
        final ChannelGroupHelper groups = new ChannelGroupHelper(this);
        return groups.find(group_id);
    }

    /** Add group
     *  @param engine SampleEngineInfo
     *  @param group_name Name that identifies the group
     *  @return ChannelGroup
     *  @throws Exception on error
     */
    public ChannelGroupConfig addGroup(final SampleEngineConfig engine,
            final String group_name) throws Exception
    {
        final ChannelGroupHelper groups = new ChannelGroupHelper(this);
        return groups.add(group_name, engine.getId(), 0);
    }
}
