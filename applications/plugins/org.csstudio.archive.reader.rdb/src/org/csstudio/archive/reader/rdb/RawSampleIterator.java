/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

/** Value Iterator that reads from the SAMPLE table.
 *  @author Kay Kasemir
 *  @author Lana Abadie (PostgreSQL)
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
    private VType value = null;

    /** Initialize
     *  @param reader RDBArchiveReader
     *  @param channel_id ID of channel
     *  @param start Start time
     *  @param end End time
     *  @throws Exception on error
     */
    public RawSampleIterator(final RDBArchiveReader reader,
            final int channel_id, final Timestamp start,
            final Timestamp end) throws Exception
    {
        super(reader, channel_id);
        try
        {
            determineInitialSample(start, end);
        }
        catch (Exception ex)
        {
            if (! RDBArchiveReader.isCancellation(ex))
                throw ex;
            // Else: Not a real error; return empty iterator
            value = null;
        }
    }

    /** Get the samples: <code>result_set</code> will have the samples,
     *  <code>value</code> will contain the first sample
     *  @param start Start time
     *  @param end End time
     *  @throws Exception on error, including cancellation
     */
    private void determineInitialSample(final Timestamp start, final Timestamp end) throws Exception
    {
        java.sql.Timestamp start_stamp = TimestampHelper.toSQLTimestamp(start);
        final java.sql.Timestamp end_stamp = TimestampHelper.toSQLTimestamp(end);

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
                if (reader.getRDB().getDialect() == Dialect.MySQL || reader.getRDB().getDialect() == Dialect.PostgreSQL)
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
        if (reader.useArrayBlob())
	        sel_samples = reader.getRDB().getConnection().prepareStatement(
	                reader.getSQL().sample_sel_by_id_start_end_with_blob);
        else
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
        sel_samples.setFetchSize(Preferences.getFetchSize());

        reader.addForCancellation(sel_samples);
        sel_samples.setInt(1, channel_id);
        sel_samples.setTimestamp(2, start_stamp);
        sel_samples.setTimestamp(3, end_stamp);
        result_set = sel_samples.executeQuery();
        // Get first sample
        if (result_set.next())
            value = decodeSampleTableValue(result_set, true);
        // else leave value null to indicate end of samples
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return value != null;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public VType next() throws Exception
    {
        // This should not happen...
        if (result_set == null)
            throw new Exception("RawSampleIterator.next(" + channel_id + ") called after end");

        // Remember value to return...
        final VType result = value;
        // ... and prepare next value
        try
        {
            if (result_set.next())
                value = decodeSampleTableValue(result_set, true);
            else
                close();
        }
        catch (Exception ex)
        {
            close();
            if (! RDBArchiveReader.isCancellation(ex))
                throw ex;
            // Else: Not a real error; return empty iterator
        }
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
