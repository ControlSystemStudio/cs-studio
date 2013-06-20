package org.csstudio.service.scanserver;

import org.csstudio.service.scanserver.QueueMethod;
import org.csstudio.service.scanserver.DataMethod;
import org.csstudio.service.scanserver.CommandsMethod;
import org.csstudio.service.scanserver.ScansMethod;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceDescription;

public class ScanServerService extends Service {
    /**
     * @param serviceDescription
     */
    public ScanServerService() {
	super(new ServiceDescription("scanserver", "ScanServer service")
	.addServiceMethod(new QueueMethod())
	.addServiceMethod(new Scan2DMethod())
	.addServiceMethod(new DataMethod())
	.addServiceMethod(new CommandsMethod())
	.addServiceMethod(new ScansMethod()));	
    }
}
