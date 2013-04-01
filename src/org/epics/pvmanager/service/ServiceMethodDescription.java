/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.service;

import java.util.HashMap;
import java.util.Map;
import static org.epics.pvmanager.service.Service.namePattern;

/**
 *
 * @author carcassi
 */
public class ServiceMethodDescription {
    
    String name;
    String description;
    Map<String, Class<?>> parameterTypes = new HashMap<>();
    Map<String, String> parameterDescriptions = new HashMap<>();
    Map<String, Class<?>> resultTypes = new HashMap<>();
    Map<String, String> resultDescriptions = new HashMap<>();

    public ServiceMethodDescription(String name, String description) {
        this.name = name;
        this.description = description;
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Name must start by a letter and only consist of letters and numbers");
        }
    }
    
    public ServiceMethodDescription addParameter(String name, String description, Class<?> type) {
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Name must start by a letter and only consist of letters and numbers");
        }
        parameterTypes.put(name, type);
        parameterDescriptions.put(name, description);
        return this;
    }
    
    public ServiceMethodDescription addResult(String name, String description, Class<?> type) {
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Name must start by a letter and only consist of letters and numbers");
        }
        resultTypes.put(name, type);
        resultDescriptions.put(name, description);
        return this;
    }
}
