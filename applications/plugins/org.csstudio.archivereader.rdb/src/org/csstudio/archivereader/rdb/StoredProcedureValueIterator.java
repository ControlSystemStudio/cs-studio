package org.csstudio.archivereader.rdb;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import oracle.jdbc.OracleTypes;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.utility.rdb.TimeWarp;

/** Value Iterator that provides 'optimized' data by calling
 *  a stored database procedure.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StoredProcedureValueIterator extends AbstractRDBValueIterator
{
    /** Values received from the stored procedure */
    private IValue values[] = null;
    
    /** Iteration index into <code>values</code>, points to what
     *  <code>next()</code> will return or -1
     */
    private int index = -1;

    /** Initialize
     *  @param reader RDBArchiveReader
     *  @param channel_id ID of channel
     *  @param start Start time
     *  @param end End time
     *  @param count Desired value count
     *  @throws Exception on error
     */
    public StoredProcedureValueIterator(final RDBArchiveReader reader,
            final int channel_id, final ITimestamp start, final ITimestamp end,
            final int count) throws Exception
    {
        super(reader, channel_id);
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
        final CallableStatement statement = reader.getRDB().getConnection().prepareCall(
            "begin ? := chan_arch_sns.archive_reader_pkg.get_browser_data(?, ?, ?, ?); end;");
        
        reader.addForCancellation(statement);
        try
        {
            statement.registerOutParameter(1, OracleTypes.CURSOR);
            statement.setInt(2, channel_id);
            statement.setTimestamp(3, TimeWarp.getSQLTimestamp(start));
            statement.setTimestamp(4, TimeWarp.getSQLTimestamp(end));
            statement.setInt(5, count);
            statement.setFetchDirection(ResultSet.FETCH_FORWARD);
            statement.setFetchSize(1000);
            statement.execute();
            final ResultSet result = (ResultSet) statement.getObject(1);
            result.setFetchSize(1000);
            
            // Determine result type: min/max/average table or
            // fallback to SAMPLE table format?
            final ResultSetMetaData meta = result.getMetaData();
            final int N = meta.getColumnCount();
            if (N == 9)
                values = decodeOptimizedTable(result);
            else
                values = decodeSampleTable(result);
            index = 0;
        }
        catch (Exception ex)
        {
            final String message = ex.getMessage();
            if (message != null  &&  message.startsWith(ORACLE_CANCELLATION))
            {
                // Not a real error; return empty iterator
            }
            else
                throw ex;
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
        IValue last_value = null;
        while (result.next())
        {
            // Time stamp
            final ITimestamp time = TimeWarp.getCSSTimestamp(result.getTimestamp(2));

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
            
            // If this value starts a 'gap' in the data, but the previous
            // sample was 'good', extrapolate that last sample so that the
            // plot draws a line up to the start of the gap.
            // TODO this could be removed if the XYGraph handled gaps the same way
            if (!severity.hasValue()  &&
                last_value != null  && last_value.getSeverity().hasValue())
            {   // Patch last value to have current time
                last_value = changeTimestamp(last_value, time);
                tmp_values.add(last_value);
            }
            
            // WB==-1 indicates a String sample
            if (result.getInt(1) < 0)
                last_value = ValueFactory.createStringValue(time, severity, status,
                        IValue.Quality.Original, new String[] { result.getString(8) });
            else
            {   // Only one value within averaging bucket?
                final int cnt = result.getInt(9);
                if (cnt == 1)
                    last_value = ValueFactory.createDoubleValue(time, severity,
                            status, meta, IValue.Quality.Original,
                            new double[] { result.getDouble(7) });
                else // Decode min/max/average
                    last_value = ValueFactory.createMinMaxDoubleValue(time, severity,
                            status, meta, IValue.Quality.Interpolated,
                            new double[] { result.getDouble(7) },
                            result.getDouble(5),
                            result.getDouble(6));
            }
            tmp_values.add(last_value);
        }
        // Convert to plain array
        final IValue values[] = tmp_values.toArray(new IValue[tmp_values.size()]);
        return values;
    }

    /** Decode samples from SAMPLE table
     *  @param result ResultSet
     *  @return IValue array of samples
     *  @throws Exception on error
     */
    private IValue[] decodeSampleTable(final ResultSet result) throws Exception
    {
        final ArrayList<IValue> tmp_values = new ArrayList<IValue>();
        IValue last_value = null;
        while (result.next())
        {
            final IValue value = decodeSampleTableValue(result);
            // If this value starts a 'gap' in the data, but the previous
            // sample was 'good', extrapolate that last sample so that the
            // plot draws a line up to the start of the gap.
            // TODO this could be removed if the XYGraph handled gaps the same way
            if (!value.getSeverity().hasValue()  &&
                last_value != null  && last_value.getSeverity().hasValue())
            {   // Patch last value to have current time
                last_value = changeTimestamp(last_value, value.getTime());
                tmp_values.add(last_value);
            }
            tmp_values.add(value);
            last_value = value;
        }
        return tmp_values.toArray(new IValue[tmp_values.size()]);
    }

    /** {@inheritDoc} */
    public boolean hasNext()
    {
        return index >= 0;
    }

    /** {@inheritDoc} */
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
