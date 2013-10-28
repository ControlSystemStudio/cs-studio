package org.csstudio.archive.reader.ea4;

import org.csstudio.archive.reader.*;
import org.csstudio.archive.reader.ea4.EA4ArchiveReaderFactory;
import org.epics.pvaccess.client.rpc.RPCClientImpl;
import org.epics.pvaccess.server.rpc.RPCRequestException;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.StructureArrayData;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;
import org.epics.vtype.VTypeToString;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.ArchiveVString;

public class ArchiveClient {

 
    public static void main(String[] args) throws Throwable {
        
        ArchiveReaderFactory readerFactory = 
                new EA4ArchiveReaderFactory();
           
        EA4ArchiveReader reader = (EA4ArchiveReader) readerFactory.getArchiveReader("");
        
        System.out.println("ServerInfoReuest: ");
        System.out.println("server name: " + reader.getServerName());
        System.out.println("url: " + reader.getURL());
        System.out.println("description: " + reader.getDescription());
        System.out.println("version: " + reader.getVersion());
        
        System.out.println("ArchivesReuest: ");
        ArchiveInfo[] aInfos = reader.getArchiveInfos();
        for(int i=0; i < aInfos.length; i++){
            System.out.println(i + 
                    ", key: " +  aInfos[i].getKey() + 
                    ", name: " + aInfos[i].getName() + 
                    ", path: " + aInfos[i].getDescription());
        }
        
        int key           = 1;
        boolean optimized = true;
        int secs          = 10;
        
        System.out.println("NamesRequest: ");
       
        NamesRequest nameRequest = new NamesRequest(key, ".*");
        nameRequest.read(reader.client, reader.REQUEST_TIMEOUT);
        
        String[] chNames   = nameRequest.getNameInfos();
         
        Timestamp[] starts = nameRequest.getStarts();
        Timestamp[] ends   = nameRequest.getEnds();
        
        System.out.println("ValuesRequest: ");
        
        int start_delta = -10;
        int end_delta   =  10;
        
        
        for (int i = 0; i < chNames.length; i++){
        	
        	Timestamp start = Timestamp.of(starts[i].getSec() + start_delta, starts[i].getNanoSec());
          	Timestamp end = Timestamp.of(ends[i].getSec() + end_delta, ends[i].getNanoSec());
          	
          	int count = (int) (end.durationFrom(start).toSeconds() / secs);
 
        	VType[] values = reader.getSamples(key, chNames[i], start, end, optimized, count);
        
        
        	System.out.println(chNames[i] + ", length: " + values.length);
        	System.out.println("start: " + starts[i].toString());
        	System.out.println("end: " + ends[i].toString());        	
        
        	if(values.length > 0) {
        		if(values[0] instanceof ArchiveVNumber){
        			System.out.println("ArchiveVNumber: " + 
        					((ArchiveVNumber) values[0]).toString());
        		} else if(values[0] instanceof ArchiveVNumberArray){
        			System.out.println("ArchiveVNumberArray: " + 
        					((ArchiveVNumberArray) values[0]).toString());
        		} else if(values[0] instanceof ArchiveVEnum){
        			System.out.println("ArchiveVEnum" + 
        					((ArchiveVEnum) values[0]).toString());
        		} else if(values[0] instanceof ArchiveVStatistics){
        			System.out.println("ArchiveVStatistics: " +
        					((ArchiveVStatistics) values[0]).toString());
        		} else if(values[0] instanceof ArchiveVString){
          			System.out.println("ArchiveVString: " +
        					((ArchiveVString) values[0]).toString());
        		} else {
        			System.out.println("Unknown Type");
        		}
        	}
        }
           
        reader.close();
    }
}

