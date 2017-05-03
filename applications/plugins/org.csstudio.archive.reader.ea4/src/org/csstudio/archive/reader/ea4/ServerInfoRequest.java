
package org.csstudio.archive.reader.ea4;

import java.util.Map;
import java.util.HashMap;

import org.epics.pvaccess.client.rpc.RPCClientImpl;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.Structure;
import org.epics.pvdata.pv.StructureArrayData;
import org.epics.vtype.AlarmSeverity;

/** Handles the "archiver.info" request and its results.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
final class ServerInfoRequest {
          
    private String commandName = "getInfo";
    
    //  {string command}
    private Structure requestType;
    
    /** String used for an OK status and severity
     *  (more generic than the EPICS 'NO_ALARM')
     */
    final static String NO_ALARM = "OK";
    
    private String description;	
    private int version;
    private String how_strings[];
    private String status_strings[];
    private Map<Integer, SeverityImpl> severities;
    
    public ServerInfoRequest(){
        createRequestType();
        severities = new HashMap<Integer, SeverityImpl>();
    }

    /** Read info from data server */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void read(RPCClientImpl client, double REQUEST_TIMEOUT) 
            throws Exception {
        
        // send request

	PVStructure pvRequest = createRequest();
	PVStructure pvResult  = client.request(pvRequest, REQUEST_TIMEOUT);
        
        // process result
        //	{ int32             ver,
		//	  string            desc,
		//	  string            how[],
		//	  string            stat[],
		//	  { int32 num,
		//	    string sevr,
		//	    bool has_value,
		//	    bool txt_stat
		//	  }                 sevr[]
		//	} = archiver.info()

		version     = pvResult.getIntField("ver").get(); 
        description = pvResult.getStringField("desc").get();
        
        // Get 'how'. Silly code to copy that into a type-safe vector.
        setHows(pvResult);
       
        // Same silly code for the status strings. Better way?
        setStat(pvResult);

        // Same silly code for the severity strings.
        setSevr(pvResult);
    }

    /** @return Returns the version number. */
    public int getVersion() {
    	return version;
    }

    /** @return Returns the description. */
    public String getDescription() {
    	return description;
    }

    /** @return Returns the list of supported request types. */
    public String[] getRequestTypes() {
        return how_strings;
    }

    /** @return Returns the status strings. */
    public String[] getStatusStrings() {
        return status_strings;
    }
    
        /** @return Returns the severity infos. */
    public Map<Integer, SeverityImpl> getSeverities(){
        return severities;
    }

    /** @return Returns the severity infos. */
    public SeverityImpl getSeverity(int severity){
        final SeverityImpl sev = severities.get(Integer.valueOf(severity));
        if (sev != null)
            return sev;
        return new SeverityImpl(AlarmSeverity.UNDEFINED,
                        "<Severity " + severity + "?>",
                        false, false);
    }

    /** @return Returns a more or less useful string for debugging. */
    @Override public String toString() {
    	final StringBuffer result = new StringBuffer();
    	result.append(String.format("Server version : %d\n", version));
    	result.append(String.format("Description    :\n%s", description));
    	result.append("Available request methods:\n");
    	for (int i=0; i<how_strings.length; ++i){
    		result.append(String.format("%d = '%s'\n", i, how_strings[i]));
        }
    	return result.toString();
    }
    
    public String getName() {
        return commandName;
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
        
        String[] names = new String[1];
        Field[] fields = new Field[1];
        
        names[0] = "command";
        fields[0] = fieldCreate.createScalar(ScalarType.pvString);
        
        requestType = fieldCreate.createStructure(names, fields);     
    }
    
    private void setHows(PVStructure pvResult){
        	
        PVStringArray howsArray =  (PVStringArray) 
             pvResult.getScalarArrayField("how", ScalarType.pvString);
        
        int length = howsArray.getLength();
        
        StringArrayData howsData = new StringArrayData();
        howsArray.get(0, length, howsData);
        
        how_strings = new String[length];
        for (int i=0; i < length; ++i){
            how_strings[i] = howsData.data[i];
        }
        
    }
    
    private void setStat(PVStructure pvResult){
        	
        PVStringArray statArray =  (PVStringArray) 
             pvResult.getScalarArrayField("stat", ScalarType.pvString);
        
        int length = statArray.getLength();
        
        StringArrayData statData = new StringArrayData();
        statArray.get(0, length, statData);
        
        status_strings = new String[length];
        for (int i=0; i < length; ++i){            
            status_strings[i] = statData.data[i];
            // Patch "NO ALARM" into "OK"
            if (status_strings[i].equals("NO_ALARM"))
                status_strings[i] = NO_ALARM;
        }
        
    }
    
    private void setSevr(PVStructure pvResult){
        
       PVStructureArray sevrs = pvResult.getStructureArrayField("sevr"); 
        
        StructureArrayData data = new StructureArrayData();       
        sevrs.get(0, sevrs.getLength(), data);
                     
        for(int i = 0; i < sevrs.getLength(); i++){
            
            PVStructure info = data.data[i];

            int num        = info.getIntField("num").get();
            String  sevr   = info.getStringField("sevr").get();
            int int_has_value  = info.getIntField("has_value").get();
            int int_txt_stat   = info.getIntField("txt_stat").get();
            
            boolean has_value = false;
            if(int_has_value > 0) has_value = true;
            
            boolean txt_stat = false;
            if(int_txt_stat > 0) txt_stat = true;           
                      
            // Patch "NO ALARM" into "OK"
            AlarmSeverity severity;
            
            if ("NO_ALARM".equals(sevr)  ||  NO_ALARM.equals(sevr))
            	severity = AlarmSeverity.NONE;
            else if ("MINOR".equals(sevr))
            	severity = AlarmSeverity.MINOR;
            else if ("MAJOR".equals(sevr))
            	severity = AlarmSeverity.MAJOR;
            else if ("MAJOR".equals(sevr))
            	severity = AlarmSeverity.INVALID;
            else
            	severity = AlarmSeverity.UNDEFINED;
            
            severities.put(num, 
                    new SeverityImpl(severity, sevr, has_value, txt_stat));
            
        }
    }
    
}

