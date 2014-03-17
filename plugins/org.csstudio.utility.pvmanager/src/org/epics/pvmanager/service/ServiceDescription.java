/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.service;

import java.util.HashMap;
import java.util.Map;
import static org.epics.pvmanager.service.Service.namePattern;

/**
 *
 * @author carcassi
 */
public class ServiceDescription {
    
    String name;
    String description;
    Map<String, ServiceMethod> serviceMethods = new HashMap<>();

    public ServiceDescription(String name, String description) {
        this.name = name;
        this.description = description;
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Name must start by a letter and only consist of letters and numbers");
        }
    }
    
    public ServiceDescription addServiceMethod(ServiceMethod serviceMethod) {
        serviceMethods.put(serviceMethod.getName(), serviceMethod);
        return this;
    }
}
