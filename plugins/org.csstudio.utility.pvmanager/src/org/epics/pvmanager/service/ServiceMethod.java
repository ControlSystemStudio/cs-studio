/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.epics.pvmanager.WriteFunction;

/**
 *
 * @author carcassi
 */
public abstract class ServiceMethod {
    private final String name;
    private final String description;
    private final Map<String, Class<?>> argumentTypes;
    private final Map<String, String> argumentDescriptions;
    private final Map<String, Class<?>> resultTypes;
    private final Map<String, String> resultDescriptions;

    public ServiceMethod(ServiceMethodDescription serviceMethodDescription) {
        this.name = serviceMethodDescription.name;
        this.description = serviceMethodDescription.description;
        this.argumentTypes = Collections.unmodifiableMap(new HashMap<>(serviceMethodDescription.argumentTypes));
        this.argumentDescriptions = Collections.unmodifiableMap(new HashMap<>(serviceMethodDescription.argumentDescriptions));
        this.resultTypes = Collections.unmodifiableMap(new HashMap<>(serviceMethodDescription.resultTypes));
        this.resultDescriptions = Collections.unmodifiableMap(new HashMap<>(serviceMethodDescription.resultDescriptions));
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final Map<String, Class<?>> getArgumentTypes() {
        return argumentTypes;
    }

    public final Map<String, String> getArgumentDescriptions() {
        return argumentDescriptions;
    }

    public final Map<String, Class<?>> getResultTypes() {
        return resultTypes;
    }

    public final Map<String, String> getResultDescriptions() {
        return resultDescriptions;
    }
    
    void validateParameters(Map<String, Object> parameters) {
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            String parameterName = parameter.getKey();
            Object parameterValue = parameter.getValue();
            Class<?> parameterType = argumentTypes.get(parameterName);
            if (parameterType == null) {
                throw new IllegalArgumentException("ServiceMethod " + name + ": unexpected parameter " + parameterName);
            }
            if (!parameterType.isInstance(parameterValue)) {
                throw new IllegalArgumentException("ServiceMethod " + name + ": parameter " + parameterName + " should be of type " + parameterType.getSimpleName() + " but was " + parameterValue.getClass().getSimpleName());
            }
        }
    }
    
    public final void execute(Map<String, Object> parameters, WriteFunction<Map<String, Object>> callback, WriteFunction<Exception> errorCallback) {
        try {
            validateParameters(parameters);
            executeMethod(parameters, callback, errorCallback);
        } catch (Exception ex) {
            errorCallback.writeValue(ex);
        }
    }
    
    public abstract void executeMethod(Map<String, Object> parameters, WriteFunction<Map<String, Object>> callback, WriteFunction<Exception> errorCallback);
}
