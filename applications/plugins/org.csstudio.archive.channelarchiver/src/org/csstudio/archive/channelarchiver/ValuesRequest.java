package org.csstudio.archive.channelarchiver;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.AsyncCallback;
import org.apache.xmlrpc.XmlRpcClient;
import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.ArchiveValues;
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
public class ValuesRequest implements AsyncCallback
{
	final private ArchiveServer server;
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

    // The result of the query
	private ArchiveValues archived_samples[];
    private Vector<Object> xml_rpc_result;
    private Exception xml_rpc_exception;

	/** Constructor for new value request.
	 *  <p>
	 *  Details regarding the meaning of 'count' for the different 'how'
	 *  values are in the XML-RPC description in the ChannelArchiver manual.
	 *
	 *  @param key Archive key
	 *  @param channels Vector of channel names
	 *  @param start Start time for retrieval
	 *  @param end  End time for retrieval
     *  @param how How to retrieve
	 *  @param parms Detailed parameters.
	 *  @throws ArchiveAccessException on accessing archive or computing averages failures
	 */
	public ValuesRequest(final ArchiveServer server,
			final int key, final String channels[],
			final ITimestamp start, final ITimestamp end, int how, Object parms[])
	        throws ArchiveAccessException
	{
        final String req_type = server.getRequestTypes()[how];

        // Check parms
        if (req_type.equals(org.csstudio.archive.ArchiveServer.GET_AVERAGE))
        {
            quality = IValue.Quality.Interpolated;
            if (server.getVersion() < 1)
            {
                // GET_AVERAGE for old server: Plot-binning
                if (! ((parms.length == 1)  &&  (parms[0] instanceof Double))) {
                    throw new ArchiveAccessException("Expected 'count' for " + req_type);
                }
                how = server.getRequestCode("plot-binning");
                // Uses bin count, so convert average interval back into bins
                final double interval = ((Double)parms[0]).doubleValue();
                final int bins = (int) ((end.toDouble() - start.toDouble()) / interval);
                parms = new Object[] { new Integer(bins) };
            }
            else
            {
                // GET_AVERAGE for new server: Min/max/average
                if (! ((parms.length == 1)  &&  (parms[0] instanceof Double))) {
                    throw new ArchiveAccessException("Expected 'Double delta' for GET_AVERAGE");
                }
                // We got the Double interval as per javadoc for the request type,
                // but the server actually only handles int...
                final double secs = ((Double)parms[0]).doubleValue();
                parms = new Object[] { new Integer((int)secs) };
                automatic_quality = true;
            }
        }
        else
        {   // All others use 'Integer count'
            if (! ((parms.length == 1)  &&  (parms[0] instanceof Integer))) {
                throw new ArchiveAccessException("Expected 'Integer count' for " + req_type);
            }

            // Raw == Original, all else is somehow interpolated
            if (req_type.equals(org.csstudio.archive.ArchiveServer.GET_RAW)) {
                quality = IValue.Quality.Original;
            } else {
                quality = IValue.Quality.Interpolated;
            }
        }

		this.server = server;
		this.key = key;
		this.channels = channels;
		this.start = start;
		this.end = end;
		this.how = how;
        this.parms = parms;
	}


