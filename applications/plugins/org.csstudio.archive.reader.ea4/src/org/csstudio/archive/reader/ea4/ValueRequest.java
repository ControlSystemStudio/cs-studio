package org.csstudio.archive.reader.ea4;

import java.util.logging.Logger;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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

import org.epics.pvaccess.client.rpc.RPCClient;
import org.epics.pvaccess.client.rpc.RPCClientImpl;
import org.epics.pvaccess.client.rpc.RPCClientRequester;
import org.epics.pvaccess.server.rpc.RPCRequestException;

import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;

import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVShort;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.ShortArrayData;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.PVFloat;
import org.epics.pvdata.pv.PVFloatArray;
import org.epics.pvdata.pv.FloatArrayData;
import org.epics.pvdata.pv.PVDouble;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.DoubleArrayData;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.StructureArrayData;
import org.epics.pvdata.pv.Status;

import org.epics.pvdata.pv.Structure;

/** Handles the "archiver.values" request and its results.
 *  @author Kay Kasemir
 */

@SuppressWarnings("nls")
public class ValueRequest {
	
	private String commandName = "getValues";
	
    private Structure requestType;

	final private EA4ArchiveReader reader;
	
	final private int key;
	final private String channels[];
	final private Timestamp start, end;
	final private int icount;
	final private int how;

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
	public ValueRequest(final EA4ArchiveReader reader,
			final int key, final String channel,
			final Timestamp start, final Timestamp end, 
			final boolean optimized, final int count)
	        throws Exception {
		
		createRequestType();
		
        this.reader = reader;
        this.key = key;
        this.channels = new String[] { channel };
        this.start = start;
        this.end = end;

        if (optimized) {
            if (reader.getVersion() < 1) {   
            	// Old server: Use plot-binning with bin count
                this.how = reader.getRequestCode("plot-binning");
                this.icount = count;
            } else {             	
            	// New server: Use min/max/average with seconds
                int secs = (int) (end.durationFrom(start).toSeconds() / count);
                if (secs < 1) secs = 1;
                this.how = reader.getRequestCode("average");
                this.icount = (int)secs ;
            }
        } else {   
        	// All others use 'Integer count'
            // Raw == Original, all else is somehow interpolated
            this.how    = reader.getRequestCode("raw");
            this.icount = count;
        }
	}

