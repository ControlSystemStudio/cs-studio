/**
 *
 */
package org.csstudio.service.unitconversion;

import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceDescription;

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
        .addServiceMethod(new ListSystemsMethod())
        .addServiceMethod(new FindDeviceMethod())
        .addServiceMethod(new GetConversionInfo()));
    }

}
