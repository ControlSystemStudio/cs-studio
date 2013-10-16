package org.csstudio.service.scanserver;

import org.csstudio.service.scanserver.DataMethod;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceDescription;

public class ScanServerService extends Service {
    /**
     * @param serviceDescription
     */
    public ScanServerService() {
	super(new ServiceDescription("scanserver", "ScanServer service")
	.addServiceMethod(new Scan2DMethod())
	.addServiceMethod(new DataMethod()));	
    }
}
