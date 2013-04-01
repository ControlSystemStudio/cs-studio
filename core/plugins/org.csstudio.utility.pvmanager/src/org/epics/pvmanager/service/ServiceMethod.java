/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.epics.pvmanager.WriteFunction;
import static org.epics.pvmanager.service.Service.namePattern;

/**
 *
 * @author carcassi
 */
public abstract class ServiceMethod {
    private String name;
    private final Map<String, Class<?>> parameterTypes;
    private final Map<String, String> parameterDescriptions;
    private final Map<String, Class<?>> resultTypes;
    private final Map<String, String> resultDescriptions;

    public ServiceMethod(ServiceMethodDescription serviceMethodDescription) {
        this.name = serviceMethodDescription.name;
        this.parameterTypes = Collections.unmodifiableMap(new HashMap<>(serviceMethodDescription.parameterTypes));
        this.parameterDescriptions = Collections.unmodifiableMap(new HashMap<>(serviceMethodDescription.parameterDescriptions));
        this.resultTypes = Collections.unmodifiableMap(new HashMap<>(serviceMethodDescription.resultTypes));
        this.resultDescriptions = Collections.unmodifiableMap(new HashMap<>(serviceMethodDescription.resultDescriptions));
    }

    public final String getName() {
        return name;
    }

    public final Map<String, Class<?>> getParameterTypes() {
        return parameterTypes;
    }

    public final Map<String, String> getParameterDescriptions() {
        return parameterDescriptions;
    }

    public final Map<String, Class<?>> getOutputTypes() {
        return resultTypes;
    }

    public final Map<String, String> getOutputDescriptions() {
        return resultDescriptions;
    }
    
    void validateParameters(Map<String, Object> parameters) {
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            String parameterName = parameter.getKey();
            Object parameterValue = parameter.getValue();
            Class<?> parameterType = parameterTypes.get(parameterName);
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
