/**
 * 
 */
package org.csstudio.service.unitconversion;

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
		.addServiceMethod(new ListSystemsMethod())
		.addServiceMethod(new FindDeviceMethod())
		.addServiceMethod(new GetConversionInfo()));
    }

}
