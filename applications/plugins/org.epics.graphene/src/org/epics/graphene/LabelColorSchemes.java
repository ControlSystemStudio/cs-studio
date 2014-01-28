/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author carcassi
 */
public class LabelColorSchemes {
    /**
     *Returns a new LabelColorScheme, based on the given hex labels. 
     * @param labels a list of strings (color values in hexadecimal)
     * @return a LabelColorScheme with the method getColor, that will return a Color corresponding to the hex label.
     */
    public static LabelColorScheme orderedHueColor(List<String> labels) {
        final List<String> orderedUniqueLabels = new ArrayList<String>(new TreeSet<String>(labels));
        return new LabelColorScheme() {
            
            float step = (1.0f / 3) / ((float) Math.ceil(orderedUniqueLabels.size() / 3.0));

            @Override
            public int getColor(String label) {
                int index = orderedUniqueLabels.indexOf(label);
                if (index == -1) {
                    return 0;
                }
                return Color.HSBtoRGB(index * step, 1.0f, 1.0f);
            }
        };
    }
}
