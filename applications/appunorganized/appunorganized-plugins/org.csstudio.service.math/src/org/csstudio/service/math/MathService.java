package org.csstudio.service.math;

import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceDescription;

public class MathService  extends Service {

    public MathService() {
        super(new ServiceDescription("math", "Simple math service")
            .addServiceMethod(new AddServiceMethod())
            .addServiceMethod(new MultiplyServiceMethod()));
    }

}