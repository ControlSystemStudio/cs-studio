package org.csstudio.service.scanserver;

import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;

public class QueueMethod extends ServiceMethod{

	public QueueMethod() {
		super(new ServiceMethodDescription("queue", "Queues a scan to the scan server")
			.addArgument("file", "File location", VString.class)
			.addResult("result", "id of queued scan", VNumber.class));
	    }

	@Override
	public void executeMethod(Map<String, Object> parameters,
			WriteFunction<Map<String, Object>> callback,
			WriteFunction<Exception> errorCallback) {
		// TODO Auto-generated method stub
		
	}

}
