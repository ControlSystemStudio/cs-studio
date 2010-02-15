package org.csstudio.archivereader.rdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.utility.rdb.TimeWarp;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** Value Iterator that reads from the SAMPLE table.
 *  @author Kay Kasemir
 */
public class RawSampleIterator extends AbstractRDBValueIterator
{
    /** SELECT ... for the start .. end samples. */
    private PreparedStatement sel_samples = null;
    
    /** Result of <code>sel_samples</code> */
    private ResultSet result_set = null;
    
    /** 'Current' value that <code>next()</code> will return,
     *  or <code>null</code>
     */
    private IValue value = null;
    
    /** The last value that next() returned */
    private IValue last_value = null;

    /** Initialize
     *  @param reader RDBArchiveReader
     *  @param channel_id ID of channel
     *  @param start Start time
     *  @param end End time
     *  @throws Exception on error
     */
    public RawSampleIterator(final RDBArchiveReader reader,
            final int channel_id, final ITimestamp start,
            final ITimestamp end) throws Exception
    {
        super(reader, channel_id);
        try
        {
            determineInitialSample(start, end);
        }
        catch (Exception ex)
        {
            final String message = ex.getMessage();
            if (message != null  &&  message.startsWith(ORACLE_CANCELLATION))
            {
                // Not a real error; return empty iterator
                value = null;
                return;
            }
            else
                throw ex;
        }
    }

    /** Get the samples: <code>result_set</code> will have the samples,
     *  <code>value</code> will contain the first sample
     *  @param start Start time
     *  @param end End time
     *  @throws Exception on error
     */
    private void determineInitialSample(final ITimestamp start, final ITimestamp end) throws Exception
    {
        Timestamp start_stamp = TimeWarp.getSQLTimestamp(start);
        final Timestamp end_stamp = TimeWarp.getSQLTimestamp(end);

        // Get time of initial sample
        final PreparedStatement statement =
            reader.getRDB().getConnection().prepareStatement(reader.getSQL().sample_sel_initial_time);
        reader.addForCancellation(statement);
        try
        {
            statement.setInt(1, channel_id);
            statement.setTimestamp(2, start_stamp);
            final ResultSet result = statement.executeQuery();
            if (result.next())
            {
                // System.out.print("Start time corrected from " + start_stamp);
                start_stamp = result.getTimestamp(1);
                // Oracle has nanoseconds in TIMESTAMP, MySQL in separate column 
                if (reader.getRDB().getDialect() == Dialect.MySQL)
                    start_stamp.setNanos(result.getInt(2));
                // System.out.println(" to " + start_stamp);
            }
        }
        finally
        {
            reader.removeFromCancellation(statement);
            statement.close();
        }

        // Fetch the samples
        sel_samples = reader.getRDB().getConnection().prepareStatement(
                reader.getSQL().sample_sel_by_id_start_end);
        sel_samples.setFetchDirection(ResultSet.FETCH_FORWARD);
        
        // Test w/ ~170000 raw samples:
        //     10  17   seconds
        //    100   6   seconds
        //   1000   4.x seconds
        //  10000   4.x seconds
        // 100000   4.x seconds
        // So default is bad. 100 or 1000 are good.
        // Bigger numbers don't help much in repeated tests, but
        // just to be on the safe side, use a bigger number.
        sel_samples.setFetchSize(100000);

        reader.addForCancellation(sel_samples);
        sel_samples.setInt(1, channel_id);
        sel_samples.setTimestamp(2, start_stamp);
        sel_samples.setTimestamp(3, end_stamp);
        result_set = sel_samples.executeQuery();
        // Get first sample
        if (result_set.next())
            value = decodeSampleTableValue(result_set);
        // else leave value null to indicate end of samples
    }

    /** {@inheritDoc} */
    public boolean hasNext()
    {
        return value != null;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    public IValue next() throws Exception
    {
        // Remember value to return...
        final IValue result = value;
        
        // If this value starts a 'gap' in the data, but the previous
        // sample was 'good', extrapolate that last sample so that the
        // plot draws a line up to the start of the gap.
        // TODO this could be removed if the XYGraph handled gaps the same way
        if (!result.getSeverity().hasValue()  &&
            last_value != null  && last_value.getSeverity().hasValue())
        {   // Patch last value to have current time
            final IValue extra = changeTimestamp(last_value, result.getTime());
            last_value = null; // Do this only once. Next time it'll be 'value'
            return extra;
        }
        
        // This should not happen...
        if (result_set == null)
            throw new Exception("RawSampleIterator.next(" + channel_id + ") called after end");
        
        // ... and prepare next value
        try
        {
            if (result_set.next())
                value = decodeSampleTableValue(result_set);
            else
                close();
        }
        catch (Exception ex)
        {
            close();
            final String message = ex.getMessage();
            if (message != null  &&  message.startsWith(ORACLE_CANCELLATION))
            {
                // Not a real error; return empty iterator
            }
            else
                throw ex;
        }
        last_value = result;
        return result;
    }

    /** Release all database resources.
     *  OK to call more than once.
     */
    @Override
    public void close()
    {
        super.close();
        value = null;
        if (result_set != null)
        {
            try
            {
                result_set.close();
            }
            catch (Exception ex)
            {
                // Ignore
            }
            result_set = null;
        }
        if (sel_samples != null)
        {
            reader.removeFromCancellation(sel_samples);
            try
            {
                sel_samples.close();
            }
            catch (Exception ex)
            {
                // Ignore
            }
            sel_samples = null;
        }
    }
}
