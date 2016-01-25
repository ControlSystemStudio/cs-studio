package org.csstudio.diirt.util;

import java.util.logging.Logger;

import org.diirt.service.ServiceProvider;
import org.diirt.service.ServiceRegistry;

public class RegisterService {

    private static final Logger logger = Logger.getLogger(RegisterService.class.getCanonicalName());

    public void registerService(ServiceProvider serviceProvider) {
        logger.info("register service:" + serviceProvider.getName());
        ServiceRegistry.getDefault().registerServices(serviceProvider);
    }

    public void deregisterService(ServiceProvider serviceProvider) {
        logger.info("deregister service:" + serviceProvider.getName());
    }

}
