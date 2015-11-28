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
import java.util.function.Consumer;

import org.diirt.service.ServiceDescription;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceMethodDescription;
import org.diirt.vtype.VString;
import org.diirt.vtype.ValueFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author shroffk
 *
 */
public class FindDeviceMethod extends ServiceMethod {

    private ConversionClient client = null;

    public FindDeviceMethod(ServiceMethodDescription serviceMethodDescription, ServiceDescription serviceDescription) {
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
        callback.accept(resultMap);

    } catch (IOException e) {
        errorCallback.accept(e);
    }

    }

}
