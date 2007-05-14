package org.csstudio.archive.channelarchiver;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;

/** Handles the "archiver.values" request and its results.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValuesRequest
{
	private final ArchiveServer server;
	private final int key;
	private final String channels[];
	private final ITimestamp start, end;
	private final int how;
    private final Object parms[];
	
	// Possible 'type' IDs for the received values.
	private static final int TYPE_STRING = 0;
	private static final int TYPE_ENUM = 1;
	private static final int TYPE_INT = 2;
	private static final int TYPE_DOUBLE = 3;
	
	// The result of the query
	private ArchiveValues archived_samples[];
	
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
	 */
	public ValuesRequest(ArchiveServer server,
			int key, String channels[],
			ITimestamp start, ITimestamp end, int how, Object parms[])
	        throws Exception
	{
        final String req_type = server.getRequestTypes()[how];
        // Check parms
        if (req_type.equals(ArchiveServer.GET_AVERAGE))
        {
            if (! (parms.length == 1  &&  parms[0] instanceof Double))
                throw new Exception("Expected 'Double delta' for GET_AVERAGE");
            // We got the Double as per javadoc for the request type,
            // but the server actually only handles int...
            double secs = ((Double)parms[0]).doubleValue();
            parms = new Object[] { new Integer((int)secs) };
        }
        else // All others (for now) use 'Integer count'
            if (! (parms.length == 1  &&  parms[0] instanceof Integer))
                throw new Exception("Expected 'Integer count' for " + req_type);
		this.server = server;
		this.key = key;
		this.channels = channels;
		this.start = start;
		this.end = end;
		this.how = how;
        this.parms = parms;
	}

	/** @see org.csstudio.archive.channelarchiver.ClientRequest#read() */
    public void read(XmlRpcClient xmlrpc) throws Exception
	{
		Vector result;
		try
		{
			Vector<Object> params = new Vector<Object>(8);
			params.add(new Integer(key));
			params.add(channels);
			params.add(new Integer((int)start.seconds()));
			params.add(new Integer((int)start.nanoseconds()));
			params.add(new Integer((int)end.seconds()));
			params.add(new Integer((int)end.nanoseconds()));
            params.add(parms[0]);
			params.add(new Integer(how));
			result = (Vector)xmlrpc.execute("archiver.values", params);
		}
		catch (XmlRpcException e)
		{
			throw new Exception("archiver.values call failed", e);
		}
		// result := { string name,  meta, int32 type,
        //              int32 count,  values }[]
		int num_returned_channels = result.size();
		archived_samples = new ArchiveValues[num_returned_channels];
		for (int channel_idx=0; channel_idx<num_returned_channels; ++channel_idx)
		{
			Hashtable channel_data = (Hashtable) result.get(channel_idx);
			String name = (String)channel_data.get("name");
			int type = (Integer)channel_data.get("type");
			int count = (Integer)channel_data.get("count");
			IMetaData meta;
			IValue samples[];
			try
			{
				meta = decodeMetaData(type, (Hashtable)channel_data.get("meta"));
				samples = decodeValues(type, count, meta,
						(Vector)channel_data.get("values"));
			}
			catch (Exception e)
			{
				throw new Exception("Error while decoding values for channel '"
						+ name + "': " + e.getMessage(), e);
			}
			archived_samples[channel_idx] =
				new ArchiveValues(server, name, samples);
		}
	}

	/** Parse the MetaData from the received XML-RPC response. */
	private IMetaData decodeMetaData(int value_type, Hashtable meta_hash)
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
		int meta_type = (Integer) meta_hash.get("type");
		if (meta_type < 0 || meta_type > 1)
			throw new Exception("Invalid 'meta' type " + meta_type);
		if (meta_type == 1)
		{
            // The 2.8.1 server will give 'ENUM' type values
            // with Numeric meta data, units = "<No data>"
            // as an error message.
			if (value_type == TYPE_ENUM)
				throw new Exception(
						"Received numeric meta information for value type "
						+ value_type);
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
		Vector state_vec = (Vector) meta_hash.get("states");
		int N = state_vec.size();
		String states[] = new String[N];
		// Silly loop because of type warnings from state_vec.toArray(states)
		for (int i=0; i<N; ++i)
			states[i] = (String) state_vec.get(i);
		return ValueFactory.createEnumeratedMetaData(states);
	}

	/** Parse the values from the received XML-RPC response. */
	private IValue [] decodeValues(int type, int count, IMetaData meta,
			                      Vector value_vec) throws Exception
	{
        // TODO: Make server and protocol provide quality
        IValue.Quality quality = IValue.Quality.Original;
        
		// values := { int32 stat,  int32 sevr,
	    //             int32 secs,  int32 nano,
	    //             <type> value[] } []
		// [{secs=1137596340, stat=0, nano=344419666, value=[0.79351], sevr=0},
		//  {secs=1137596400, stat=0, nano=330619666, value=[0.79343], sevr=0},..]
		int num_samples = value_vec.size();
		IValue samples[] = new IValue[num_samples];
		for (int si=0; si<num_samples; ++si)
		{
			Hashtable sample_hash = (Hashtable) value_vec.get(si);
			long secs = (Integer)sample_hash.get("secs");
			long nano = (Integer)sample_hash.get("nano");
			ITimestamp time = TimestampFactory.createTimestamp(secs, nano);
			int stat_code = (Integer)sample_hash.get("stat");
			int sevr_code = (Integer)sample_hash.get("sevr");
			Vector vv = (Vector)sample_hash.get("value");
            SeverityImpl sevr = server.getSeverity(sevr_code);
            String stat = server.getStatus(sevr, stat_code);
			if (type == TYPE_DOUBLE)
			{
				double values[] = new double[count];
				for (int vi=0; vi<count; ++vi)
					values[vi] = (Double)vv.get(vi);
				samples[si] = ValueFactory.createDoubleValue(time, sevr, stat,
                                (INumericMetaData)meta, quality, values);
			}
			else if (type == TYPE_ENUM)
			{
				int values[] = new int[count];
				for (int vi=0; vi<count; ++vi)
					values[vi] = (Integer)vv.get(vi);
                samples[si] = ValueFactory.createEnumeratedValue(time, sevr, stat,
                                (IEnumeratedMetaData)meta, quality, values);
			}
			else if (type == TYPE_STRING)
			{
				String value = (String)vv.get(0);
                samples[si] = ValueFactory.createStringValue(time, sevr, stat,
                                quality, value);
			}
			else if (type == TYPE_INT)
			{
				int values[] = new int[count];
				for (int vi=0; vi<count; ++vi)
					values[vi] = (Integer)vv.get(vi);
                samples[si] = ValueFactory.createIntegerValue(time, sevr, stat,
                                (INumericMetaData)meta, quality, values);
			}
			else 
				throw new Exception("Unknown value type " + type);
		}
		return samples;
	}

	/** @return Returns one <code>ArchivedSamples</code> per channel. */
	public ArchiveValues[] getArchivedSamples()
	{
		return archived_samples;
	}	
}
