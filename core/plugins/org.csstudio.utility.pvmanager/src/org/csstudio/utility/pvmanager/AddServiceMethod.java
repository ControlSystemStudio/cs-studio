package org.csstudio.utility.pvmanager;

import java.util.HashMap;
import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;

public class AddServiceMethod extends ServiceMethod {
	
	public AddServiceMethod() {
		super(new ServiceMethodDescription("add", "Adds two numbers")
		.addParameter("arg1", "First argument", Number.class)
		.addParameter("arg2", "Second argument", Number.class)
		.addResult("result", "The sum", Number.class));
	}

	@Override
	public void executeMethod(Map<String, Object> parameters,
			WriteFunction<Map<String, Object>> callback,
			WriteFunction<Exception> errorCallback) {
        Number arg1 = (Number) parameters.get("arg1");
        Number arg2 = (Number) parameters.get("arg2");
        Number result = arg1.doubleValue() + arg2.doubleValue();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        callback.writeValue(resultMap);
	}

}
