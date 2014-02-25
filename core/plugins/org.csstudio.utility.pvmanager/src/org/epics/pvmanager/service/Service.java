/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author carcassi
 */
public class Service {
    static Pattern namePattern = Pattern.compile("[a-zA-Z_]\\w*");
    
    private final String name;
    private final String description;
    private final Map<String, ServiceMethod> serviceMethods;

    public Service(ServiceDescription serviceDescription) {
        this.name = serviceDescription.name;
        this.description = serviceDescription.description;
        this.serviceMethods = Collections.unmodifiableMap(new HashMap<>(serviceDescription.serviceMethods));
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final Map<String, ServiceMethod> getServiceMethods() {
        return serviceMethods;
    }
    
}
