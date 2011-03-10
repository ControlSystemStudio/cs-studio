/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.csstudio.archive.rdb.internal.EnumMetaDataHelper;
import org.csstudio.archive.rdb.internal.NumericMetaDataHelper;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** Iterator over raw archive samples.
 *  @author Kay Kasemir
 *  @author Lana Abadie (PostgreSQL)
 */
@SuppressWarnings("nls")
public class RawSampleIterator implements SampleIterator
{
	/** Archive against which this iterator runs. */
	final private RDBArchive archive;

	/** Channel.
	 *  We set its meta data in <code>determineMetaData()</code>
	 *  unless it's already defined.
	 */
	final private ChannelConfig channel;

	/** SELECT ... for the start .. end samples. */
	private PreparedStatement sel_samples = null;

    /** SELECT ... for the array samples. */
	private PreparedStatement sel_array_samples = null;

	/** 'Current' value that <code>next()</code> will return,
	 *  or <code>null</code>
	 */
	private IValue value = null;

	/** Result of <code>sel_samples</code> */
	private ResultSet result_set = null;

	/** Special Severity that's INVALID without a value */
	private static ISeverity no_value_severity = null;

	/** For performance reasons, we remember the fact that we
	 *  found (or didn't find) array samples.
	 *  <p>
	 *  Initially: Assume there are array samples.
	 *  Once a scalar is identified, we stick with scalars.
	 *  The obvious drawback: If only a few samples are array
	 *  samples, we're likely to ignore them.
	 */
	private boolean data_is_scalar = false;

	/** Numeric meta data used as default if nothing else is known */
    private static INumericMetaData default_numeric_meta;

	/** Constructor, to be called by RDBArchive */
	public RawSampleIterator(final RDBArchive archive,
				   final ChannelConfig channel,
				   final ITimestamp start,
				   final ITimestamp end) throws Exception
	{
		this.archive = archive;
		this.channel = channel;
		try
		{
            determineMetaData();
            determineInitialSample(start, end);
		}
		catch (Exception ex)
		{
		    // ORA-01013: user requested cancel of current operation
		    if (ex.getMessage().startsWith("ORA-01013"))
		    {
		        // Not a real error; return empty iterator
		        Activator.getLogger().log(Level.FINE,
		                "Channel {0} request cancelled", channel.getName());
		        close();
		    }
		}
	}

