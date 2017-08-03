/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueIterator;
import org.diirt.vtype.VType;

/** Value Iterator that provides 'optimized' data by calling
 *  a stored database procedure.
 *  @author Kay Kasemir
 *  @author Laurent Philippe - MySQL support
 */
@SuppressWarnings("nls")
public class StoredProcedureValueIterator extends AbstractInfluxDBValueIterator
{
    final private String stored_procedure;

    /** Values received from the stored procedure */
    private List<VType> values = null;

    /** Iteration index into <code>values</code>, points to what
     *  <code>next()</code> will return or -1
     */
    private int index = -1;

    /** Initialize
     *  @param reader InfluxDBArchiveReader
     *  @param stored_procedure Name of the stored procedure to call
     *  @param channel_id ID of channel
     *  @param start Start time
     *  @param end End time
     *  @param count Desired value count
     *  @throws Exception on error
     */
    public StoredProcedureValueIterator(final InfluxDBArchiveReader reader,
            final String stored_procedure,
            final String channel_name, final Instant start, final Instant end,
            final int count) throws Exception
    {
        super(reader, channel_name);
        this.stored_procedure = stored_procedure;
        executeProcedure(start, end, count);
    }

    /** Invoke stored procedure
     *  @param start Start time
     *  @param end End time
     *  @param count Desired value count
     *  @throws Exception on error
     */
    private void executeProcedure(final Instant start, final Instant end,
            final int count) throws Exception
    {
        //        final String sql;
        //        final Dialect dialect = reader.getDialect();
        //
        //        switch (dialect)
        //        {
        //        case MySQL:
        //            sql = "{call " + stored_procedure + "(?, ?, ?, ?)}";
        //            break;
        //        case PostgreSQL:
        //            sql = "{? = call " + stored_procedure + "(?, ?, ?, ?)}";
        //            break;
        //        case Oracle:
        //            sql = "begin ? := " + stored_procedure + "(?, ?, ?, ?); end;";
        //            break;
        //        default:
        //            throw new Exception("Stored procedure data readout not supported for " + dialect);
        //
        //        }
        //
        //        final CallableStatement statement =
        //                reader.getConnection().prepareCall(sql);
        //        reader.addForCancellation(statement);
        //        try
        //        {
        //            final ResultSet result;
        //
        //            if (dialect == RDBUtil.Dialect.MySQL)
        //            {     //MySQL
        //                statement.setInt(1, channel_id);
        //                statement.setTimestamp(2, TimestampHelper.toSQLTimestamp(start));
        //                statement.setTimestamp(3, TimestampHelper.toSQLTimestamp(end));
        //                statement.setInt(4, count);
        //                result = statement.executeQuery();
        //            }
        //            else if(dialect == RDBUtil.Dialect.PostgreSQL)
        //            {    //PostgreSQL
        //                boolean autoCommit = reader.getConnection().getAutoCommit();
        //                // Disable auto-commit to determine sample with PostgreSQL when fetch direction is FETCH_FORWARD
        //                if (autoCommit) {
        //                    reader.getConnection().setAutoCommit(false);
        //                }
        //                statement.registerOutParameter(1, Types.OTHER);
        //                statement.setLong(2, channel_id);
        //                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        //                statement.setString(3, sdf.format(new Date(TimestampHelper.toSQLTimestamp(start).getTime())));
        //                statement.setString(4, sdf.format(new Date(TimestampHelper.toSQLTimestamp(end).getTime())));
        //                statement.setLong(5, count);
        //                statement.setFetchDirection(ResultSet.FETCH_FORWARD);
        //                statement.setFetchSize(1000);
        //                statement.execute();
        //                result = (ResultSet) statement.getObject(1);
        //            }
        //
        //            else
        //            {    //ORACLE
        //                statement.registerOutParameter(1, OracleTypes.CURSOR);
        //                statement.setInt(2, channel_id);
        //                statement.setTimestamp(3, TimestampHelper.toSQLTimestamp(start));
        //                statement.setTimestamp(4, TimestampHelper.toSQLTimestamp(end));
        //                statement.setInt(5, count);
        //                statement.setFetchDirection(ResultSet.FETCH_FORWARD);
        //                statement.setFetchSize(1000);
        //                statement.execute();
        //                result = (ResultSet) statement.getObject(1);
        //            }
        //            result.setFetchSize(1000);
        //
        //            // Determine result type: min/max/average table or
        //            // fallback to SAMPLE table format?
        //            final ResultSetMetaData meta = result.getMetaData();
        //            final int N = meta.getColumnCount();
        //            if (N == 9)
        //                values = decodeOptimizedTable(result);
        //            else
        //                values = decodeSampleTable(result);
        //            // Initialize iterator for first value
        //            if (values.size() > 0)
        //                index = 0;
        //            // else: No data, leave as -1
        //        }
        //        catch (Exception ex)
        //        {
        //            if (! InfluxDBArchiveReader.isCancellation(ex))
        //                throw ex;
        //            // Else: Not a real error; return empty iterator
        //            Logger.getLogger(getClass().getName()).log(Level.FINE,
        //                    "Stored procedure cancelled", ex);
        //        }
        //        finally
        //        {
        //            reader.removeFromCancellation(statement);
        //            statement.close();
        //        }
    }

