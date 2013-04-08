/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author carcassi
 */
public class ServiceRegistry {
    private final static ServiceRegistry registry = new ServiceRegistry();

    public static ServiceRegistry getDefault() {
        return registry;
    }
    
    private Map<String, Service> services = new ConcurrentHashMap<>();
    
    public void registerService(Service service) {
        services.put(service.getName(), service);
    }
    
    public Service findService(String name) {
        return services.get(name);
    }
    
    public ServiceMethod findServiceMethod(String serviceName, String methodName) {
        Service service = findService(serviceName);
        if (service == null) {
            return null;
        }
        return service.getServiceMethods().get(methodName);
    }
    
    public ServiceMethod findServiceMethod(String fullName) {
        String[] tokens = fullName.split("/");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Service method id must be \"service/method\"");
        }
        ServiceMethod method = findServiceMethod(tokens[0], tokens[1]);
        if (method == null) {
            throw new IllegalArgumentException("Service \"" + fullName + "\" not found");
        }
        return method;
    }
}
