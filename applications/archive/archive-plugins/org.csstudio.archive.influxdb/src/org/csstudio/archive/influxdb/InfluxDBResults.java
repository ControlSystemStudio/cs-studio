package org.csstudio.archive.influxdb;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

public class InfluxDBResults
{
    public static int getResultCount(QueryResult results)
    {
        if ( results.hasError() ) {
            return -1;
        }
        if ( results.getResults() == null )
        {
            return 0;
        }
        return results.getResults().size();
    }

    public static int getSeriesCount(Result result)
    {
        if ( result.hasError() )
        {
            return -1;
        }
        if ( result.getSeries() == null )
        {
            return 0;
        }
        return result.getSeries().size();
    }

    public static int getTagCount(Series series)
    {
        if (series.getTags() == null)
        {
            return 0;
        }
        return series.getTags().size();
    }

    public static int getColumnCount(Series series)
    {
        if (series.getColumns() == null)
        {
            return 0;
        }
        return series.getColumns().size();
    }

    public static int getValueCount(Series series)
    {
        if (series.getValues() == null)
        {
            return 0;
        }
        return series.getValues().size();
    }

    public static int getValueSum(QueryResult results)
    {
        int count = 0;
        for (Result result : results.getResults())
            if (result != null)
                for (Series series : result.getSeries())
                    if (series != null)
                        for (List<Object> row : series.getValues())
                            if (row != null)
                                for (Object obj : row)
                                    if (obj instanceof Number)
                                        count += ((Number)obj).intValue();
        return count;
    }

    public static List<Series> getSeries(QueryResult results)
    {
        List<Series> ret = new ArrayList<Series>();
        if (getResultCount(results) > 0)
        {
            for (Result result : results.getResults() )
            {
                if (getSeriesCount(result) > 0)
                {
                    ret.addAll(result.getSeries());
                }
            }
        }
        return ret;
    }

    public static List<Series> getNonEmptySeries(QueryResult results)
    {
        List<Series> ret = new ArrayList<Series>();
        if (getResultCount(results) > 0)
        {
            for (Result result : results.getResults() )
            {
                if (getSeriesCount(result) > 0)
                {
                    for (Series series : result.getSeries())
                    {
                        if (getValueCount(series) > 0)
                        {
                            ret.add(series);
                        }
                    }
                }
            }
        }
        return ret;
    }

    public static int getValueCount(QueryResult results)
    {
        int ret = 0;
        if (results == null)
            return -1;
        if (getResultCount(results) > 0)
        {
            for (Result result : results.getResults() )
            {
                if (getSeriesCount(result) > 0)
                {
                    for (Series series : result.getSeries())
                    {
                        ret += getValueCount(series);
                    }
                }
            }
        }
        return ret;
    }

    public static Object getValue(Series series, final String colname, final int validx)
    {
        if (series == null)
            return null;

        final List<String> cols = series.getColumns();
        if (cols == null)
            return null;

        final List<List<Object>> vals = series.getValues();
        if (vals == null)
            return null;

        return vals.get(validx).get(cols.indexOf(colname));
    }

    public static Instant getTimestamp(QueryResult results)
    {
        //Activator.getLogger().log(Level.FINE, "Results from query: {0}", InfluxDBResults.toString(results));

        final Instant ret;
        try
        {
            final Series series0 = results.getResults().get(0).getSeries().get(0);
            //final String ts = (String) InfluxDBResults.getValue(series0, "time", 0);
            ret = InfluxDBUtil.fromInfluxDBTimeFormat(InfluxDBResults.getValue(series0, "time", 0));
        }
        catch (Exception e)
        {
            Activator.getLogger().log(Level.FINE, () -> "Could not get timestamp from results :" + InfluxDBResults.toString(results));
            return null;
        }
        return ret;
    }

    public static String[] getMeasurements(final QueryResult results) throws Exception {
        final List<Series> series = InfluxDBResults.getSeries(results);
        Set<String> measurements = new HashSet<String>();

        for (Series S : series) {
            measurements.add(S.getName());
        }
        return measurements.toArray(new String[measurements.size()]);
    }

    public static TableBuilder makeSeriesTable(List<String> cols, List<List<Object>> all_vals)
    {
        TableBuilder tb = new TableBuilder();
        ArrayList<String> r0 = new ArrayList<String>();
        ArrayList<String> r1 = new ArrayList<String>();

        if (cols != null)
        {
            r0.clear();
            r1.clear();
            for (String col : cols)
            {
                r0.add(col);
                r1.add(StringUtils.repeat('-', col.length()));
            }
            tb.addRow(r0.toArray());
            tb.addRow(r1.toArray());
        }

        if (all_vals != null)
        {
            for (List<Object> vals : all_vals)
            {
                r0.clear();
                //r1.clear();
                for (Object val : vals)
                {
                    try
                    {
                        r0.add(val.toString());
                    }
                    catch (Exception e)
                    {
                        r0.add("");
                    }
                    //r1.add(val.getClass().getName());
                }
                tb.addRow(r0.toArray());
                //tb.addRow(r1.toArray());
            }
        }

        return tb;
    }

    public static String toString(QueryResult results)
    {
        if (results == null)
            return "null";

        StringBuilder buf = new StringBuilder();

        if ( results.hasError() ) {
            buf.append( "Result error: ").append(results.getError());
            return buf.toString();
        }

        int result_count = getResultCount(results);
        buf.append("Total Results = ").append(result_count).append('\n');

        if (result_count > 0)
        {
            result_count = 0;

            for ( Result result : results.getResults() )
            {
                result_count++;
                int series_count = getSeriesCount(result);

                buf.append("  Result ").append(result_count).append(": Total Series = ").append(series_count).append('\n');
                if (series_count > 0)
                {
                    series_count = 0;

                    for ( Series series : result.getSeries() )
                    {
                        series_count++;
                        int tag_count = getTagCount(series);
                        int col_count = getColumnCount(series);
                        int val_count = getValueCount(series);

                        buf.append("    Series ").append(series_count)
                        .append(": name[").append(series.getName())
                        .append("] (").append(tag_count).append(" tags) (")
                        .append(col_count).append(" cols) (")
                        .append(val_count).append(" vals)").append('\n');

                        if (tag_count > 0)
                        {
                            for ( String key : series.getTags().keySet() )
                            {
                                buf.append("    tag[").append(key).append(":").append(series.getTags().get(key)).append("]").append('\n');
                            }
                        }

                        buf.append("    +-\n");
                        final TableBuilder tb  = makeSeriesTable(series.getColumns(), series.getValues());
                        tb.setIndent("    ");
                        buf.append(tb.toString());
                    }
                }
            }
        }
        return buf.toString();
    }
}