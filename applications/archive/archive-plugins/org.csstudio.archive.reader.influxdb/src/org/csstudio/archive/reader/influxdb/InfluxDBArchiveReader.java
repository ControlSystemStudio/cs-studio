/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb;

//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Statement;
import java.time.Instant;
import java.util.logging.Level;

import org.csstudio.archive.influxdb.InfluxDBArchivePreferences;
import org.csstudio.archive.influxdb.InfluxDBQueries;
import org.csstudio.archive.influxdb.InfluxDBQueries.DBNameMap;
import org.csstudio.archive.influxdb.InfluxDBQueries.DefaultDBNameMap;
import org.csstudio.archive.influxdb.InfluxDBResults;
import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.csstudio.archive.influxdb.InfluxDBUtil.ConnectionInfo;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.influxdb.raw.ConnectionCache;
import org.influxdb.InfluxDB;
import org.influxdb.dto.QueryResult;

/** ArchiveReader for InfluxDB data
 *  @author Megan Grodowitz
 *  @author Amanda Carpenter - implemented getOptimizedValues()
 */
@SuppressWarnings("nls")
public class InfluxDBArchiveReader implements ArchiveReader
{
    //TODO: cleanup.

    final private String url;
    final private String user;
    final private int password;
    /** Timeout when waiting for chunks of data */
    final private int timeout;
    //
    final private ConnectionCache.Entry influxdb;
    final private InfluxDBQueries influxQuery;

    final static private DBNameMap dbnames = new DefaultDBNameMap();

    ///** Map of status IDs to Status strings */
    // don't need this for influx, just store the status strings as tags
    //final private HashMap<Integer, String> stati;

    ///** Map of severity IDs to Severities */
    // don't need this for influx, just store severity strings as tags
    //final private HashMap<Integer, AlarmSeverity> severities;

    /** Initialize
     *  @param url Database URL
     *  @param user .. user
     *  @param password .. password
     *  @param schema .. schema (including ".") or ""
     *  @param stored_procedure Stored procedure or "" for client-side optimization
     *  @throws Exception on error
     */
    public InfluxDBArchiveReader(final String url)
            throws Exception
    {
        this(url, null, null);
    }

    /** Initialize
     *  @param url Database URL
     *  @param user .. user
     *  @param password .. password
     *  @param schema .. schema (including ".") or ""
     *  @param stored_procedure Stored procedure or "" for client-side optimization
     *  @param use_array_blob Use BLOB for array elements?
     *  @throws Exception on error
     */
    public InfluxDBArchiveReader(final String url, final String user, final String password)
            throws Exception
    {
        this.url = url;
        this.user = user;
        this.password = (password == null) ? 0 : password.length();

        //TODO: other Influx read optimizations?
        timeout = InfluxDBArchivePreferences.getChunkTimeoutSecs();
        influxdb = ConnectionCache.get(url, user, password);
        influxQuery = new InfluxDBQueries(influxdb.getConnection(), dbnames);
    }


    /** @return InfluxDB connection
     *  @throws Exception on error
     */
    InfluxDB getConnection() throws Exception
    {
        //TODO: No exception is thrown for closed connection. Connection is only checked on initial connect.
        return influxdb.getConnection();
    }

    public ConnectionInfo getConnectionInfo() throws Exception
    {
        return new ConnectionInfo(influxdb.getConnection());
    }

    /** @return Query statements */
    InfluxDBQueries getQueries()
    {
        return influxQuery;
    }

    int getTimeout()
    {
        return timeout;
    }

    //    /** @param status_id Numeric status ID
    //     *  @return Status string for ID
    //     */
    //    String getStatus(int status_id)
    //    {
    //        final String status = stati.get(status_id);
    //        if (status == null)
    //            return "<" + status_id + ">";
    //        return status;
    //    }
    //
    //    /** @param severity_id Numeric severity ID
    //     *  @return ISeverity for ID
    //     */
    //    AlarmSeverity getSeverity(int severity_id)
    //    {
    //        final AlarmSeverity severity = severities.get(severity_id);
    //        if (severity != null)
    //            return severity;
    //        Activator.getLogger().log(Level.WARNING, "Undefined alarm severity ID {0}", severity_id);
    //        severities.put(severity_id, AlarmSeverity.UNDEFINED);
    //        return AlarmSeverity.UNDEFINED;
    //    }

    /** {@inheritDoc} */
    @Override
    public String getServerName()
    {
        return "InfluxDB";
    }

    /** {@inheritDoc} */
    @Override
    public String getURL()
    {
        return url;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return "InfluxDB Archive V" + getVersion() + "\n" +
                "User: " + user + "\n" +
                "Password: " + password + " characters";
    }

