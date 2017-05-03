package org.csstudio.archive.reader.ea4;


//import org.apache.xmlrpc.XmlRpcClient;
//import org.apache.xmlrpc.XmlRpcException;
import org.csstudio.archive.reader.ArchiveInfo;

import org.epics.pvaccess.client.rpc.RPCClientImpl;

import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.StructureArrayData;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;

/** Handles the "archives" request and its results.
*  @author Nikolay Malitsky
*/
public class ArchivesRequest {
 
 private String commandName = "getArchives";
 
 //  {string command}
 private Structure requestType;
 
 // { infoType [] archives; }
 private Structure responseType;
 
 // {int key, string name, string path}
 private Structure infoType;
 
 private ArchiveInfo archive_infos[];
 
 public ArchivesRequest(){
     createRequestType();
     createResponseType();
 }

 /** Read info from data server */
 @SuppressWarnings({ "nls", "unchecked" })
 public void read(RPCClientImpl client, double REQUEST_TIMEOUT) 
         throws Exception {
         
     // send request

	PVStructure pvRequest = createRequest();
	PVStructure pvResult = client.request(pvRequest, REQUEST_TIMEOUT);
     
     // process result

	PVStructureArray infos = pvResult.getStructureArrayField("archives"); 
     
     StructureArrayData data = new StructureArrayData();       
     infos.get(0, infos.getLength(), data);
          
     archive_infos = new ArchiveInfo[data.data.length];
	for(int i=0; i < data.data.length; i++) {
         
	    PVStructure info = data.data[i];

	    int key     = info.getIntField("key").get();
	    String name = info.getStringField("name").get();
	    String path = info.getStringField("path").get();
        
         archive_infos[i] = new ArchiveInfo(name, path, key);     
	}
 }
     
 /** @return Returns all the archive infos obtained in the request. */
 public ArchiveInfo[] getArchiveInfos() {
	return archive_infos;
 }

 /** @return Returns a more or less useful string. */
 @SuppressWarnings("nls")
 @Override public String toString() {
     StringBuffer result = new StringBuffer();
     for (int i = 0; i < archive_infos.length; i++){
     	result.append(String.format("Key %4d: '%s' (%s)\n",
             archive_infos[i].getKey(),
             archive_infos[i].getName(),
             archive_infos[i].getDescription()));
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
 
 private void createInfoType(){
     
     FieldCreate fieldCreate = FieldFactory.getFieldCreate();
     
     String[] names = new String[3];
     Field[] fields = new Field[3];
     
     names[0] = "key";
     fields[0] = fieldCreate.createScalar(ScalarType.pvInt);
     
     names[1] = "name";
     fields[1] = fieldCreate.createScalar(ScalarType.pvString);
     
     names[2] = "path";
     fields[2] = fieldCreate.createScalar(ScalarType.pvString);
     
     infoType = fieldCreate.createStructure(names, fields);     
     
 }
 
 private void createResponseType(){
     
    createInfoType();
   
    FieldCreate fieldCreate = FieldFactory.getFieldCreate();
    
     String[] names = new String[1];
     Field[] fields = new Field[1];

     names[0] = "archives";
     fields[0] = fieldCreate.createStructureArray(infoType);
     
     responseType = fieldCreate.createStructure(names, fields);
 }
 
}

