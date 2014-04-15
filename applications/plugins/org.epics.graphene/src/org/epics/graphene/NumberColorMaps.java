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
 * A utility class that provides implementations of {@link NumberColorMap},
 * a set standard utilities and a directory for color maps.
 *
 * @author carcassi
 */
public class NumberColorMaps {
    
    // TODO: add more color schemes like the ones that can be found:
    // http://www.mathworks.com/help/matlab/ref/colormap.html
    
    /**
     * JET ranges from blue to red, going through cyan and yellow.
     */
    public static final NumberColorMap JET = new NumberColorMapGradient(new Color[]{new Color(0,0,138), 
                                                                                Color.BLUE,
                                                                                Color.CYAN,
                                                                                Color.YELLOW,
                                                                                Color.RED,
                                                                                new Color(138,0,0), 
                                                                                Color.BLACK});
    /**
     * GRAY ranges from black to white.
     */
    public static final NumberColorMap GRAY = new NumberColorMapGradient(new Color[]{Color.BLACK, 
                                                                                       Color.WHITE,
                                                                                       Color.RED});
    private static final Map<String, NumberColorMap> registeredColorSchemes
            = new ConcurrentHashMap<String, NumberColorMap>();
    
    static {
        registeredColorSchemes.put("JET", JET);
        registeredColorSchemes.put("GRAY", GRAY);
    }
    
    /**
     * A set of registered color maps available to all applications.
     * 
     * @return a set of color maps and their names
     */
    public static Map<String, NumberColorMap> getRegisteredColorSchemes() {
        return Collections.unmodifiableMap(registeredColorSchemes);
    }
    
    /**
     * Returns a new optimized instance created by pre-calculating the colors
     * in the given range and storing them in an array.
     * <p>
     * An optimized map will trade off precision for speed. The color will not
     * change smoothly but will be quantized to the size of the array.
     * 
     * @param instance the color map instance to optimize
     * @param range the range of values to optimize
     * @return the optimized map
     */
    public static NumberColorMapInstance optimize(NumberColorMapInstance instance, Range range){
        return new NumberColorMapInstanceOptimized(instance, range);
    }
    
    /**
     * TODO: what is this about?
     * 
     * @param instance
     * @param oldRange
     * @param newRange
     * @return 
     */
    public static NumberColorMapInstance optimize(NumberColorMapInstance instance, Range oldRange, Range newRange){
        return new NumberColorMapInstanceOptimized(instance, oldRange, newRange);
    }
    
}
