package org.csstudio.service.scanserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.util.array.ArrayDouble;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;

import edu.msu.frib.scanserver.api.Data;
import edu.msu.frib.scanserver.api.ScanServer;
import edu.msu.frib.scanserver.api.ScanServerClient;
import edu.msu.frib.scanserver.api.ScanServerClientImpl.SSCBuilder;

public class DataMethod extends ServiceMethod {

	public DataMethod() {
		super(new ServiceMethodDescription("data", "Current data from scan")
		    .addArgument("server", "scan server (optional)", VString.class)
			.addArgument("id", "scan id", VNumber.class)
			.addResult("result", "data", VTable.class));
	    }

	@Override
	public void executeMethod(Map<String, Object> parameters,
			WriteFunction<Map<String, Object>> callback,
			WriteFunction<Exception> errorCallback) {


		ScanServerClient ssc = null;
		if (((VString) parameters.get("server")) != null){
			String server = ((VString) parameters.get("server")).getValue();
			ssc = SSCBuilder.serviceURL(server)
					.create();
		} else {
			ssc = ScanServer.getClient();
		}
        Number id = ((VNumber) parameters.get("id")).getValue();
        Long cleanInt = Long.valueOf(id.intValue());
        
        
        Data data = ssc.getScanData(cleanInt);
        List<Timestamp> timeList = (List<Timestamp>)data.getValues().get(0);
        // Gabriele is adding timestamp, this is temp
        double[] doubleArrayTime = new double[timeList.size()];
        int index1=0;
        for(Timestamp time: timeList){
        	doubleArrayTime[index1++] = Long.valueOf(time.getSec()).doubleValue()+0.1*time.getNanoSec();
        }
        data.getValues().set(0, new ArrayDouble(doubleArrayTime));
        
        List<Class<?>> types= data.getTypes();
        types.set(0, double.class);
        //TODO: check for types (scan can have a string or float)
        for (int i = 1; i< data.getNames().size(); i++){
        	types.set(i,double.class);
        	List<Float> listFloat = (List<Float>)data.getValues().get(i);
        	double[] doubleArray = new double[listFloat.size()];
        	int index = 0;
        	for(Float number: listFloat){
        	  doubleArray[index++] = number.doubleValue();
        	}
        	
        	data.getValues().set(i, new ArrayDouble(doubleArray));
        }       
        
        

		 Map<String, Object> resultMap = new HashMap<>();
		    resultMap.put("result",
			    ValueFactory.newVTable(types, data.getNames(), data.getValues()));
		    resultMap.put("result_size", timeList.size());
		    callback.writeValue(resultMap);	
	}

}
