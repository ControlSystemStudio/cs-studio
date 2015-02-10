package org.csstudio.service.math;

import org.diirt.service.Service;
import org.diirt.service.ServiceDescription;

public class MathService  extends Service {

    public MathService() {
        super(new ServiceDescription("math", "Simple math service")
	        .addServiceMethod(new AddServiceMethod())
	        .addServiceMethod(new MultiplyServiceMethod()));
    }
    
}