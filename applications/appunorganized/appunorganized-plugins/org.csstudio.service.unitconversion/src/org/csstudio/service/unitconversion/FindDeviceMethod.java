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
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author shroffk
 *
 */
public class FindDeviceMethod extends ServiceMethod {

    private ConversionClient client = null;

    public FindDeviceMethod() {
    super(new ServiceMethodDescription("find", "Find Devices")
        .addArgument("name_query", "device name search pattern",
            VString.class)
        .addResult("result", "Query Result", VTable.class)
        .addResult("result_size", "Query Result size", VNumber.class));
    }

    @Override
    public void executeMethod(Map<String, Object> parameters,
        WriteFunction<Map<String, Object>> callback,
        WriteFunction<Exception> errorCallback) {
    if (client == null) {
        client = new ConversionClient("http://localhost:8000/magnets");
    }
    try {
        String query = ((VString) parameters.get("name_query")).getValue();
        List<Device> result = new ArrayList<Device>(
            client.findDevices(query));

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
        // Add Device system column
        names.add("System");
        types.add(String.class);
        values.add(Lists.transform(result, new Function<Device, String>() {
        @Override
        public String apply(Device input) {
            return input.getSystem();
        }
        }));
        // Add Device InstallId column
        names.add("InstallId");
        types.add(String.class);
        values.add(Lists.transform(result, new Function<Device, String>() {
        @Override
        public String apply(Device input) {
            return String.valueOf(input.getInstallId());
        }
        }));

        // Add Device ComponentType column
        names.add("ComponentType");
        types.add(String.class);
        values.add(Lists.transform(result, new Function<Device, String>() {
        @Override
        public String apply(Device input) {
            return input.getComponentType();
        }
        }));

        // Add Device TypeDescription column
        names.add("TypeDescription");
        types.add(String.class);
        values.add(Lists.transform(result, new Function<Device, String>() {
        @Override
        public String apply(Device input) {
            return input.getTypeDescription();
        }
        }));

        // Add Device TypeDescription column
        names.add("InstallId");
        types.add(String.class);
        values.add(Lists.transform(result, new Function<Device, String>() {
        @Override
        public String apply(Device input) {
            return String.valueOf(input.getInstallId());
        }
        }));

        // Add Device TypeDescription column
        names.add("InventoryId");
        types.add(String.class);
        values.add(Lists.transform(result, new Function<Device, String>() {
        @Override
        public String apply(Device input) {
            return String.valueOf(input.getInventoryId());
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