	/**
	 * @param xmlrpc the client
	 * @throws ArchiveAccessException
	 */
    @SuppressWarnings("unchecked")
    public void read(final XmlRpcClient xmlrpc) throws ArchiveAccessException
	{
        xml_rpc_result = null;
        xml_rpc_exception = null;
		final Vector<Object> params = new Vector<Object>(8);
		params.add(new Integer(key));
		params.add(channels);
		params.add(new Integer((int)start.seconds()));
		params.add(new Integer((int)start.nanoseconds()));
		params.add(new Integer((int)end.seconds()));
		params.add(new Integer((int)end.nanoseconds()));
        params.add(parms[0]);
		params.add(new Integer(how));
		// xmlrpc.execute("archiver.values", params);
        xmlrpc.executeAsync("archiver.values", params, this);
		// Wait for AsynCallback to set the xml_rpc_result or .._exception
		synchronized (this)
        {
		    try {
                wait();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ArchiveAccessException("Wait during read has been interrupted.", e);
            }
        }
		if (xml_rpc_exception != null) {
            throw new ArchiveAccessException("archiver.values call failed: " + xml_rpc_exception.getMessage());
        }
		// Cancelled?
		if (xml_rpc_result == null)
		{
		    archived_samples = new ArchiveValues[channels.length];
	        for (int i=0; i<archived_samples.length; ++i) {
                archived_samples[i] =
	                new ArchiveValues(server, channels[i], new IValue[0]);
            }
		    return;
		}

		// result := { string name,  meta, int32 type,
        //              int32 count,  values }[]
		final int num_returned_channels = xml_rpc_result.size();
		archived_samples = new ArchiveValues[num_returned_channels];
		for (int channel_idx=0; channel_idx<num_returned_channels; ++channel_idx)
		{
			final Hashtable<String, Object> channel_data =
			    (Hashtable<String, Object>) xml_rpc_result.get(channel_idx);
            final String name = (String)channel_data.get("name");
            final int type = (Integer)channel_data.get("type");
            final int count = (Integer)channel_data.get("count");
			IMetaData meta;
			IValue samples[];
			try
			{
				meta = decodeMetaData(name, type, (Hashtable)channel_data.get("meta"));
				samples = decodeValues(type, count, meta,
						(Vector)channel_data.get("values"));
			}
			catch (final Exception e)
			{
				throw new ArchiveAccessException("Error while decoding values for channel '"
						+ name + "': " + e.getMessage(), e);
			}
			archived_samples[channel_idx] =
				new ArchiveValues(server, name, samples);
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
        synchronized (this)
        {
            xml_rpc_exception = null;
            xml_rpc_result = null;
            notifyAll();
        }
    }

	/** @see AsyncCallback */
    public void handleError(final Exception error, final URL arg1, final String arg2)
    {
        synchronized (this)
        {
            xml_rpc_exception = error;
            notifyAll();
        }
    }

    /** @see AsyncCallback */
    @SuppressWarnings("unchecked")
    public void handleResult(final Object result, final URL arg1, final String arg2)
    {
        synchronized (this)
        {
            xml_rpc_result = (Vector<Object>) result;
            notifyAll();
        }
    }

    /** Parse the MetaData from the received XML-RPC response.
	 * @param name */
	@SuppressWarnings("unchecked")
    private IMetaData decodeMetaData(final String name, final int value_type, final Hashtable meta_hash)
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
		if ((meta_type < 0) || (meta_type > 1)) {
            throw new Exception("Invalid 'meta' type " + meta_type);
        }
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
		if (! ((value_type == TYPE_ENUM)  ||  (value_type == TYPE_STRING))) {
            throw new Exception(
					"Received enumerated meta information for value type "
					+ value_type);
        }
		final Vector state_vec = (Vector) meta_hash.get("states");
		final int N = state_vec.size();
		final String states[] = new String[N];
		// Silly loop because of type warnings from state_vec.toArray(states)
		for (int i=0; i<N; ++i) {
            states[i] = (String) state_vec.get(i);
        }
		return ValueFactory.createEnumeratedMetaData(states);
	}

	/** Parse the values from the received XML-RPC response. */
	@SuppressWarnings("unchecked")
    private IValue [] decodeValues(final int type, final int count, final IMetaData meta,
			                      final Vector value_vec) throws Exception
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
            final SeverityImpl sevr = server.getSeverity(sevr_code);
            final String stat = server.getStatus(sevr, stat_code);
			final Vector vv = (Vector)sample_hash.get("value");

			if (type == TYPE_DOUBLE)
			{
				final double values[] = new double[count];
				for (int vi=0; vi<count; ++vi) {
                    values[vi] = (Double)vv.get(vi);
                }
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
	                for (int vi=0; vi<count; ++vi) {
                        values[vi] = ((Integer)vv.get(vi));
                    }
	                samples[si] = ValueFactory.createLongValue(time, sevr, stat,
	                                (INumericMetaData)meta, quality, values);
				}
				else
				{
	                final int values[] = new int[count];
	                for (int vi=0; vi<count; ++vi) {
                        values[vi] = (Integer)vv.get(vi);
                    }
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
				for (int vi=0; vi<count; ++vi) {
                    values[vi] = ((Integer)vv.get(vi));
                }
                samples[si] = ValueFactory.createLongValue(time, sevr, stat,
                                (INumericMetaData)meta, quality, values);
			} else {
                throw new Exception("Unknown value type " + type);
            }
		}
		return samples;
	}

	/** @return Returns one <code>ArchivedSamples</code> per channel. */
	public ArchiveValues[] getArchivedSamples()
	{
		return archived_samples;
	}
}
