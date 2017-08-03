/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.influxdb;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.archive.influxdb.InfluxDBArchivePreferences;
import org.csstudio.archive.influxdb.InfluxDBQueries;
import org.csstudio.archive.influxdb.InfluxDBQueries.DBNameMap;
import org.csstudio.archive.influxdb.InfluxDBQueries.DefaultDBNameMap;
import org.csstudio.archive.influxdb.InfluxDBResults;
import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.csstudio.archive.influxdb.InfluxDBUtil.ConnectionInfo;
import org.csstudio.archive.influxdb.MetaTypes;
import org.csstudio.archive.influxdb.MetaTypes.MetaObject;
import org.csstudio.archive.influxdb.MetaTypes.StoreAs;
import org.csstudio.archive.vtype.MetaDataHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.archive.writer.ArchiveWriter;
import org.csstudio.archive.writer.WriteChannel;
import org.diirt.vtype.Display;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;

/** ArchiveWriter implementation for InfluxDB
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class InfluxDBArchiveWriter implements ArchiveWriter
{
    /** InfluxDB connection */
    final private InfluxDB influxdb;

    /** InfluxDB statements */
    final private InfluxDBQueries influxQuery;

    /** Cache of channels by name */
    final private Map<String, InfluxDBWriteChannel> channels = new HashMap<String, InfluxDBWriteChannel>();

    final static private DBNameMap dbnames = new DefaultDBNameMap();

    static class batchPointSets implements Iterable<BatchPoints>
    {
        /** Batched points to be written per retention policy, per database.
         * Map from dbname to a retention policy map. Retention policies are mapped
         * to their corresponding BatchPoints instance. */
        final private Map<String, Map<String, BatchPoints>> dbPoints = new HashMap<>();

        private BatchPoints getMakePoints(final String dbName)
        {
            return getMakePoints(dbName, null);
        }

        private BatchPoints getMakePoints(final String dbName, final String rpName)
        {
            Map<String, BatchPoints> rp_points = dbPoints.get(dbName);
            BatchPoints points;
            if (rp_points == null)
            {
                rp_points = new HashMap<String, BatchPoints>();
                dbPoints.put(dbName, rp_points);
                points = null;
            }
            else
                points = rp_points.get(rpName);
            if (points == null)
            {
                //TODO: set consistency policy
                points = BatchPoints
                        .database(dbName)
                        .consistency(ConsistencyLevel.ALL)
                        .retentionPolicy(rpName != null ? rpName : "autogen")
                        .build();
                rp_points.put(rpName, points);
            }
            return points;
        }

        public BatchPoints getChannelSamplePoints(final String channel_name) throws Exception
        {
            return getMakePoints(dbnames.getDataDBName(channel_name));
        }

        public BatchPoints getChannelSamplePoints(final String channel_name, final String rpname) throws Exception
        {
            return getMakePoints(dbnames.getDataDBName(channel_name), rpname);
        }

        public BatchPoints getChannelMetaPoints(final String channel_name) throws Exception
        {
            return getMakePoints(dbnames.getMetaDBName(channel_name));
        }

        public void removeDBPoints(final String dbName)
        {
            dbPoints.remove(dbName);
        }

        public void clear()
        {
            dbPoints.clear();
        }

        @Override
        public Iterator<BatchPoints> iterator()
        {
            return new Iterator<BatchPoints>()
            {
                private final Iterator<Map<String,BatchPoints>> dbs = dbPoints.values().iterator();
                private Iterator<BatchPoints> points = Collections.emptyIterator();
                @Override
                public boolean hasNext()
                {
                    while (!points.hasNext())
                    {
                        if (!dbs.hasNext()) return false;
                        points = dbs.next().values().iterator();
                    }
                    return true;
                }

                @Override
                public BatchPoints next()
                {
                    hasNext();
                    return points.next();
                }
            };
        }
    };

    final batchPointSets batchSets = new batchPointSets();

    //    /** Severity (ID, name) cache */
    //    private SeverityCache severities;
    //
    //    /** Status (ID, name) cache */
    //    private StatusCache stati;

    /** Initialize from preferences.
     *  This constructor will be invoked when an {@link ArchiveWriter}
     *  is created via the extension point.
     *  @throws Exception on error, for example InfluxDB connection error
     */
    public InfluxDBArchiveWriter() throws Exception
    {
        this(InfluxDBArchivePreferences.getURL(), InfluxDBArchivePreferences.getUser(),
                InfluxDBArchivePreferences.getPassword());
    }

    /** Initialize
     *  @param url InfluxDB URL
     *  @param user .. user name
     *  @param password .. password
     *  @throws Exception on error, for example InfluxDB connection error
     */
    public InfluxDBArchiveWriter(final String url, final String user, final String password) throws Exception
    {
        influxdb = InfluxDBUtil.connect(url, user, password);
        influxQuery = new InfluxDBQueries(influxdb, dbnames);
        //        severities = new SeverityCache(influxdb, sql);
        //        stati = new StatusCache(influxdb, sql);
    }

    public ConnectionInfo getConnectionInfo() throws Exception
    {
        return new ConnectionInfo(influxdb);
    }

    public InfluxDBQueries getQueries()
    {
        return influxQuery;
    }

    @Override
    public WriteChannel getChannel(final String name) throws Exception
    {
        return getChannel(name, null);
    }

    @Override
    public WriteChannel getChannel(final String name, final String retention) throws Exception
    {
        // Check cache
        InfluxDBWriteChannel channel = channels.get(name);
        if (channel == null)
        {    // Get channel information from InfluxDB
            QueryResult results = influxQuery.get_newest_meta_datum(name);
            if (InfluxDBResults.getValueCount(results) <= 0)
            {
                // throw new Exception("Unknown channel " + name);
                return makeNewChannel(name, retention);
            }
            List<MetaObject> meta = MetaTypes.toMetaObjects(results);
            if (meta.size() != 1)
            {
                throw new Exception("Metadata results for channel " + name + " did not parse into single object: " + results);
            }
            channel = new InfluxDBWriteChannel(name, retention);
            channel.setMetaData(meta.get(0));
            channels.put(name, channel);
        }
        return channel;
    }

    public WriteChannel makeNewChannel(final String name) throws Exception
    {
        return makeNewChannel(name, null);
    }

    public WriteChannel makeNewChannel(final String name, final String rp) throws Exception
    {
        // Check cache
        InfluxDBWriteChannel channel = channels.get(name);
        if (channel != null)
        {
            throw new Exception("Channel already exists in Writer " + name);
        }

        QueryResult results = influxQuery.get_newest_meta_datum(name);
        if (InfluxDBResults.getValueCount(results) > 0)
        {
            throw new Exception("Channel already exists in Database " + name);
        }
        channel = new InfluxDBWriteChannel(name, rp);
        channels.put(name, channel);

        return channel;
    }

    @Override
    public void addSample(final WriteChannel channel, final VType sample) throws Exception
    {
        final InfluxDBWriteChannel influxdb_channel = (InfluxDBWriteChannel) channel;
        final StoreAs storeas = MetaTypes.writeVtypeAs(sample);
        final Instant stamp = VTypeHelper.getTimestamp(sample);

        writeMetaData(influxdb_channel, stamp, sample, storeas);
        batchSets.getChannelSamplePoints(channel.getName(), influxdb_channel.getRP())
            .point(InfluxDBSampleEncoder.encodeSample(influxdb_channel, stamp, sample, storeas));
    }

    /** Write meta data if it was never written or has changed
     *  @param channel Channel for which to write the meta data
     *  @param sample Sample that may have meta data to write
     */
    private void writeMetaData(final InfluxDBWriteChannel channel, final Instant stamp, final VType sample, final StoreAs storeas) throws Exception
    {
        switch(storeas)
        {
        case ARCHIVE_DOUBLE :
        case ARCHIVE_LONG :
        case ARCHIVE_DOUBLE_ARRAY :
        case ARCHIVE_LONG_ARRAY :
        {
            if (sample instanceof Display)
            {
                final Display display = (Display)sample;
                if ((channel.getStorageType() == storeas) && (MetaDataHelper.equals(display, channel.getMetadata())))
                    return;
                Point point = MetaTypes.toDisplayMetaPoint(display, channel.getName(), stamp, storeas);
                batchSets.getChannelMetaPoints(channel.getName()).point(point);
                channel.setMetaData(display, storeas);
            }
            else {
                throw new Exception ("Cannot determine meta data object for " + storeas.name() + " with sample type " + sample.getClass().getName());
            }
        }
        break;
        case ARCHIVE_ENUM :
        {
            if (sample instanceof VEnum)
            {
                final List<String> labels = ((VEnum)sample).getLabels();
                if ((channel.getStorageType() == storeas) && (MetaDataHelper.equals(labels, channel.getMetadata())))
                    return;
                Point point = MetaTypes.toEnumMetaPoint(labels, channel.getName(), stamp, storeas);
                batchSets.getChannelMetaPoints(channel.getName()).point(point);
                channel.setMetaData(labels, storeas);
            }
            else {
                throw new Exception ("Cannot determine meta data object for " + storeas.name() + " with sample type " + sample.getClass().getName());
            }
        }
        break;
        case ARCHIVE_STRING :
        case ARCHIVE_UNKNOWN :
            if ((channel.getStorageType() == storeas) && (channel.getMetadata() == null))
                return;
            Point point = MetaTypes.toNullMetaPoint(channel.getName(), stamp, storeas);
            batchSets.getChannelMetaPoints(channel.getName()).point(point);
            channel.setMetaData(null, storeas);
            break;
        default:
            throw new Exception ("Sample generated unhandled meta store type: " + storeas.name());
        }

    }


    /** {@inheritDoc}
     *  InfluxDB implementation completes pending batches
     */
    @Override
    public void flush() throws Exception
    {
        for (BatchPoints batchPoints : batchSets)
        {
            try
            {
                influxdb.write(batchPoints);
            }
            catch (Exception e)
            {
                throw new Exception("Write of points failed " + e.getMessage(), e);
            }
        }
        batchSets.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        channels.clear();
        //        if (severities != null)
        //        {
        //            severities.dispose();
        //            severities = null;
        //        }
        //        if (stati != null)
        //        {
        //            stati.dispose();
        //            stati = null;
        //        }

        //TODO: do we need to flush points here?
        influxdb.close();
    }
}