	/** @see org.csstudio.archive.channelarchiver.ClientRequest#read() */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void read(RPCClientImpl client, double REQUEST_TIMEOUT) 
                throws Exception {
            
        PVStructure pvRequest = createRequest();
        
        PVInt keyField = pvRequest.getIntField("key");
        keyField.put(key);
              
        PVStringArray nameArray =  (PVStringArray) 
                pvRequest.getScalarArrayField("name", ScalarType.pvString);       
        nameArray.put(0, channels.length, channels, 0);
           
        PVInt start_secField = pvRequest.getIntField("start_sec");
        start_secField.put((int) start.getSec());
        
        PVInt start_nanoField = pvRequest.getIntField("start_nano");
        start_nanoField.put((int) start.getNanoSec());   
        
        PVInt end_secField = pvRequest.getIntField("end_sec");
        end_secField.put((int) end.getSec());
        
        PVInt end_nanoField = pvRequest.getIntField("end_nano");
        end_nanoField.put((int) end.getNanoSec());    
        
        PVInt countField = pvRequest.getIntField("count");
        countField.put(icount);        
        
        PVInt howField = pvRequest.getIntField("how");
        howField.put(how);        
        
        PVStructure pvResults = client.request(pvRequest, REQUEST_TIMEOUT);       

		// result := { string name,  meta, int32 type,
        //              int32 count,  values }[]
        
        PVInt sizeField = pvResults.getIntField("size");
         
		final int num_returned_channels = sizeField.get();
		
		if (num_returned_channels != 1)
            throw new Exception("archiver.values returned data for " + 
            		num_returned_channels + " channels?");
		
	    PVStructure pvResult = pvResults.getStructureField("0");
	     
        final String name = pvResult.getStringField("name").get();
        
        final int pv_type = pvResult.getIntField("type").get();
        final ScalarType type  = ScalarType.values()[pv_type];
        final int pv_count   = pvResult.getIntField("count").get();
        
        // System.out.println("scalar type: " + pv_type + ", count: " + pv_count);       
		
	    PVStructure pvMeta   = pvResult.getStructureField("meta");
	    PVStructureArray pvValues = pvResult.getStructureArrayField("values");
		
		try {
			
			final Object meta = decodeMetaData(type, pvMeta);
			
			final Display display;
			final List<String> labels;
			
			if (meta instanceof Display) {
				display = (Display) meta;
				labels = null;
			} else if (meta instanceof List) {
				display = null;
				labels = (List) meta;
			} else {
				display = null;
				labels = null;
			}
			
			samples = decodeValues(pv_type, pv_count, display, labels, pvValues);
			
		} catch (Exception e) {
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
	public void cancel() {
    }


    /** Parse the MetaData from the received XML-RPC response.
	 *  @param value_type Type code of received values
	 *  @param meta_hash Hash with meta data to decode
	 *  @return {@link Display} or List of {@link String}[] depending on data type
	 */
	@SuppressWarnings({ "rawtypes" })
    private Object decodeMetaData(final ScalarType value_type, 
    		PVStructure pvMeta) throws Exception {
		
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
		
		final int meta_type = pvMeta.getIntField("type").get();
		
		if (meta_type < 0 || meta_type > 1)
			throw new Exception("Invalid 'meta' type " + meta_type);
		
		if (meta_type == 1) {
			
            // The 2.8.1 server will give 'ENUM' type values
            // with Numeric meta data, units = "<No data>"
            // as an error message.
			
        	final NumberFormat format = 
        			NumberFormats.format((Integer) pvMeta.getIntField("prec").get());
        	
			return ValueFactory.newDisplay(
					(Double) pvMeta.getDoubleField("disp_low").get(),
					(Double) pvMeta.getDoubleField("alarm_low").get(),
					(Double) pvMeta.getDoubleField("warn_low").get(),
					(String) pvMeta.getStringField("units").get(),
					format,
					(Double) pvMeta.getDoubleField("warn_high").get(),
					(Double) pvMeta.getDoubleField("alarm_high").get(),
					(Double) pvMeta.getDoubleField("disp_high").get(),
					(Double) pvMeta.getDoubleField("disp_low").get(),
					(Double) pvMeta.getDoubleField("disp_high").get());
		}
		
        //  else
		/*
		if (! (value_type == ScalarType.pvString)){
			throw new Exception(
					"Received enumerated meta information for value type "
					+ value_type);
		}
		*/
		
	    PVStringArray statesArray =  (PVStringArray) 
	                pvMeta.getScalarArrayField("states", ScalarType.pvString);  
	    
	    StringArrayData statesData = new StringArrayData();
	    statesArray.get(0, statesArray.getLength(), statesData);
		
		final int N = statesData.data.length;		
		final List<String> states = new ArrayList<String>(N);
		
		// Silly loop because of type warnings from state_vec.toArray(states)
		for (int i=0; i < N; ++i){
			states.add((String) statesData.data[i]);
		}
		
		return states;
	}

	/** Parse the values from the received XML-RPC response. */
	@SuppressWarnings({ "rawtypes" })
    private VType[] decodeValues(final int type, 
    			final int count, 
    			final Display display,
    		    final List<String> labels, 
    		    final PVStructureArray pvValues) throws Exception {
		
        // values := { int32 stat,  int32 sevr,
	    //             int32 secs,  int32 nano,
	    //             <type> value[] } []
        // [{secs=1137596340, stat=0, nano=344419666, value=[0.79351], sevr=0},
	    //  {secs=1137596400, stat=0, nano=330619666, value=[0.79343], sevr=0},..]
				
		final int num_samples = pvValues.getLength();
		
		StructureArrayData valuesData = new StructureArrayData();
		pvValues.get(0, num_samples, valuesData);
		
		final VType samples[] = new VType[num_samples];
		
		for (int si=0; si < num_samples; ++si) {
						
			final PVStructure pvValue = valuesData.data[si];
			
			final long secs = pvValue.getIntField("secs").get();
			final int nano  = pvValue.getIntField("nano").get();
			final Timestamp time = Timestamp.of(secs, nano);
			
			final int stat_code = pvValue.getIntField("stat").get();
			final int sevr_code = pvValue.getIntField("sevr").get();
            final SeverityImpl sevr = reader.getSeverity(sevr_code);
            final String stat = reader.getStatus(sevr, stat_code);
			final AlarmSeverity severity = sevr.getSeverity();
			          			
			switch(type){
			
			case 3: // pvInt
			{				
				final PVIntArray vvArray = (PVIntArray)
						pvValue.getScalarArrayField("value", ScalarType.pvInt);	
				
				IntArrayData vvData = new IntArrayData();
				vvArray.get(0,  vvArray.getLength(), vvData);
				
				// System.out.println(si + ", " + vvData.data[0]);
				
				if (count == 1) {
					final int value = vvData.data[0];
					samples[si] = new ArchiveVNumber(time, 
													 severity, stat, 
													 display, 
													 value);
				}
				else {
					final int values[] = new int[count];
					for (int vi=0; vi < count; ++vi) values[vi] = vvData.data[vi];
					samples[si] = new ArchiveVNumberArray(time, 
								                          severity, stat, 
								                          display, 
								                          values);
				}
			}
			break;
			
	
			
			case 10: //  pvDouble
			{		
					final PVDoubleArray vvArray = (PVDoubleArray)
						pvValue.getScalarArrayField("value", ScalarType.pvDouble);
				
					DoubleArrayData vvData = new DoubleArrayData();
					vvArray.get(0,  vvArray.getLength(), vvData);
				
					final double values[] = new double[count];				
					for (int vi=0; vi < count; ++vi) {
						values[vi] = vvData.data[vi];
					} 
					
					// System.out.println(si + ", " + vvData.data[0]);
					
					Structure valueType = pvValue.getStructure();
				
					final int minIndex = valueType.getFieldIndex("min");
					final int maxIndex = valueType.getFieldIndex("max");
					
					// System.out.println("min: " + minIndex + ", max: " + maxIndex);
				
					// Check for "min", "max".
					// Only handles min/max for double, but that's OK
					// since for now that's all that the server does as well.
				
					if (minIndex != -1 && maxIndex != -1) {   
						// It's a min/max double, certainly interpolated
						final double min = pvValue.getDoubleField("min").get();
						final double max = pvValue.getDoubleField("max").get();
						samples[si] = new ArchiveVStatistics(time, 
															severity, stat, 
															display,
															values[0], 
															min, max, 
															0.0, 1);
					} else {   
						// Was this from a min/max/avg request?
						// Yes: Then we ran into a raw value.
						// No: Then it's whatever quality we expected in general
						if (values.length == 1){
							samples[si] = new ArchiveVNumber(time, 
															severity, stat, 
															display, 
															values[0]);
						} else {
							samples[si] = new ArchiveVNumberArray(time, 
																  severity, stat, 
																  display, 
																  values);
						}
					}
			}
			break;	
				
			case 11: //  pvString
			{
				final PVStringArray vvArray = (PVStringArray)
					pvValue.getScalarArrayField("value", ScalarType.pvString);	
					
				StringArrayData vvData = new StringArrayData();
				vvArray.get(0,  vvArray.getLength(), vvData);	
					
				final String value = vvData.data[0];
				samples[si] = new ArchiveVString(time, severity, stat, value);
			}
			break;
			
			default:
				throw new Exception("Unknown value type " + type);
			}
		}
			
		return samples;
	}

	/** @return Samples */
	public VType[] getSamples() {
		return samples;
	}
	
    public PVStructure createRequest(){
        
        PVDataCreate dataCreate = PVDataFactory.getPVDataCreate();
        
        PVStructure request = dataCreate.createPVStructure(requestType);
        
        PVString commandField = request.getStringField("command");
        commandField.put(commandName);
        
        return request;     
    }
      
    // private methods
    
    private void createRequestType(){
        
        FieldCreate fieldCreate = FieldFactory.getFieldCreate();
        
        String[] names = new String[9];
        Field[] fields = new Field[9];
        
        names[0] = "command";
        fields[0] = fieldCreate.createScalar(ScalarType.pvString);
        
        names[1] = "key";
        fields[1] = fieldCreate.createScalar(ScalarType.pvInt);   
        
        names[2] = "name";
        fields[2] = fieldCreate.createScalarArray(ScalarType.pvString);
        
        names[3] = "start_sec";
        fields[3] = fieldCreate.createScalar(ScalarType.pvInt);   
        
        names[4] = "start_nano";
        fields[4] = fieldCreate.createScalar(ScalarType.pvInt);  
        
        names[5] = "end_sec";
        fields[5] = fieldCreate.createScalar(ScalarType.pvInt);   
        
        names[6] = "end_nano";
        fields[6] = fieldCreate.createScalar(ScalarType.pvInt);  
        
        names[7] = "count";
        fields[7] = fieldCreate.createScalar(ScalarType.pvInt);   
        
        names[8] = "how";
        fields[8] = fieldCreate.createScalar(ScalarType.pvInt);         
        
        requestType = fieldCreate.createStructure(names, fields);     
    }
    
}
