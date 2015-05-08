package org.csstudio.graphene;

import java.util.List;

public class ComboDataUtil {
    public static String[] toStringArray(List<?> values) {
        String[] result = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).toString();
        }
        return result;
    }

    public static int indexOf(String[] labels, String label) {
        for (int i=0; i<labels.length; i++) {
            if (labels[i].equals(label)) {
                return i;
            }
        }
        return -1;
    }
}
