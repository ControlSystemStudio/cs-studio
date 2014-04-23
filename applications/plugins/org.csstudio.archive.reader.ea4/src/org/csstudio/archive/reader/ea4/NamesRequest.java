package org.csstudio.archive.reader.ea4;


import org.epics.pvaccess.client.rpc.RPCClientImpl;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;
import org.epics.pvdata.pv.StructureArrayData;
import org.epics.util.time.Timestamp;

/** Handles the "archiver.names" request and its results. */
@SuppressWarnings("nls")
public class NamesRequest {
    
    private String commandName = "getChannels";
    
    //  {string command}
    private Structure requestType;
    
    // { infoType [] channels; }
    private Structure responseType;
    
    // {string name, int32 start_sec, int32 start_nano, 
    // int32 end_sec, int32 end_nano;}
    private Structure infoType;
    
    final private int key;
    final private String pattern;
      
    private String chNames[];
    private Timestamp starts[];
    private Timestamp ends[];

	
    /** Create a name lookup.
      * @param pattern Regular expression pattern for the name.
    */
    public NamesRequest(int key, String pattern) {
        createRequestType();
        this.key = key;
        this.pattern = pattern;
    }

    /** Read info from data server */
    @SuppressWarnings("unchecked")
    public void read(RPCClientImpl client, double REQUEST_TIMEOUT) 
                throws Exception {
            
        PVStructure pvRequest = createRequest();
        
        PVInt keyField = pvRequest.getIntField("key");
        keyField.put(key);
        PVString patternField = pvRequest.getStringField("pattern");
        patternField.put(pattern);
        
        PVStructure pvResult = client.request(pvRequest, REQUEST_TIMEOUT);       
        
        // process result

        PVStructureArray infos = pvResult.getStructureArrayField("channels"); 
        
        StructureArrayData data = new StructureArrayData();       
        infos.get(0, infos.getLength(), data);
        
        this.chNames  = new String[data.data.length];      
        this.starts   = new Timestamp[data.data.length];
        this.ends     = new Timestamp[data.data.length];
        
        for(int i=0; i < data.data.length; i++) {   
        	
            PVStructure info = data.data[i];
            
            chNames[i]    = info.getStringField("name").get();   
            
            int start_secs = info.getIntField("start_sec").get();  
            int start_nano = info.getIntField("start_nano").get();
            starts[i] = Timestamp.of(start_secs, start_nano);
 
            int end_secs  = info.getIntField("end_sec").get();  
            int end_nano  = info.getIntField("end_nano").get();  
            ends[i] = Timestamp.of(end_secs, end_nano);
        }   
    }

    /** @return Returns the name infos that were found. */
    public final String[] getNameInfos() {
        return chNames;
    }
    
    public final Timestamp[] getStarts() {
        return starts;
    }
    
    public final Timestamp[] getEnds() {
        return ends;
    }
	
    /** @return Returns a more or less useful string. */
    @Override public String toString() {
		
        StringBuffer result = new StringBuffer();
        result.append(String.format("Names with key %d matching '%s':\n",
                    key, pattern));
            
        for (int i=0; i < chNames.length; ++i) {
                if (i>0) result.append(", ");
		result.append('\'');
		result.append(chNames[i]);
		result.append('\'');
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
        
        String[] names = new String[3];
        Field[] fields = new Field[3];
        
        names[0] = "command";
        fields[0] = fieldCreate.createScalar(ScalarType.pvString);
        
        names[1] = "key";
        fields[1] = fieldCreate.createScalar(ScalarType.pvInt);   
        
        names[2] = "pattern";
        fields[2] = fieldCreate.createScalar(ScalarType.pvString);
        
        requestType = fieldCreate.createStructure(names, fields);     
    }
    
    private void createInfoType(){
        
        FieldCreate fieldCreate = FieldFactory.getFieldCreate();
        
        String[] names = new String[5];
        Field[] fields = new Field[5];
              
        names[0] = "name";
        fields[0] = fieldCreate.createScalar(ScalarType.pvString);
        
        names[1] = "start_sec";
        fields[1] = fieldCreate.createScalar(ScalarType.pvInt);
        
        names[2] = "start_nano";
        fields[2] = fieldCreate.createScalar(ScalarType.pvInt);
        
        names[3] = "end_sec";
        fields[3] = fieldCreate.createScalar(ScalarType.pvInt);
        
        names[4] = "end_nano";
        fields[4] = fieldCreate.createScalar(ScalarType.pvInt);
        
        infoType = fieldCreate.createStructure(names, fields);     
        
    }
    
    private void createResponseType(){
        
       createInfoType();
      
       FieldCreate fieldCreate = FieldFactory.getFieldCreate();
       
        String[] names = new String[1];
        Field[] fields = new Field[1];
 
        names[0] = "channels";
        fields[0] = fieldCreate.createStructureArray(infoType);
        
        responseType = fieldCreate.createStructure(names, fields);
    }
    
}

