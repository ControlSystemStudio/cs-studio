package org.csstudio.service.scanserver;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.ValueFactory;

import edu.msu.frib.scanserver.api.ScanServer;
import edu.msu.frib.scanserver.api.ScanServerClient;
import edu.msu.frib.scanserver.api.ScanServerClientImpl.SSCBuilder;
import edu.msu.frib.scanserver.api.commands.CommandSet;
import edu.msu.frib.scanserver.api.commands.DelayCommand;
import edu.msu.frib.scanserver.api.commands.LogCommand;
import edu.msu.frib.scanserver.api.commands.LoopCommand;


public class Scan2DMethod extends ServiceMethod{

	public Scan2DMethod() {
		super(new ServiceMethodDescription("scan2d", "Queues a 2D scan to the scan server")
			.addArgument("server", "scan server (optional)", VString.class)
			.addArgument("positioner", "Positioner PV", VString.class)
			.addArgument("detector", "Detector PV", VString.class)
			.addArgument("start", "Start location", VNumber.class)
			.addArgument("end", "End location", VNumber.class)
			.addArgument("step", "Step size", VNumber.class)
			.addArgument("delay", "delay before logging (optional)", VNumber.class)
			.addResult("result", "id of queued scan", VNumber.class));
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
		
        String positioner = ((VString) parameters.get("positioner")).getValue();
        String detector = ((VString) parameters.get("detector")).getValue();
        Number start = ((VNumber) parameters.get("start")).getValue();
        Number end = ((VNumber) parameters.get("end")).getValue();
        Number step = ((VNumber) parameters.get("step")).getValue();

        
        List<String> devices = new ArrayList<String>();
        devices.add(detector);
        devices.add(positioner);
        LogCommand log = LogCommand.builder().devices(devices).build();
        LoopCommand.Builder<?> loopBuilder = LoopCommand.builder().device(positioner)
        		.start((Double) start)
        		.end((Double) end)
        		.step((Double) step);
        if (((VNumber) parameters.get("delay")) != null){
            Number delayTime = ((VNumber) parameters.get("delay")).getValue();
        	DelayCommand delay = DelayCommand.builder().seconds(delayTime.doubleValue()).build();
        	loopBuilder.add(delay);
        }
		LoopCommand loop = loopBuilder.add(log).build();
        CommandSet commandSet = CommandSet.builder().add(loop).build();
        Long scanId = ssc.queueScan(positioner+" "+detector+" 2D Scan",commandSet);
        
        Map<String, Object> resultMap = new HashMap<>();
	    resultMap.put("result",ValueFactory.newVInt(scanId.intValue(), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));
	    callback.writeValue(resultMap);
		
	}

}
