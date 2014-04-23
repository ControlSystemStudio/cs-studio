package org.csstudio.service.scanserver;

import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VTable;

public class ScansMethod extends ServiceMethod{

	public ScansMethod() {
		super(new ServiceMethodDescription("scans", "Current queue of scans")
			.addResult("result", "Scans", VTable.class));
	    }

	@Override
	public void executeMethod(Map<String, Object> parameters,
			WriteFunction<Map<String, Object>> callback,
			WriteFunction<Exception> errorCallback) {
		// TODO Auto-generated method stub
		
	}

}