    /** Decode samples from 'optimized' table with min/max/average
     *  @param result ResultSet
     *  @return IValue array of samples
     *  @throws Exception on error
     */
    private List<VType> decodeOptimizedTable(final ResultSet result) throws Exception
    {
        final List<VType> values = new ArrayList<VType>();

        //        // Row with min/max/average data:
        //        // WB: 1, SMPL_TIME: 2010/01/22 21:07:18.772633666, SEVERITY_ID: null, STATUS_ID: null, MIN_VAL: 8.138729867823713E-8, MAX_VAL: 6.002717327646678E-7, AVG_VAL: 8.240168908036992E-8, STR_VAL: null, CNT: 3611
        //        // Row with String value:
        //        // WB: -1, SMPL_TIME: 2010/01/28 11:14:11.086000000, SEVERITY_ID: 2, STATUS_ID: 2, MIN_VAL: null, MAX_VAL: null, AVG_VAL: null, STR_VAL: Archive_Off, CNT: 1
        //        // i.e. Columns 1 WB, 2 SMPL_TIME, 3 SEVERITY_ID, 4 STATUS_ID, 5 MIN_VAL, 6 MAX_VAL, 7 AVG_VAL, 8 STR_VAL, 9 CNT
        //        while (result.next())
        //        {
        //            // Time stamp
        //            final Instant time = TimestampHelper.fromSQLTimestamp(result.getTimestamp(2));
        //
        //            // Get severity/status
        //            final AlarmSeverity severity;
        //            final String status;
        //            final int sev_id = result.getInt(3);
        //            if (result.wasNull())
        //            {
        //                severity = AlarmSeverity.NONE;
        //                status = "";
        //            }
        //            else
        //            {
        //                status = reader.getStatus(result.getInt(4));
        //                severity = filterSeverity(reader.getSeverity(sev_id), status);
        //            }
        //
        //            // WB==-1 indicates a String sample
        //            final VType value;
        //            if (result.getInt(1) < 0)
        //                value = new ArchiveVString(time, severity, status, result.getString(8));
        //            else
        //            {   // Only one value within averaging bucket?
        //                final int cnt = result.getInt(9);
        //                final double val_or_avg = result.getDouble(7);
        //                if (cnt == 1)
        //                    value = new ArchiveVNumber(time, severity, status, display, val_or_avg);
        //                else // Decode min/max/average
        //                {
        //                    final double min = result.getDouble(5);
        //                    final double max = result.getDouble(6);
        //                    final double stddev = 0.0; // not known
        //                    value = new ArchiveVStatistics(time, severity,
        //                            status, display,
        //                            val_or_avg, min, max, stddev, cnt);
        //                }
        //            }
        //            values.add(value);
        //        }
        return values;
    }

    /** Decode samples from SAMPLE table
     *  @param result ResultSet
     *  @return IValue array of samples
     *  @throws Exception on error, including cancellation
     */
    private List<VType> decodeSampleTable(final ResultSet result) throws Exception
    {
        final ArrayList<VType> values = new ArrayList<VType>();
        //        while (result.next())
        //        {
        //            final VType value = decodeSampleTableValue(result, false);
        //            values.add(value);
        //        }
        return values;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return index >= 0;
    }

    /** {@inheritDoc} */
    @Override
    public VType next() throws Exception
    {
        final VType result = values.get(index);
        ++index;
        if (index >= values.size())
            index = -1;
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        super.close();
        index = -1;
        values = null;
        //        if (reader.getDialect() == Dialect.PostgreSQL) {
        //            // Restore default auto-commit on result set close
        //            try {
        //                reader.getConnection().setAutoCommit(true);
        //            } catch (Exception e) {
        //                // Ignore
        //            }
        //        }

    }
}
