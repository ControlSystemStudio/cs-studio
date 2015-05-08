/**
 *
 */
package org.csstudio.service.unitconversion;

import gov.bnl.unitconversion.ConversionClient;
import gov.bnl.unitconversion.Device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author shroffk
 *
 */
public class GetConversionInfo extends ServiceMethod {

    private ConversionClient client;

    public GetConversionInfo() {
    super(new ServiceMethodDescription("info", "get conversion Info")
        .addArgument("name", "device name", VString.class)
        .addResult("result", "conversion Info", VTable.class));
    }

    @Override
    public void executeMethod(Map<String, Object> parameters,
        WriteFunction<Map<String, Object>> callback,
        WriteFunction<Exception> errorCallback) {
    if (client == null) {
        client = new ConversionClient("http://localhost:8000/magnets");
    }
    try {
        String query = ((VString) parameters.get("name")).getValue();
        List<Device> result = new ArrayList<Device>(client.getConversionInfo(query));

        Collections.sort(result, new Comparator<Device>() {

        @Override
        public int compare(Device o1, Device o2) {
            return o1.getName().compareTo(o2.getName());
        }

        });

        List<String> names = new ArrayList<String>();
        List<Class<?>> types = new ArrayList<Class<?>>();
        List<Object> values = new ArrayList<Object>();

        // Add Device Name column
        names.add("Name");
        types.add(String.class);
        values.add(Lists.transform(result, new Function<Device, String>() {
        @Override
        public String apply(Device input) {
            return input.getName();
        }
        }));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result",
            ValueFactory.newVTable(types, names, values));
        resultMap.put("result_size", result.size());
        callback.writeValue(resultMap);

    } catch (IOException e) {
        errorCallback.writeValue(e);
    }

    }
}