    /** {@inheritDoc} */
    @Override
    public int getVersion()
    {
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    public ArchiveInfo[] getArchiveInfos()
    {
        return new ArchiveInfo[]
                {
                        new ArchiveInfo("influxdb", "", 1)
                };
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNamesByPattern(final int key, final String glob_pattern) throws Exception
    {
        return getNamesByRegExp(key, InfluxDBUtil.globToRegex(glob_pattern));
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNamesByRegExp(final int key, final String reg_exp) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        sb.append("^").append(reg_exp).append("$");

        final QueryResult results = influxQuery.get_newest_meta_datum_regex(sb.toString());

        if (results.hasError())
        {
            throw new Exception("Error when searching for pattern '" + reg_exp + "' : " + results.getError());
        }
        return InfluxDBResults.getMeasurements(results);
    }

    /** {@inheritDoc} */
    @Override
    public ValueIterator getRawValues(final int key, final String name,
            final Instant start, final Instant end) throws UnknownChannelException, Exception
    {
        //final int channel_id = getChannelID(name);
        return getRawValues(name, start, end);
    }

    /** Fetch raw samples
     *  @param channel_name Channel name in influxdb
     *  @param start Start time
     *  @param end End time
     *  @return {@link ValueIterator} for raw samples
     *  @throws Exception on error
     */
    public ValueIterator getRawValues(final String channel_name,
            final Instant start, final Instant end) throws Exception
    {
        return new SampleIterator(this, channel_name, start, end);
    }

    /** {@inheritDoc} */
    @Override
    public ValueIterator getOptimizedValues(final int key, final String name,
            final Instant start, final Instant end, int count) throws UnknownChannelException, Exception
    {
        // MySQL version of the stored proc. requires count > 1
        if (count <= 1)
            throw new Exception("Count must be > 1");
        //final int channel_id = getChannelID(name);

        //        // Use stored procedure in RDB server?
        //        if (stored_procedure.length() > 0)
        //            return new StoredProcedureValueIterator(this, stored_procedure, channel_id, start, end, count);
        //
        //        // Else: Determine how many samples there are
        //        final int counted;
        //        try
        //        (
        //                final PreparedStatement count_samples = rdb.getConnection().prepareStatement(
        //                        influxQuery.sample_count_by_id_start_end);
        //                )
        //        {
        //            count_samples.setInt(1, channel_id);
        //            count_samples.setTimestamp(2, TimestampHelper.toSQLTimestamp(start));
        //            count_samples.setTimestamp(3, TimestampHelper.toSQLTimestamp(end));
        //            final ResultSet result = count_samples.executeQuery();
        //            if (! result.next())
        //                throw new Exception("Cannot count samples");
        //            counted = result.getInt(1);
        //        }
        // Fetch raw data and perform averaging
        //final ValueIterator raw_data = getRawValues(name, start, end);

        //        // If there weren't that many, that's it
        //        if (counted < count)
        //            return raw_data;

        // Else: Perform averaging to reduce sample count
        //final double seconds = TimeDuration.toSecondsDouble(Duration.between(start, end)) / count;
        //return new AveragedValueIterator(raw_data, seconds);
        return getOptimizedValues(name, start, end, count);
    }

    /** Fetch optimized (downsampled) samples.
     *  @param channel_name Channel name in influxdb
     *  @param start Start time
     *  @param end End time
     *  @return {@link ValueIterator} for optimized samples
     *  @throws Exception on error
     */
    public ValueIterator getOptimizedValues(final String channel_name,
            final Instant start, final Instant end, final long count) throws Exception
    {
        try
        {
            return new OptimizedSampleIterator(this, channel_name, start, end, count);
        }
        catch (Exception e)
        {
            Activator.getLogger().log(Level.WARNING, "Could not create optimized sample iterator for " + channel_name + "; falling back to raw", e);
            return getRawValues(channel_name, start, end);
        }
    }

    //TODO: this comes from in-memory configuration object now
    /** @param name Channel name
     *  @return Numeric channel ID
     *  @throws UnknownChannelException when channel not known
     *  @throws Exception on error
     */
    // Allow access from 'package' for tests
    int getChannelID(final String name) throws UnknownChannelException, Exception
    {
        //        try
        //        (
        //                final PreparedStatement statement =
        //                rdb.getConnection().prepareStatement(influxQuery.channel_sel_by_name);
        //                )
        //        {
        //            if (timeout > 0)
        //                statement.setQueryTimeout(timeout);
        //            statement.setString(1, name);
        //            final ResultSet result = statement.executeQuery();
        //            if (!result.next())
        //                throw new UnknownChannelException(name);
        //            return result.getInt(1);
        //        }
        return 1;
    }

    //    /** Add a statement to the list of statements-to-cancel in cancel()
    //     *  @param statement Statement to cancel
    //     *  @see #cancel()
    //     */
    //    void addForCancellation(final Statement statement)
    //    {
    //        synchronized (cancellable_statements)
    //        {
    //            cancellable_statements.add(statement);
    //        }
    //    }
    //
    //    /** Remove a statement to the list of statements-to-cancel in cancel()
    //     *  @param statement Statement that should no longer be cancelled
    //     *  @see #cancel()
    //     */
    //    void removeFromCancellation(final Statement statement)
    //    {
    //        synchronized (cancellable_statements)
    //        {
    //            cancellable_statements.remove(statement);
    //        }
    //    }

    /** Check if an exception indicates Oracle operation was canceled,
     *  i.e. this program requested the operation to abort
     *  @param ex Exception (Throwable) to test
     *  @return <code>true</code> if it looks like the result of cancellation.
     */
    public static boolean isCancellation(final Throwable ex)
    {
        //        final String message = ex.getMessage();
        //        if (message == null)
        //            return false;
        //        if (message.startsWith(ORACLE_CANCELLATION))
        //            return true;
        //        if (message.startsWith(ORACLE_RECURSIVE_ERROR))
        //        {
        //            final Throwable cause = ex.getCause();
        //            if (cause != null)
        //                return isCancellation(cause);
        //        }
        return false;
    }

    @Override
    public void cancel()
    {
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        cancel();
        ConnectionCache.release(influxdb);
    }

    @Override
    public void enableConcurrency(boolean concurrency) {
        //TODO: does enable concurrency mean anything for InfluxDB?
    }


}
