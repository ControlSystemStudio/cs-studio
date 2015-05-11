package org.csstudio.graphene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.epics.graphene.NumberColorMap;
import org.epics.graphene.NumberColorMaps;

public class NumberColorMapUtil {

    public static String[] colorMapNames() {
        List<String> colorMapNames = new ArrayList<String>(NumberColorMaps.getRegisteredColorSchemes().keySet());
        Collections.sort(colorMapNames);
        return colorMapNames.toArray(new String[colorMapNames.size()]);
    }

    public static String colorMapName(NumberColorMap colorMap) {
        String key = null;
        for (Map.Entry<String, NumberColorMap> entry : NumberColorMaps.getRegisteredColorSchemes().entrySet()) {
            if (entry.getValue().equals(colorMap)) {
                key = entry.getKey();
            }
        }

        return key;
    }

    public static int colorMapIndex(NumberColorMap colorMap) {
        String key = colorMapName(colorMap);

        if (key == null) {
            return -1;
        }

        int index = Arrays.asList(colorMapNames()).indexOf(key);
        return index;
    }

    public static NumberColorMap colorMap(int index) {
        if (index == -1)
            return null;

        return NumberColorMaps.getRegisteredColorSchemes().get(colorMapNames()[index]);
    }

}
