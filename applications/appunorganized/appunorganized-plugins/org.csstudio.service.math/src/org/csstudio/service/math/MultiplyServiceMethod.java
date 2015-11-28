package org.csstudio.service.math;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.diirt.service.ServiceDescription;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceMethodDescription;
import org.diirt.vtype.VNumber;

public class MultiplyServiceMethod extends ServiceMethod {

    public MultiplyServiceMethod(ServiceMethodDescription serviceMethodDescription, ServiceDescription serviceDescription) {
        super(serviceMethodDescription, serviceDescription);
    }

    @Override
    public void executeAsync(Map<String, Object> parameters,
            Consumer<Map<String, Object>> callback,
            Consumer<Exception> errorCallback) {
    Number arg1 = ((VNumber) parameters.get("arg1")).getValue();
    Number arg2 = ((VNumber) parameters.get("arg2")).getValue();
    Number result = arg1.doubleValue() * arg2.doubleValue();
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("result", result);
    callback.accept(resultMap);
    }

}