	/** Read meta data.
     *  <p>
     *  Called once when getting initial sample.
     *  <p>
     *  Sets the channel's meta data to what's found or <code>null</code>
     */
    private void determineMetaData()
    {
        // Don't bother if already defined.
        if (channel.getMetaData() != null)
            return;
        try
        {
            final INumericMetaData numeric = NumericMetaDataHelper.get(archive, channel);
            if (numeric != null)
            {
                channel.setMetaData(numeric);
                return;
            }
    	    final IEnumeratedMetaData enums = EnumMetaDataHelper.get(archive, channel);
    	    if (enums != null)
    	    {
    	        channel.setMetaData(enums);
    	        return;
    	    }
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING,
                    "Channel " + channel.getName() + " meta data read error", ex);
            // Continue
        }
    }

    /** @return Some default numeric meta data */
    private INumericMetaData getDefaultNumericMeta()
    {
        if (default_numeric_meta == null)
            default_numeric_meta =
                ValueFactory.createNumericMetaData(0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 2, "");
        return default_numeric_meta;
    }

    /** Get the initial sample, prepare result_set for the bulk of samples. */
    private void determineInitialSample(final ITimestamp start,
            final ITimestamp end)
            throws Exception
    {
        final Connection connection = archive.getRDB().getConnection();
        // To allow multiple sample iterators at the same time,
		// each iterator prepares its own statements, instead
		// of lazily initializing a single, shared one.

		// Get initial sample
        final Timestamp start_stamp = start.toSQLTimestamp();
        final String initial_sql = archive.getSQL().sample_sel_initial_by_id_time;
        PreparedStatement sel_initial_sample =
            connection.prepareStatement(initial_sql);
        archive.addForCancellation(sel_initial_sample);
        try
        {
    		sel_initial_sample.setInt(1, channel.getId());
    		sel_initial_sample.setTimestamp(2, start_stamp);
    		result_set = sel_initial_sample.executeQuery();
    		if (result_set.next())
    			value = decodeValue(result_set);
        }
        finally
        {
            archive.removeFromCancellation(sel_initial_sample);
        }
		result_set.close();
		result_set = null;
		sel_initial_sample.close();
		sel_initial_sample = null;

		// Start fetching the bulk of the samples
		sel_samples = connection.prepareStatement(
				archive.getSQL().sample_sel_by_id_start_end);
        archive.addForCancellation(sel_samples);
        try
        {
    		sel_samples.setInt(1, channel.getId());
    		sel_samples.setTimestamp(2, start_stamp);
    		sel_samples.setTimestamp(3, end.toSQLTimestamp());
    		result_set = sel_samples.executeQuery();
    		// If there's no initial sample, get the first one from the bulk result
    		if (value == null  &&  result_set.next())
    			value = decodeValue(result_set);
            // else leave value on the initial sample
        }
        finally
        {
            archive.removeFromCancellation(sel_samples);
        }
    }

	/** @return <code>true</code> if there is another value */
	@Override
    public boolean hasNext()
	{
		return value != null;
	}

	/** Return the next sample.
	 *  <p>
	 *  Should only be called after <code>hasNext()</code>
	 *  indicated that there is in fact another sample.
	 */
	@Override
    public IValue next() throws Exception
	{	// Remember value to return...
		final IValue result = value;

		// This should not happen...
		if (result_set == null)
		{
            Activator.getLogger().log(Level.SEVERE,
                "RawSampleIterator.next({0}) called after end", channel.getName());
		    return null;
		}

		// ... and prepare next value
		try
		{
    		if (result_set.next())
    			value = decodeValue(result_set);
    		else
    		    close();
		}
		catch (Exception ex)
		{
            final String message = ex.getMessage();
            if (message != null  &&  message.startsWith("ORA-01013"))
            {
                // Not a real error; return empty iterator
                Activator.getLogger().log(Level.FINE,
                        "Channel {0} request cancelled", channel.getName());
                close();
            }
		}
		return result;
	}

	/** Release all database resources.
	 *  OK to call more than once.
	 */
	private void close()
	{
        value = null;
        if (result_set != null)
        {
            try
            {
                result_set.close();
            }
            catch (Exception ex)
            {
                Activator.getLogger().log(Level.FINE, "'close' error", ex);
            }
            result_set = null;
        }
        if (sel_samples != null)
        {
            try
            {
                sel_samples.close();
            }
            catch (Exception ex)
            {
                Activator.getLogger().log(Level.FINE, "'close' error", ex);
            }
            sel_samples = null;
        }
        closeArraySampleSel();
	}

	/** Close the select statement for array samples. */
    private void closeArraySampleSel()
    {
        if (sel_array_samples != null)
        {
            try
            {
                sel_array_samples.close();
            }
            catch (Exception ex)
            {
                Activator.getLogger().log(Level.FINE, "'close' error", ex);
            }
            sel_array_samples = null;
        }
    }

	/** Decode the current result set values into an IValue.
	 *  @param res ResultSet that must contain time, severity, ..., value
	 *  @return IValue Decoded IValue
	 *  @throws Exception on error
	 */
	private IValue decodeValue(final ResultSet res) throws Exception
	{
	    final Timestamp stamp = res.getTimestamp(1);
	    // Oracle has nanoseconds in TIMESTAMP, MySQL in separate column
	    if (archive.getRDB().getDialect() == Dialect.MySQL || archive.getRDB().getDialect() == Dialect.PostgreSQL)
	        stamp.setNanos(res.getInt(7));
		final ITimestamp time = TimestampFactory.fromSQLTimestamp(stamp);
		ISeverity severity = archive.getSeverity(res.getInt(2));
		final String status = archive.getStatusString(res.getInt(3));

		// Hard-coded knowledge:
		// When the severity is INVALID and the status indicates
    	// that the archive is off or channel was disconnected,
		// we use the special INVALID severity that marks a sample
		// without a value.
    	if (severity.isInvalid() &&
	    	 (status.equalsIgnoreCase("Archive_Off") ||
			  status.equalsIgnoreCase("Disconnected")))
    	{
    		severity = getNoValueSeverity();
    	}

		// Determine the value type
        // Try double
        final double dbl0 = res.getDouble(5);
        if (! res.wasNull())
        {
            final IMetaData meta = channel.getMetaData();
            // Is it an error to have enum strings for double samples?
            // In here, we handle it by returning enum samples,
            // because the meta data would be wrong for double values.
            if (meta instanceof IEnumeratedMetaData)
                return ValueFactory.createEnumeratedValue(time, severity, status,
                        (IEnumeratedMetaData) meta, IValue.Quality.Original,
                        new int [] { (int) dbl0 });
            // Double data. Get array elements - if any.
            final double data[] = readArrayElements(severity, stamp, dbl0);
            if (meta instanceof INumericMetaData)
                return ValueFactory.createDoubleValue(time, severity, status,
                        (INumericMetaData)meta, IValue.Quality.Original, data);
            // Make some meta data up
            return ValueFactory.createDoubleValue(time, severity, status,
                    getDefaultNumericMeta(), IValue.Quality.Original, data);
        }
    	// Try integer
		final int num = res.getInt(4);
		if (! res.wasNull())
		{   // Enumerated integer?
            final IMetaData meta = channel.getMetaData();
            if (meta instanceof IEnumeratedMetaData)
	            return ValueFactory.createEnumeratedValue(time, severity, status,
	                    (IEnumeratedMetaData) meta, IValue.Quality.Original,
	                    new int [] { num });
		    // Fall back to plain (long) integer
            if (meta instanceof INumericMetaData)
    			return ValueFactory.createLongValue(time, severity, status,
    					(INumericMetaData)meta, IValue.Quality.Original,
    					new long [] { num });
            return ValueFactory.createLongValue(time, severity, status,
                    getDefaultNumericMeta(), IValue.Quality.Original,
                    new long [] { num });
		}
		// Default to string
		final String txt = res.getString(6);
		return ValueFactory.createStringValue(time, severity, status,
				IValue.Quality.Original, new String [] { txt });
	}

    /** Given the time and first element of the  sample, see if there
	 *  are more array elements.
	 *  @return Array with given element and maybe more.
	 */
	private double[] readArrayElements(final ISeverity severity,
	        final Timestamp stamp,
            final double dbl0) throws Exception
    {
        // For performance reasons, we only look for array data
        // until we hit a scalar sample.
	    if (data_is_scalar)
	        return new double [] { dbl0 };

        // See if there are more array elements
	    if (sel_array_samples == null)   // Lazy initialization
	        sel_array_samples = archive.getRDB().getConnection().prepareStatement(
	                archive.getSQL().sample_sel_array_vals);
	    sel_array_samples.setInt(1, channel.getId());
	    sel_array_samples.setTimestamp(2, stamp);
	    // MySQL keeps nanoseconds in designated column, not TIMESTAMP
	    if (archive.getRDB().getDialect() == Dialect.MySQL || archive.getRDB().getDialect() == Dialect.PostgreSQL)
	        sel_array_samples.setInt(3, stamp.getNanos());

        // Assemble array of unknown size in ArrayList ....
        final ArrayList<Double> vals = new ArrayList<Double>();
	    archive.addForCancellation(sel_array_samples);
	    try
	    {
	        final ResultSet res = sel_array_samples.executeQuery();
    	    vals.add(new Double(dbl0));
    	    while (res.next())
    	        vals.add(res.getDouble(1));
    	    res.close();
	    }
	    finally
	    {
	        archive.removeFromCancellation(sel_array_samples);
	    }

        // Convert to plain double array
	    final int N = vals.size();
	    final double ret[] = new double[N];
	    for (int i = 0; i < N; i++)
	        ret[i] = vals.get(i).doubleValue();
	    // Check if it's in fact just a scalar, and a valid one
        if (N == 1  &&  !severity.isInvalid())
        {   // Found a perfect non-array sample:
            // Assume that the data is scalar, skip the array check from now on
            data_is_scalar = true;
            closeArraySampleSel();
        }
	    return ret;
    }

    /** @return Special Severity that's INVALID without a value */
	private static ISeverity getNoValueSeverity()
	{
		// Lazy init.
		if (no_value_severity == null)
			no_value_severity = new ISeverity()
		{
			@Override
            public boolean hasValue()  { return false; }
            @Override
			public boolean isInvalid() { return true;  }
            @Override
			public boolean isMajor()   { return false; }
            @Override
			public boolean isMinor()   { return false; }
            @Override
			public boolean isOK()      { return false; }
			@Override
			public String toString()   { return "INVALID"; }
		};
		return no_value_severity;
	}
}
