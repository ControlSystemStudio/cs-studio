/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.graphene;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author carcassi
 */
public class ValueColorSchemes {
    public static final ValueColorScheme JET = new ValueColorSchemeJet();
    private static final Map<String, ValueColorScheme> registeredColorSchemes
            = new ConcurrentHashMap<String, ValueColorScheme>();
    
    static {
        registeredColorSchemes.put("JET", JET);
    }
    
    public static Map<String, ValueColorScheme> getRegisteredColorSchemes() {
        return Collections.unmodifiableMap(registeredColorSchemes);
    }
}
