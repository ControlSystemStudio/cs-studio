package org.csstudio.service.unitconversion;

import gov.bnl.unitconversion.ConversionClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VNumber;
import org.epics.vtype.VStringArray;
import org.epics.vtype.ValueFactory;

/**
 * @author shroffk
 *
 */
public class ListSystemsMethod extends ServiceMethod {

    ConversionClient client;

    public ListSystemsMethod() {
    super(new ServiceMethodDescription("list", "List Devices").addResult(
        "result", "Query Result", VStringArray.class).addResult(
        "result_size", "Query Result size", VNumber.class));
    }

    @Override
    public void executeMethod(Map<String, Object> parameters,
        WriteFunction<Map<String, Object>> callback,
        WriteFunction<Exception> errorCallback) {
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
        callback.writeValue(resultMap);
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

}
