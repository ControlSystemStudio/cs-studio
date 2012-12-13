/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.jdbc.OracleTypes;

import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** Value Iterator that provides 'optimized' data by calling
 *  a stored database procedure.
 *  @author Kay Kasemir
 *  @author Laurent Philippe - MySQL support
 */
@SuppressWarnings("nls")
public class StoredProcedureValueIterator extends AbstractRDBValueIterator
{
    final private String stored_procedure;

    /** Values received from the stored procedure */
    private IValue values[] = null;

    /** Iteration index into <code>values</code>, points to what
     *  <code>next()</code> will return or -1
     */
    private int index = -1;

    /** Initialize
     *  @param reader RDBArchiveReader
     *  @param stored_procedure Name of the stored procedure to call
     *  @param channel_id ID of channel
     *  @param start Start time
     *  @param end End time
     *  @param count Desired value count
     *  @throws Exception on error
     */
    public StoredProcedureValueIterator(final RDBArchiveReader reader,
            final String stored_procedure,
            final int channel_id, final ITimestamp start, final ITimestamp end,
            final int count) throws Exception
    {
        super(reader, channel_id);
        this.stored_procedure = stored_procedure;
        executeProcedure(start, end, count);
    }

    /** Invoke stored procedure
     *  @param start Start time
     *  @param end End time
     *  @param count Desired value count
     *  @throws Exception on error
     */
    private void executeProcedure(final ITimestamp start, final ITimestamp end,
            final int count) throws Exception
    {
        final String sql;
        final Dialect dialect = reader.getRDB().getDialect();

        switch (dialect)
        {
        case MySQL:
            sql = "{call " + stored_procedure + "(?, ?, ?, ?)}";
        	break;
        case Oracle:
            sql = "begin ? := " + stored_procedure + "(?, ?, ?, ?); end;";
            break;
        default:
            throw new Exception("Stored procedure data readout not supported for " + dialect);

        }

        final CallableStatement statement =
                reader.getRDB().getConnection().prepareCall(sql);
        reader.addForCancellation(statement);
        try
        {
        	final ResultSet result;

        	if (dialect == RDBUtil.Dialect.MySQL)
        	{	 //MySQL
        		 statement.setInt(1, channel_id);
                 statement.setTimestamp(2, start.toSQLTimestamp());
                 statement.setTimestamp(3, end.toSQLTimestamp());
                 statement.setInt(4, count);
                 result = statement.executeQuery();
        	}
        	else
        	{	//ORACLE
        		statement.registerOutParameter(1, OracleTypes.CURSOR);
                statement.setInt(2, channel_id);
                statement.setTimestamp(3, start.toSQLTimestamp());
                statement.setTimestamp(4, end.toSQLTimestamp());
                statement.setInt(5, count);
                statement.setFetchDirection(ResultSet.FETCH_FORWARD);
                statement.setFetchSize(1000);
                statement.execute();
                result = (ResultSet) statement.getObject(1);
        	}
            result.setFetchSize(1000);

            // Determine result type: min/max/average table or
            // fallback to SAMPLE table format?
            final ResultSetMetaData meta = result.getMetaData();
            final int N = meta.getColumnCount();
            if (N == 9)
                values = decodeOptimizedTable(result);
            else
                values = decodeSampleTable(result);
            // Initialize iterator for first value
            if (values.length > 0)
                index = 0;
            // else: No data, leave as -1
        }
        catch (Exception ex)
        {
            if (! RDBArchiveReader.isCancellation(ex))
                throw ex;
            // Else: Not a real error; return empty iterator
            Logger.getLogger(getClass().getName()).log(Level.FINE,
                    "Stored procedure cancelled", ex);
        }
        finally
        {
            reader.removeFromCancellation(statement);
            statement.close();
        }
    }

    /** Decode samples from 'optimized' table with min/max/average
     *  @param result ResultSet
     *  @return IValue array of samples
     *  @throws Exception on error
     */
    private IValue[] decodeOptimizedTable(final ResultSet result) throws Exception
    {
        final ArrayList<IValue> tmp_values = new ArrayList<IValue>();

        // Need numeric meta data or nothing
        final INumericMetaData meta = (this.meta instanceof INumericMetaData) ?
                (INumericMetaData) this.meta : null;

        // Row with min/max/average data:
        // WB: 1, SMPL_TIME: 2010/01/22 21:07:18.772633666, SEVERITY_ID: null, STATUS_ID: null, MIN_VAL: 8.138729867823713E-8, MAX_VAL: 6.002717327646678E-7, AVG_VAL: 8.240168908036992E-8, STR_VAL: null, CNT: 3611
        // Row with String value:
        // WB: -1, SMPL_TIME: 2010/01/28 11:14:11.086000000, SEVERITY_ID: 2, STATUS_ID: 2, MIN_VAL: null, MAX_VAL: null, AVG_VAL: null, STR_VAL: Archive_Off, CNT: 1
        // i.e. Columns 1 WB, 2 SMPL_TIME, 3 SEVERITY_ID, 4 STATUS_ID, 5 MIN_VAL, 6 MAX_VAL, 7 AVG_VAL, 8 STR_VAL, 9 CNT
        while (result.next())
        {
            // Time stamp
            final ITimestamp time = TimestampFactory.fromSQLTimestamp(result.getTimestamp(2));

            // Get severity/status
            ISeverity severity = reader.getSeverity(result.getInt(3));
            final String status;
            if (result.wasNull())
            {
                severity = ValueFactory.createOKSeverity();
                status = severity.toString();
            }
            else
            {
                status = reader.getStatus(result.getInt(4));
                severity = filterSeverity(severity, status);
            }

            // WB==-1 indicates a String sample
            final IValue value;
            if (result.getInt(1) < 0)
                value = ValueFactory.createStringValue(time, severity, status,
                        IValue.Quality.Original, new String[] { result.getString(8) });
            else
            {   // Only one value within averaging bucket?
                final int cnt = result.getInt(9);
                if (cnt == 1)
                    value = ValueFactory.createDoubleValue(time, severity,
                            status, meta, IValue.Quality.Original,
                            new double[] { result.getDouble(7) });
                else // Decode min/max/average
                    value = ValueFactory.createMinMaxDoubleValue(time, severity,
                            status, meta, IValue.Quality.Interpolated,
                            new double[] { result.getDouble(7) },
                            result.getDouble(5),
                            result.getDouble(6));
            }
            tmp_values.add(value);
        }
        // Convert to plain array
        final IValue values[] = tmp_values.toArray(new IValue[tmp_values.size()]);
        return values;
    }

    /** Decode samples from SAMPLE table
     *  @param result ResultSet
     *  @return IValue array of samples
     *  @throws Exception on error, including cancellation
     */
    private IValue[] decodeSampleTable(final ResultSet result) throws Exception
    {
        final ArrayList<IValue> tmp_values = new ArrayList<IValue>();
        while (result.next())
        {
            final IValue value = decodeSampleTableValue(result);
            tmp_values.add(value);
        }
        return tmp_values.toArray(new IValue[tmp_values.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return index >= 0;
    }

    /** {@inheritDoc} */
    @Override
    public IValue next() throws Exception
    {
        final IValue result = values[index];
        ++index;
        if (index >= values.length)
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
    }
}
