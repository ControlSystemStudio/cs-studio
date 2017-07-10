package org.csstudio.archive.influxdb;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class InfluxDBQueries
{
    private final InfluxDB influxdb;

    abstract public static class DBNameMap {
        public abstract String getDataDBName(final String channel_name) throws Exception;

        public abstract String getMetaDBName(final String channel_name) throws Exception;

        public abstract List<String> getAllDBNames();
    };

    private final DBNameMap dbnames;

    public static class DefaultDBNameMap extends DBNameMap {

        protected final String db_name;
        protected final String meta_db_name;
        protected final List<String> all_names;

        public DefaultDBNameMap() {
            db_name = InfluxDBArchivePreferences.getDBPrefix() + InfluxDBArchivePreferences.getDBName();
            meta_db_name = InfluxDBArchivePreferences.getDBPrefix() + InfluxDBArchivePreferences.getMetaDBName();

            all_names = new ArrayList<String>();
            all_names.add(db_name);
            all_names.add(meta_db_name);
        }

        @Override
        public String getDataDBName(String channel_name) {
            return db_name;
        }

        @Override
        public String getMetaDBName(String channel_name) {
            return meta_db_name;
        }

        @Override
        public List<String> getAllDBNames() {
            return all_names;
        }
    };

    public List<String> getAllDBNames() {
        return dbnames.getAllDBNames();
    }

    public void initDatabases(final InfluxDB influxdb) {
        for (String db : dbnames.getAllDBNames()) {
            influxdb.createDatabase(db);
        }
    }

    public InfluxDBQueries(InfluxDB influxdb, final DBNameMap dbnames)
    {
        this.influxdb = influxdb;
        if (dbnames == null)
            this.dbnames = new DefaultDBNameMap();
        else
            this.dbnames = dbnames;
    }

    //TODO: timestamps come back with wrong values stored in Double... would be faster if it worked.
    //private final static boolean query_nanos = true;

    public static QueryResult makeQuery(final InfluxDB influxdb, final String stmt, final String dbName)
    {
        Activator.getLogger().log(Level.FINE, "InfluxDB query ({0}): {1}", new Object[] {dbName, stmt});
        //if (query_nanos)
        //    return influxdb.query(new Query(stmt, dbName), TimeUnit.NANOSECONDS);
        return influxdb.query(new Query(stmt, dbName));

    }

    public static void makeChunkQuery(int chunkSize, Consumer<QueryResult> consumer,
            InfluxDB influxdb, String stmt, String dbName) throws Exception
    {
        Activator.getLogger().log(Level.FINE, "InfluxDB chunked ({2}) query ({0}): {1}", new Object[] {dbName, stmt, chunkSize});
        //if (query_nanos)
        //   influxdb.query(new Query(stmt, dbName), TimeUnit.NANOSECONDS, chunkSize, consumer);
        //else
        influxdb.query(new Query(stmt, dbName), chunkSize, consumer);
    }


    private static String get_points(final StringBuilder sb, final List<String> where_clauses,
            String group_by_what, final Long limit)
    {
        if ((where_clauses != null) && (where_clauses.size() > 0))
        {
            sb.append(" WHERE ");
            for (int idx = 0; idx < where_clauses.size(); idx++) {
                if (idx > 0)
                    sb.append(" AND ");
                sb.append(where_clauses.get(idx));
            }
        }
        if (group_by_what != null)
        {
        	sb.append(" GROUP BY ");
        	sb.append(group_by_what);
        }
        sb.append(" ORDER BY time ");
        if (limit != null)
        {
            if (limit > 0)
                sb.append(" LIMIT ").append(limit);
            else if (limit < 0)
                sb.append(" DESC LIMIT ").append(-limit);
        }
        return sb.toString();
    }

    private static List<String> getTimeClauses(final Instant starttime, final Instant endtime) {
        if ((starttime == null) && (endtime == null))
            return null;

        List<String> where_clauses = new ArrayList<String>();
        if (starttime != null) {
            where_clauses.add("time >= " + InfluxDBUtil.toNano(starttime).toString());
        }
        if (endtime != null) {
            where_clauses.add("time <= " + InfluxDBUtil.toNano(endtime).toString());
        }
        return where_clauses;
    }
    
	private static String getGroupByTimeClause(final Instant starttime, final Instant endtime, final long count)
    {
    	//TODO: rounding/truncation problem?
        StringBuilder ret = new StringBuilder();
        ret.append("time(");
        ret.append(InfluxDBUtil.toMicro(
        		Duration.between(starttime, endtime).dividedBy(count)).toString());
		//Fill options: fill(none) is best, because empty time intervals (a.k.a. buckets) are automatically excluded.
		//There is no need to try to sample data with metadata, because metadata exists independently of sample data.
        ret.append("u) fill(none)");
        return ret.toString();
    }

	public static String get_channel_points(final String select_what, final String channel_name,
            final Instant starttime, final Instant endtime, String where_what, String group_by_what,
            final Long limit)
	{
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(select_what).append(" FROM \"").append(channel_name).append('\"');
        List<String> where_clauses = getTimeClauses(starttime, endtime);
        if (where_what != null)
        	where_clauses.add(where_what);
        return get_points(sb, where_clauses, group_by_what, limit);
    }

    public static String get_series_points(final InfluxDBSeriesInfo series, final Instant starttime,
            final Instant endtime, final Long limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT \"").append(series.field).append("\" FROM \"").append(series.measurement).append('\"');

        List<String> where_clauses = series.getTagClauses();
        final List<String> time_clauses = getTimeClauses(starttime, endtime);

        if (where_clauses == null)
            where_clauses = time_clauses;
        else if (time_clauses != null)
            where_clauses.addAll(time_clauses);

        return get_points(sb, where_clauses, null, limit);
    }

    public static String get_pattern_points(final String select_what, final String pattern, final Instant starttime,
            final Instant endtime, final Long limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(select_what).append(" FROM /").append(pattern).append('/');
        return get_points(sb, getTimeClauses(starttime, endtime), null, limit);
    }

    ///////////////////////////// RAW DATA QUERIES

    public void chunk_get_series_samples(final int chunkSize, final InfluxDBSeriesInfo series, final Instant starttime,
            final Instant endtime, Long limit, Consumer<QueryResult> consumer) throws Exception {
        makeChunkQuery(chunkSize, consumer, influxdb, get_series_points(series, starttime, endtime, limit),
                dbnames.getDataDBName(series.getMeasurement()));
    }

    public QueryResult get_oldest_series_sample(final InfluxDBSeriesInfo series) throws Exception {
        return makeQuery(influxdb, get_series_points(series, null, null, 1L),
                dbnames.getDataDBName(series.getMeasurement()));
    }

    public QueryResult get_newest_series_samples(final InfluxDBSeriesInfo series, final Instant starttime,
            final Instant endtime, Long num) throws Exception {
        return makeQuery(influxdb, get_series_points(series, starttime, endtime, -num),
                dbnames.getDataDBName(series.getMeasurement()));
    }

    public QueryResult get_series_samples(final InfluxDBSeriesInfo series, final Instant starttime,
            final Instant endtime, Long num) throws Exception {
        return makeQuery(influxdb, get_series_points(series, starttime, endtime, num),
                dbnames.getDataDBName(series.getMeasurement()));
    }

    ///////////////////////////// DATA ARCHIVE QUERIES

    public QueryResult get_oldest_channel_sample(final String channel_name) throws Exception
    {
        return makeQuery(
                influxdb,
                get_channel_points("*", channel_name, null, null, null, null, 1L),
                dbnames.getDataDBName(channel_name));
    }

    public QueryResult get_newest_channel_samples(final String channel_name, final Instant starttime,
            final Instant endtime, Long num) throws Exception
    {
        return makeQuery(
                influxdb,
                get_channel_points("*", channel_name, starttime, endtime, null, null, -num),
                dbnames.getDataDBName(channel_name));
    }

    public QueryResult get_channel_samples(final String channel_name, final Instant starttime, final Instant endtime,
            Long num) throws Exception
    {
        return makeQuery(
                influxdb,
                get_channel_points("*", channel_name, starttime, endtime, null, null, num),
                dbnames.getDataDBName(channel_name));
    }


    public void chunk_get_channel_samples(final int chunkSize,
            final String channel_name, final Instant starttime, final Instant endtime, Long limit, Consumer<QueryResult> consumer) throws Exception
    {
        makeChunkQuery(
                chunkSize, consumer, influxdb,
                get_channel_points("*", channel_name, starttime, endtime, null, null, limit),
                dbnames.getDataDBName(channel_name));
    }

    public QueryResult get_newest_channel_datum_regex(final String pattern) throws Exception {
        return makeQuery(influxdb, get_pattern_points("*", pattern, null, null, -1L), dbnames.getDataDBName(pattern));
    }
    
	public void chunk_get_channel_sample_stats(final int chunkSize, final String channel_name, final Instant starttime,
			final Instant endtime, Long limit, boolean stdDev, Consumer<QueryResult> consumer) throws Exception
	{
    	StringBuilder select_what = new StringBuilder("MEAN(/\\.0/),MAX(/\\.0/),MIN(/\\.0/),");
    	if (stdDev)
    		select_what.append("STDDEV(/\\.0/),");
    	select_what.append("COUNT(/\\.0/),FIRST(/\\.0/)");
		makeChunkQuery(chunkSize, consumer, influxdb,
				get_channel_points(select_what.toString(), channel_name, starttime, endtime, "status != 'NaN'",
						getGroupByTimeClause(starttime, endtime, limit), null),
				dbnames.getDataDBName(channel_name));
    }

    ///////////////////////////// META DATA ARCHIVE QUERIES

    public QueryResult get_newest_meta_data(final String channel_name, final Instant starttime, final Instant endtime,
            Long num) throws Exception
    {
        return makeQuery(
                influxdb,
                get_channel_points("*", channel_name, starttime, endtime, null, null, -num),
                dbnames.getMetaDBName(channel_name));
    }

    public QueryResult get_newest_meta_datum(final String channel_name) throws Exception
    {
        return makeQuery(
                influxdb,
                get_channel_points("*", channel_name, null, null, null, null, -1L),
                dbnames.getMetaDBName(channel_name));
    }

    public QueryResult get_newest_meta_datum_regex(final String pattern) throws Exception {
        return makeQuery(influxdb, get_pattern_points("*", pattern, null, null, -1L),
                dbnames.getMetaDBName(pattern));
    }

    public QueryResult get_all_meta_data(final String channel_name) throws Exception
    {
        return makeQuery(
                influxdb,
                get_channel_points("*", channel_name, null, null, null, null, null),
                dbnames.getMetaDBName(channel_name));
    }

    public void chunk_get_channel_metadata(final int chunkSize,
            final String channel_name, final Instant starttime, final Instant endtime, Long limit, Consumer<QueryResult> consumer) throws Exception
    {
        makeChunkQuery(
                chunkSize, consumer, influxdb,
                get_channel_points("*", channel_name, starttime, endtime, null, null, limit),
                dbnames.getMetaDBName(channel_name));
    }
}
