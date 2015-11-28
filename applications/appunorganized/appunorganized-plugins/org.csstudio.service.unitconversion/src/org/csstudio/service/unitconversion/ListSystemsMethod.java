package org.csstudio.service.unitconversion;

import gov.bnl.unitconversion.ConversionClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.diirt.service.ServiceDescription;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceMethodDescription;
import org.diirt.vtype.ValueFactory;

/**
 * @author shroffk
 *
 */
public class ListSystemsMethod extends ServiceMethod {

    ConversionClient client;

    public ListSystemsMethod(ServiceMethodDescription serviceMethodDescription, ServiceDescription serviceDescription) {
        super(serviceMethodDescription, serviceDescription);
    }

    @Override
    public void executeAsync(Map<String, Object> parameters,
        Consumer<Map<String, Object>> callback,
        Consumer<Exception> errorCallback) {
    if (client == null) {
        client = new ConversionClient("http://localhost:8000/magnets");
    }
    try {
        List<String> systems = new ArrayList<String>(client.listSystems());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(
            "result",
            ValueFactory.newVStringArray(systems,
                ValueFactory.alarmNone(), ValueFactory.timeNow()));
        resultMap.put("result_size", systems.size());
        callback.accept(resultMap);
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

}
