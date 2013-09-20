package org.csstudio.archive.reader.ea4;

import org.csstudio.archive.reader.*;
import org.csstudio.archive.reader.ea4.EA4ArchiveReaderFactory;
import org.epics.pvaccess.client.rpc.RPCClientImpl;
import org.epics.pvaccess.server.rpc.RPCRequestException;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.StructureArrayData;

public class ArchiveClient {

    /*
    static private  RPCClientImpl client;

    private final static double REQUEST_TIMEOUT = 3.0;

    static public void getArchives() throws RPCRequestException {
        
        ArchiveCommandRegistry commands = ArchiveCommandRegistry.getInstance();
	ArchiveCommand command = commands.getCommand("getArchives");
        
        // send request

	PVStructure pvRequest = command.createRequest();
	PVStructure pvResult = client.request(pvRequest, REQUEST_TIMEOUT);
        
        // process result

	PVStructureArray infos = pvResult.getStructureArrayField("archives"); 
        
        StructureArrayData data = new StructureArrayData();       
        infos.get(0, infos.getLength(), data);
             
	for(int i=0; i < data.data.length; i++) {
            
	    PVStructure info = data.data[i];

	    int key     = info.getIntField("key").get();
	    String name = info.getStringField("name").get();
	    String path = info.getStringField("path").get();

	    System.out.println("key: " + key + ", name: " + name + ", path: " + path);
      
	}

    }
    */
	
    public static void main(String[] args) throws Throwable {
        
        ArchiveReaderFactory readerFactory = 
                new EA4ArchiveReaderFactory();
           
        ArchiveReader reader = readerFactory.getArchiveReader("");
        
        System.out.println("server name: " + reader.getServerName());
        System.out.println("url: " + reader.getURL());
        System.out.println("description: " + reader.getDescription());
        System.out.println("version: " + reader.getVersion());
        
        System.out.println("Archive Infos: ");
        ArchiveInfo[] aInfos = reader.getArchiveInfos();
        for(int i=0; i < aInfos.length; i++){
            System.out.println(i + 
                    ", key: " +  aInfos[i].getKey() + 
                    ", name: " + aInfos[i].getName() + 
                    ", path: " + aInfos[i].getDescription());
        }
        
        String[] chNames = reader.getNamesByRegExp(1, ".*");
        
        for (String req : chNames){
            System.out.println(req);
        }
           
        reader.close();
    }
}

