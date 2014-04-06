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
class ValueColorSchemeJet implements ValueColorScheme{
    public ValueColorSchemeInstance createInstance(Range range){
        List<Color> colors = new ArrayList<Color>();
        colors.add(new Color(0,0,138)); //Dark Blue
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        colors.add(new Color(138,0,0)); //Dark Red
        colors.add(Color.BLACK);    //NaN
        ValueColorSchemeInstanceJet colorSchemeInstance = new ValueColorSchemeInstanceJet(colors, range, Color.black.getRGB());
        return colorSchemeInstance;
    }
    private class ValueColorSchemeInstanceJet extends ValueColorSchemeInstanceGeneral{
        public ValueColorSchemeInstanceJet(List<Color> colors, Range range, int nanColor){
        this.range = range;
        this.colors = colors;
        this.nanColor = nanColor;
        percentages = percentageRange(colors.size()-2);
        }
    }
}
