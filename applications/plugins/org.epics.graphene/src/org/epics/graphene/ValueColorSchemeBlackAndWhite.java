/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sjdallst
 */
public class ValueColorSchemeBlackAndWhite implements ValueColorScheme{
    public ValueColorSchemeInstance createInstance(Range range){
        List<Color> colors = new ArrayList<Color>();
        colors.add(Color.black);
        colors.add(Color.white);
        colors.add(Color.red);
        ValueColorSchemeInstanceBlackAndWhite colorSchemeInstance = new ValueColorSchemeInstanceBlackAndWhite(colors, range, Color.red.getRGB());
        return colorSchemeInstance;
    }
    private class ValueColorSchemeInstanceBlackAndWhite extends ValueColorSchemeInstanceGeneral{
        public ValueColorSchemeInstanceBlackAndWhite(List<Color> colors, Range range, int nanColor){
        this.range = range;
        this.colors = colors;
        this.nanColor = nanColor;
        percentages = percentageRange(colors.size()-2);
        }
    }
}
