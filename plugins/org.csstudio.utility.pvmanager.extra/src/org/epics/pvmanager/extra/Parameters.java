/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author carcassi
 */
public class Parameters {
    
    private final Map<Object, Object> map;

    protected Parameters(Map<Object, Object> map) {
        this.map = map;
    }

    public Parameters(Parameters oldParams, Parameters... newParams) {
        this(combineMaps(oldParams, newParams));
    }
    
    private static Map<Object, Object> combineMaps(Parameters oldParams, Parameters... newParams) {
        Map<Object, Object> parameters = new HashMap<Object, Object>(oldParams.map);
        for (Parameters newParam : newParams) {
            parameters.putAll(newParam.map);
        }
        return parameters;
    }
    
    public Map<Object, Object> getParameters() {
        return map;
    }
}
