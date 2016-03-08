/**
 *
 */
package org.csstudio.service.unitconversion;

import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceMethodDescription;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VString;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VTable;
import org.diirt.service.Service;
import org.diirt.service.ServiceDescription;

/**
 * @author shroffk
 *
 */
public class UnitConversionService extends Service {

    /**
     * @param serviceDescription
     */
    public UnitConversionService() {
    super(new ServiceDescription("uc", "UnitConversion service")
        .addServiceMethod(listMethod())
        .addServiceMethod(findMethod())
        .addServiceMethod(getInfoMethod()));
    }

    public static ServiceMethodDescription listMethod() {
        return new ServiceMethodDescription("find", "Find Devices") {

            @Override
            public ServiceMethod createServiceMethod(ServiceDescription serviceDescription) {
                return new ListSystemsMethod(this, serviceDescription);
            }
        }.addResult(
                "result", "Query Result", VStringArray.class).addResult(
                        "result_size", "Query Result size", VNumber.class);
    }

    public static ServiceMethodDescription findMethod() {
        return new ServiceMethodDescription("list", "List Devices") {

            @Override
            public ServiceMethod createServiceMethod(ServiceDescription serviceDescription) {
                return new FindDeviceMethod(this, serviceDescription);
            }
        }.addArgument("name_query", "device name search pattern",
                VString.class)
                .addResult("result", "Query Result", VTable.class)
                .addResult("result_size", "Query Result size", VNumber.class);
    }

    public static ServiceMethodDescription getInfoMethod() {
        return new ServiceMethodDescription("info", "get conversion Info") {

            @Override
            public ServiceMethod createServiceMethod(ServiceDescription serviceDescription) {
                return new GetConversionInfo(this, serviceDescription);
            }
        }.addArgument("name", "device name", VString.class)
                .addResult("result", "conversion Info", VTable.class);
    }

}
