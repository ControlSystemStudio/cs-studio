/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.AsyncCallback;
import org.apache.xmlrpc.XmlRpcClient;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.ArchiveVString;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

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
	final private Timestamp start, end;
	final private int how;
    final private Object parms[];

    // Possible 'type' IDs for the received values.
	final private static int TYPE_STRING = 0;
    final private static int TYPE_ENUM = 1;
    final private static int TYPE_INT = 2;
    final private static int TYPE_DOUBLE = 3;

    final private Result result = new Result();

    /** The result of the query */
    private VType samples[];

	/** Constructor for new value request.
	 *  @param reader ChannelArchiverReader
	 *  @param key Archive key
	 *  @param channel Channel name
	 *  @param start Start time for retrieval
	 *  @param end  End time for retrieval
     *  @param optimized Get optimized or raw data?
	 *  @param count Number of values
	 */
	public ValueRequest(final ChannelArchiverReader reader,
			final int key, final String channel,
			final Timestamp start, final Timestamp end, final boolean optimized, final int count)
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
            if (reader.getVersion() < 1)
            {   // Old server: Use plot-binning with bin count
                how = reader.getRequestCode("plot-binning");
                parms = new Object[] { Integer.valueOf(count) };
            }
            else
            {   // New server: Use min/max/average with seconds
                int secs = (int) (end.durationFrom(start).toSeconds() / count);
                if (secs < 1)
                    secs = 1;
                how = reader.getRequestCode("average");
                parms = new Object[] { Integer.valueOf((int)secs) };
            }
        }
        else
        {   // All others use 'Integer count'
            // Raw == Original, all else is somehow interpolated
            how = reader.getRequestCode("raw");
            parms = new Object[] { Integer.valueOf(count) };
        }
	}

	/** @see org.csstudio.archive.channelarchiver.ClientRequest#read() */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void read(XmlRpcClient xmlrpc) throws Exception
	{
        final Vector<Object> params = new Vector<Object>(8);
		params.add(Integer.valueOf(key));
		params.add(channels);
		params.add(Integer.valueOf((int)start.getSec()));
		params.add(Integer.valueOf(start.getNanoSec()));
		params.add(Integer.valueOf((int)end.getSec()));
		params.add(Integer.valueOf(end.getNanoSec()));
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
			    samples = new VType[0];
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
		try
		{
			final Object meta = decodeMetaData(type, (Hashtable)channel_data.get("meta"));
			final Display display;
			final List<String> labels;
			if (meta instanceof Display)
			{
				display = (Display) meta;
				labels = null;
			}
			else if (meta instanceof List)
			{
				display = null;
				labels = (List) meta;
			}
			else
			{
				display = null;
				labels = null;
			}
			samples = decodeValues(type, count, display, labels,
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
	 *  @param value_type Type code of received values
	 *  @param meta_hash Hash with meta data to decode
	 *  @return {@link Display} or List of {@link String}[] depending on data type
	 */
	@SuppressWarnings({ "rawtypes" })
    private Object decodeMetaData(final int value_type, final Hashtable meta_hash)
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
        	final NumberFormat format = NumberFormats.format((Integer) meta_hash.get("prec"));
			return ValueFactory.newDisplay(
					(Double) meta_hash.get("disp_low"),
					(Double) meta_hash.get("alarm_low"),
					(Double) meta_hash.get("warn_low"),
					(String) meta_hash.get("units"),
					format,
					(Double) meta_hash.get("warn_high"),
					(Double) meta_hash.get("alarm_high"),
					(Double) meta_hash.get("disp_high"),
					(Double) meta_hash.get("disp_low"),
					(Double) meta_hash.get("disp_high"));
		}
        //  else
		if (! (value_type == TYPE_ENUM  ||  value_type == TYPE_STRING))
			throw new Exception(
					"Received enumerated meta information for value type "
					+ value_type);
		final Vector state_vec = (Vector) meta_hash.get("states");
		final int N = state_vec.size();
		final List<String> states = new ArrayList<String>(N);
		// Silly loop because of type warnings from state_vec.toArray(states)
		for (int i=0; i<N; ++i)
			states.add((String) state_vec.get(i));
		return states;
	}

	/** Parse the values from the received XML-RPC response. */
	@SuppressWarnings({ "rawtypes" })
    private VType[] decodeValues(final int type, final int count, final Display display,
    		                     final List<String> labels, final Vector value_vec) throws Exception
	{
        // values := { int32 stat,  int32 sevr,
	    //             int32 secs,  int32 nano,
	    //             <type> value[] } []
		// [{secs=1137596340, stat=0, nano=344419666, value=[0.79351], sevr=0},
		//  {secs=1137596400, stat=0, nano=330619666, value=[0.79343], sevr=0},..]
		final int num_samples = value_vec.size();
		final VType samples[] = new VType[num_samples];
		int gg = -1;
		for (int si=0; si<num_samples; ++si)
		{
			final Hashtable sample_hash = (Hashtable) value_vec.get(si);
			final long secs = (Integer)sample_hash.get("secs");
			final int nano = (Integer)sample_hash.get("nano");
			final Timestamp time = Timestamp.of(secs, nano);
			final int stat_code = (Integer)sample_hash.get("stat");
			final int sevr_code = (Integer)sample_hash.get("sevr");
            final SeverityImpl sevr = reader.getSeverity(sevr_code);
            final String stat = reader.getStatus(sevr, stat_code);
			final Vector vv = (Vector)sample_hash.get("value");
			final AlarmSeverity severity = sevr.getSeverity();
			
			if (! sevr.hasValue()) continue;
			gg += 1;
			
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
                    samples[gg] = new ArchiveVStatistics(time, severity, stat, display,
                    		values[0], min, max, 0.0, 1);
                }
                else
                {   // Was this from a min/max/avg request?
                    // Yes: Then we ran into a raw value.
                    // No: Then it's whatever quality we expected in general
                	if (values.length == 1)
                		samples[gg] = new ArchiveVNumber(time, severity, stat, display, values[0]);
                	else
                		samples[gg] = new ArchiveVNumberArray(time, severity, stat, display, values);
                }
			}
			else if (type == TYPE_ENUM)
			{
				// The 2.8.1 server will give 'ENUM' type values
	            // with Numeric meta data, units = "<No data>".
	            // as an error message -> Handle it by returning
			    // the data as long with the numeric meta that we have.
				if (labels != null)
				{
					if (count < 0)
						throw new Exception("No values");
					final int index = (Integer)vv.get(0);
					samples[gg] = new ArchiveVEnum(time, severity, stat, labels, index);
				}
				else
				{
					if (count == 1)
                		samples[gg] = new ArchiveVNumber(time, severity, stat, display, (Integer)vv.get(0));
					else
					{
		                final int values[] = new int[count];
		                for (int vi=0; vi<count; ++vi)
		                    values[vi] = ((Integer)vv.get(vi));
                		samples[gg] = new ArchiveVNumberArray(time, severity, stat, display, values);
					}
				}
			}
			else if (type == TYPE_STRING)
			{
				final String value = (String)vv.get(0);
                samples[gg] = new ArchiveVString(time, severity, stat, value);
			}
			else if (type == TYPE_INT)
			{
				if (count == 1)
				{
					final int value = (Integer)vv.get(0);
					samples[gg] = new ArchiveVNumber(time, severity, stat, display, value);
				}
				else
				{
					final int values[] = new int[count];
					for (int vi=0; vi<count; ++vi)
						values[vi] = ((Integer)vv.get(vi));
					samples[gg] = new ArchiveVNumberArray(time, severity, stat, display, values);
				}
			}
			else
				throw new Exception("Unknown value type " + type);
		}
		final VType good_samples[] = new VType[gg+1];
		for (int si = 0; si<=gg; ++si) good_samples[si] = samples[si];
		return good_samples;
	}

	/** @return Samples */
	public VType[] getSamples()
	{
		return samples;
	}
}
