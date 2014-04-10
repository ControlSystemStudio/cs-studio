/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.graphene;

import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author carcassi
 */
public class ValueColorSchemes {
    public static final ValueColorScheme JET = new ValueColorScheme(new Color[]{new Color(0,0,138), 
                                                                                Color.BLUE,
                                                                                Color.CYAN,
                                                                                Color.YELLOW,
                                                                                Color.RED,
                                                                                new Color(138,0,0), 
                                                                                Color.BLACK});
    
    public static final ValueColorScheme GRAY_SCALE = new ValueColorScheme(new Color[]{Color.BLACK, 
                                                                                       Color.WHITE,
                                                                                       Color.RED});
    private static final Map<String, ValueColorScheme> registeredColorSchemes
            = new ConcurrentHashMap<String, ValueColorScheme>();
    
    static {
        registeredColorSchemes.put("JET", JET);
        registeredColorSchemes.put("GRAY_SCALE", GRAY_SCALE);
    }
    
    public static Map<String, ValueColorScheme> getRegisteredColorSchemes() {
        return Collections.unmodifiableMap(registeredColorSchemes);
    }
}
