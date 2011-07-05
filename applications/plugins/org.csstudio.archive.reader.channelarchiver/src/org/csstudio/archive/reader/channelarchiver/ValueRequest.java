/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.AsyncCallback;
import org.apache.xmlrpc.XmlRpcClient;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/** Handles the "archiver.values" request and its results.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueRequest implements AsyncCallback
{
	/** Helper for passing the result or an error from the XML-RPC callback
	 *  to the ValueRequest that waits for it
	 */
	static class Result
	{
		boolean isSet = false;
	    Vector<Object> xml_rpc_result = null;
	    Exception xml_rpc_exception = null;

		synchronized void clear()
        {
			notify(null, null);
        }

		synchronized void setError(final Exception error)
        {
			notify(null, error);
        }

		synchronized void setData(Vector<Object> data)
        {
			notify(data, null);
        }

		private void notify(final Vector<Object> data, final Exception error)
        {
			xml_rpc_result = data;
			xml_rpc_exception = error;
			isSet = true;
			notifyAll();
        }
	};

	final private ChannelArchiverReader reader;
	final private int key;
	final private String channels[];
	final private ITimestamp start, end;
	final private int how;
    final private Object parms[];

    /** Quality to use for received samples unless automatic_quality */
    final private IValue.Quality quality;

	/** Determine quality automatically based on received sample?
     *  With min/max: interpolated?
     */
    private boolean automatic_quality = false;

    // Possible 'type' IDs for the received values.
	final private static int TYPE_STRING = 0;
    final private static int TYPE_ENUM = 1;
    final private static int TYPE_INT = 2;
    final private static int TYPE_DOUBLE = 3;

    final private Result result = new Result();

    /** The result of the query */
    private IValue samples[];

	/** Constructor for new value request.
	 *  @param reader ChannelArchiverReader
	 *  @param key Archive key
	 *  @param channel Channel name
	 *  @param start Start time for retrieval
	 *  @param end  End time for retrieval
     *  @param optimized Get optimized or raw data?
	 *  @param count Number of values
	 */
	public ValueRequest(ChannelArchiverReader reader,
			int key, String channel,
			ITimestamp start, ITimestamp end, boolean optimized, int count)
	        throws Exception
	{
        this.reader = reader;
        this.key = key;
        this.channels = new String[] { channel };
        this.start = start;
        this.end = end;

        // Check parms
        if (optimized)
        {
            quality = IValue.Quality.Interpolated;
            if (reader.getVersion() < 1)
            {   // Old server: Use plot-binning with bin count
                how = reader.getRequestCode("plot-binning");
                parms = new Object[] { Integer.valueOf(count) };
            }
            else
            {   // New server: Use min/max/average with seconds
                int secs = (int) ((end.toDouble() - start.toDouble()) / count);
                if (secs < 1)
                    secs = 1;
                how = reader.getRequestCode("average");
                parms = new Object[] { Integer.valueOf((int)secs) };
                automatic_quality = true;
            }
        }
        else
        {   // All others use 'Integer count'
            // Raw == Original, all else is somehow interpolated
            how = reader.getRequestCode("raw");
            parms = new Object[] { Integer.valueOf(count) };
            quality = IValue.Quality.Original;
        }
	}

	/** @see org.csstudio.archive.channelarchiver.ClientRequest#read() */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void read(XmlRpcClient xmlrpc) throws Exception
	{
        final Vector<Object> params = new Vector<Object>(8);
		params.add(Integer.valueOf(key));
		params.add(channels);
		params.add(Integer.valueOf((int)start.seconds()));
		params.add(Integer.valueOf((int)start.nanoseconds()));
		params.add(Integer.valueOf((int)end.seconds()));
		params.add(Integer.valueOf((int)end.nanoseconds()));
        params.add(parms[0]);
		params.add(Integer.valueOf(how));
		// xmlrpc.execute("archiver.values", params);
        xmlrpc.executeAsync("archiver.values", params, this);

		// Wait for AsynCallback to set the result
	    final Vector<Object> xml_rpc_result;
		synchronized (result)
        {
			while (! result.isSet)
				result.wait();
			// Failed?
			if (result.xml_rpc_exception != null)
			    throw new Exception("archiver.values call failed: " + result.xml_rpc_exception.getMessage());
			// Cancelled?
			if (result.xml_rpc_result == null)
			{
			    samples = new IValue[0];
			    return;
			}
			xml_rpc_result = result.xml_rpc_result;
        }

		// result := { string name,  meta, int32 type,
        //              int32 count,  values }[]
		final int num_returned_channels = xml_rpc_result.size();
		if (num_returned_channels != 1)
            throw new Exception("archiver.values returned data for " + num_returned_channels + " channels?");

		final Hashtable<String, Object> channel_data =
		    (Hashtable<String, Object>) xml_rpc_result.get(0);
        final String name = (String)channel_data.get("name");
        final int type = (Integer)channel_data.get("type");
        final int count = (Integer)channel_data.get("count");
		IMetaData meta;
		try
		{
			meta = decodeMetaData(name, type, (Hashtable)channel_data.get("meta"));
			samples = decodeValues(type, count, meta,
					(Vector)channel_data.get("values"));
		}
		catch (Exception e)
		{
			throw new Exception("Error while decoding values for channel '"
					+ name + "': " + e.getMessage(), e);
		}
	}

    /** Cancel an ongoing read.
     *  <p>
     *  Somewhat fake, because there is no way to stop the underlying
     *  XML-RPC request, but we can abandon the read and pretend
     *  that we didn't receive any data.
     */
	public void cancel()
    {
        result.clear();
    }

	/** @see AsyncCallback */
    @Override
    public void handleError(Exception error, URL arg1, String arg2)
    {
        result.setError(error);
    }

    /** @see AsyncCallback */
    @Override
    @SuppressWarnings("unchecked")
    public void handleResult(Object data, URL arg1, String arg2)
    {
    	result.setData((Vector<Object>) data);
    }

    /** Parse the MetaData from the received XML-RPC response.
	 * @param name */
	@SuppressWarnings({ "rawtypes" })
    private IMetaData decodeMetaData(final String name, int value_type, Hashtable meta_hash)
		throws Exception
	{
		// meta := { int32 type;
		//		     type==0: string states[],
		//		     type==1: double disp_high,
		//		              double disp_low,
		//		              double alarm_high,
		//		              double alarm_low,
		//		              double warn_high,
		//		              double warn_low,
		//		              int prec,  string units
		//         }
		final int meta_type = (Integer) meta_hash.get("type");
		if (meta_type < 0 || meta_type > 1)
			throw new Exception("Invalid 'meta' type " + meta_type);
		if (meta_type == 1)
		{
            // The 2.8.1 server will give 'ENUM' type values
            // with Numeric meta data, units = "<No data>"
            // as an error message.
			return ValueFactory.createNumericMetaData(
	                (Double) meta_hash.get("disp_low"),
                    (Double) meta_hash.get("disp_high"),
                    (Double) meta_hash.get("warn_low"),
                    (Double) meta_hash.get("warn_high"),
                    (Double) meta_hash.get("alarm_low"),
					(Double) meta_hash.get("alarm_high"),
					(Integer) meta_hash.get("prec"),
					(String) meta_hash.get("units"));
		}
        //  else
		if (! (value_type == TYPE_ENUM  ||  value_type == TYPE_STRING))
			throw new Exception(
					"Received enumerated meta information for value type "
					+ value_type);
		final Vector state_vec = (Vector) meta_hash.get("states");
		final int N = state_vec.size();
		final String states[] = new String[N];
		// Silly loop because of type warnings from state_vec.toArray(states)
		for (int i=0; i<N; ++i)
			states[i] = (String) state_vec.get(i);
		return ValueFactory.createEnumeratedMetaData(states);
	}

	/** Parse the values from the received XML-RPC response. */
	@SuppressWarnings({ "rawtypes" })
    private IValue [] decodeValues(int type, int count, IMetaData meta,
			                      Vector value_vec) throws Exception
	{
        // values := { int32 stat,  int32 sevr,
	    //             int32 secs,  int32 nano,
	    //             <type> value[] } []
		// [{secs=1137596340, stat=0, nano=344419666, value=[0.79351], sevr=0},
		//  {secs=1137596400, stat=0, nano=330619666, value=[0.79343], sevr=0},..]
		final int num_samples = value_vec.size();
		final IValue samples[] = new IValue[num_samples];
		for (int si=0; si<num_samples; ++si)
		{
			final Hashtable sample_hash = (Hashtable) value_vec.get(si);
			final long secs = (Integer)sample_hash.get("secs");
			final long nano = (Integer)sample_hash.get("nano");
			final ITimestamp time = TimestampFactory.createTimestamp(secs, nano);
			final int stat_code = (Integer)sample_hash.get("stat");
			final int sevr_code = (Integer)sample_hash.get("sevr");
            final SeverityImpl sevr = reader.getSeverity(sevr_code);
            final String stat = reader.getStatus(sevr, stat_code);
			final Vector vv = (Vector)sample_hash.get("value");

			if (type == TYPE_DOUBLE)
			{
				final double values[] = new double[count];
				for (int vi=0; vi<count; ++vi)
					values[vi] = (Double)vv.get(vi);
                // Check for "min", "max".
                // Only handles min/max for double, but that's OK
                // since for now that's all that the server does as well.
                if (sample_hash.containsKey("min") &&
                    sample_hash.containsKey("max"))
                {   // It's a min/max double, certainly interpolated
                    final double min = (Double)sample_hash.get("min");
                    final double max = (Double)sample_hash.get("max");
                    samples[si] = ValueFactory.createMinMaxDoubleValue(
                                    time, sevr, stat, (INumericMetaData)meta,
                                    IValue.Quality.Interpolated, values, min, max);
                }
                else
                {   // Was this from a min/max/avg request?
                    // Yes: Then we ran into a raw value.
                    // No: Then it's whatever quality we expected in general
                    final IValue.Quality q = automatic_quality ?
                                        IValue.Quality.Original : quality;
                    samples[si] = ValueFactory.createDoubleValue(
                                    time, sevr, stat, (INumericMetaData)meta,
                                    q, values);
                }
			}
			else if (type == TYPE_ENUM)
			{
				// The 2.8.1 server will give 'ENUM' type values
	            // with Numeric meta data, units = "<No data>".
	            // as an error message -> Handle it by returning
			    // the data as long with the numeric meta that we have.
				if (meta instanceof INumericMetaData)
				{
	                final long values[] = new long[count];
	                for (int vi=0; vi<count; ++vi)
	                    values[vi] = (long) ((Integer)vv.get(vi));
	                samples[si] = ValueFactory.createLongValue(time, sevr, stat,
	                                (INumericMetaData)meta, quality, values);
				}
				else
				{
	                final int values[] = new int[count];
	                for (int vi=0; vi<count; ++vi)
	                    values[vi] = (Integer)vv.get(vi);
	                samples[si] = ValueFactory.createEnumeratedValue(time, sevr, stat,
                                (IEnumeratedMetaData)meta, quality, values);
				}
			}
			else if (type == TYPE_STRING)
			{
				final String values[] = new String[] { (String)vv.get(0) };
                samples[si] = ValueFactory.createStringValue(time, sevr, stat,
                                quality, values);
			}
			else if (type == TYPE_INT)
			{
				final long values[] = new long[count];
				for (int vi=0; vi<count; ++vi)
					values[vi] = (long) ((Integer)vv.get(vi));
                samples[si] = ValueFactory.createLongValue(time, sevr, stat,
                                (INumericMetaData)meta, quality, values);
			}
			else
				throw new Exception("Unknown value type " + type);
		}
		return samples;
	}

	/** @return Samples */
	public IValue[] getSamples()
	{
		return samples;
	}
}
