package org.csstudio.service.math;

import org.diirt.service.Service;
import org.diirt.service.ServiceDescription;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceMethodDescription;
import org.diirt.vtype.VNumber;

public class MathService  extends Service {

    public MathService() {
        super(new ServiceDescription("math", "Simple math service")
            .addServiceMethod(addMethod())
            .addServiceMethod(multMethod()));
    }

    public static ServiceMethodDescription addMethod() {
        return new ServiceMethodDescription("add", "Adds two numbers") {

            @Override
            public ServiceMethod createServiceMethod(ServiceDescription serviceDescription) {
                return new AddServiceMethod(this, serviceDescription);
            }
        }.addArgument("arg1", "First argument", VNumber.class)
                .addArgument("arg2", "Second argument", VNumber.class)
                .addResult("result", "The sum", VNumber.class);
    }

    public static ServiceMethodDescription multMethod() {
        return new ServiceMethodDescription("multiply", "Multiplies two numbers") {

            @Override
            public ServiceMethod createServiceMethod(ServiceDescription serviceDescription) {
                return new MultiplyServiceMethod(this, serviceDescription);
            }
        }.addArgument("arg1", "First argument", VNumber.class)
                .addArgument("arg2", "Second argument", VNumber.class)
                .addResult("result", "The product", VNumber.class);
    }

}