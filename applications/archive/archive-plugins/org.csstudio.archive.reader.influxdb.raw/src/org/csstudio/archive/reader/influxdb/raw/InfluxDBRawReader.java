/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb.raw;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.influxdb.InfluxDBArchivePreferences;
import org.csstudio.archive.influxdb.InfluxDBQueries;
import org.csstudio.archive.influxdb.InfluxDBQueries.DBNameMap;
import org.csstudio.archive.influxdb.InfluxDBResults;
import org.csstudio.archive.influxdb.InfluxDBSeriesInfo;
import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.csstudio.archive.influxdb.InfluxDBUtil.ConnectionInfo;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.diirt.util.time.TimeDuration;
import org.influxdb.InfluxDB;
import org.influxdb.dto.QueryResult;

/** ArchiveReader for InfluxDB data
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class InfluxDBRawReader implements ArchiveReader
{
    final private String url;
    final private String user;
    final private String dbname;
    final private int password;
    /** Timeout when waiting for chunks of data */
    final private int timeout;
    //
    final private ConnectionCache.Entry influxdb;
    final private InfluxDBQueries influxQuery;

    public static class DBNameMapRaw extends DBNameMap {
        private final String mydbname;

        public DBNameMapRaw(final String dbname) {
            mydbname = dbname;
        }

        @Override
        public String getDataDBName(String field_name) throws Exception {
            return mydbname;
        }

        @Override
        public String getMetaDBName(String field_name) throws Exception {
            throw new Exception("No metadata DB for Raw InfluxDB Reader: " + field_name);
        }

        @Override
        public List<String> getAllDBNames() {
            ArrayList<String> ret = new ArrayList<String>();
            ret.add(mydbname);
            return ret;
        }
    }

    /** Initialize
     *  @param url Database URL
     *  @param user .. user
     *  @param password .. password
     *  @param schema .. schema (including ".") or ""
     *  @param stored_procedure Stored procedure or "" for client-side optimization
     *  @throws Exception on error
     */
    public InfluxDBRawReader(final String url, final String dbName)
            throws Exception
    {
        this(url, null, null, dbName);
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
    public InfluxDBRawReader(final String url, final String user, final String password, final String dbName)
            throws Exception
    {
        this.url = url;
        this.user = user;
        this.password = (password == null) ? 0 : password.length();
        this.dbname = dbName;

        timeout = InfluxDBArchivePreferences.getChunkTimeoutSecs();
        influxdb = ConnectionCache.get(url, user, password);
        influxQuery = new InfluxDBQueries(influxdb.getConnection(), new DBNameMapRaw(dbName));
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
    public InfluxDBQueries getQueries()
    {
        return influxQuery;
    }

    int getTimeout()
    {
        return timeout;
    }

    /** {@inheritDoc} */
    @Override
    public String getServerName()
    {
        return "InfluxDB-Raw";
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
        return "InfluxDB Raw Archive V" + getVersion() + "\n" +
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
                new ArchiveInfo("influxdb-raw", "", 1)
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

        final QueryResult results = influxQuery.get_newest_channel_datum_regex(sb.toString());

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
        return getRawValues(name, start, end);
    }

    /** Fetch raw samples
     *  @param channel_name Channel name in influxdb
     *  @param start Start time
     *  @param end End time
     *  @return {@link ValueIterator} for raw samples
     *  @throws Exception on error
     */
    public ValueIterator getRawValues(final String name,
            final Instant start, final Instant end) throws Exception
    {
        return new SampleIterator(this, InfluxDBSeriesInfo.decodeLineProtocol(name), start, end);
    }

    /** {@inheritDoc} */
    @Override
    public ValueIterator getOptimizedValues(final int key, final String name,
            final Instant start, final Instant end, int count) throws UnknownChannelException, Exception
    {
        if (count <= 1)
            throw new Exception("Count must be > 1");

        // TODO: Implement server side downsample query
        // Fallback client side downsample
        final ValueIterator raw_data = getRawValues(name, start, end);
        final double seconds = TimeDuration.toSecondsDouble(Duration.between(start, end)) / count;
        return new AveragedValueIterator(raw_data, seconds);
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
