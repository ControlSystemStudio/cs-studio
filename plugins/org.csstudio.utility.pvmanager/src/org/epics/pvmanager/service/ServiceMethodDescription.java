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
public class ServiceMethodDescription {
    
    String name;
    String description;
    Map<String, Class<?>> argumentTypes = new HashMap<>();
    Map<String, String> argumentDescriptions = new HashMap<>();
    Map<String, Class<?>> resultTypes = new HashMap<>();
    Map<String, String> resultDescriptions = new HashMap<>();

    public ServiceMethodDescription(String name, String description) {
        this.name = name;
        this.description = description;
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Name must start by a letter and only consist of letters and numbers");
        }
    }
    
    public ServiceMethodDescription addArgument(String name, String description, Class<?> type) {
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Name must start by a letter and only consist of letters and numbers");
        }
        argumentTypes.put(name, type);
        argumentDescriptions.put(name, description);
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
