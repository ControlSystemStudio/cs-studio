package org.csstudio.service.scanserver;

import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VNumber;

public class CommandsMethod extends ServiceMethod{

	public CommandsMethod() {
		super(new ServiceMethodDescription("commands", "Commands that make up a scan")
			.addArgument("id", "scan id", VNumber.class)
			.addResult("result", "commands", VNumber.class));
	    }

	@Override
	public void executeMethod(Map<String, Object> parameters,
			WriteFunction<Map<String, Object>> callback,
			WriteFunction<Exception> errorCallback) {
		// TODO Auto-generated method stub
		
	}

}
