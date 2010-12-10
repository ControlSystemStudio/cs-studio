/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.NumberFormat;
import org.epics.pvmanager.util.NumberFormats;

/**
 * Default implementation for formatting.
 *
 * @author carcassi
 */
class DefaultFormatting extends Formatting {

    private int maxElements;
    private NumberFormat format;

    DefaultFormatting(int maxElements) {
        this.maxElements = maxElements;
    }

    DefaultFormatting(int precision, int maxElements) {
        this.maxElements = maxElements;
        format = NumberFormats.format(precision);
    }

    @Override
    public String format(Scalar<?> scalar) {
        if (scalar == null || scalar.getValue() == null) {
            return "";
        }

        if (scalar instanceof Display) {
            NumberFormat f = nf(scalar);
            return f.format(scalar.getValue());
        }

        return scalar.getValue().toString();
    }

    /**
     * Returns the appropriate NumberFormat: either the one
     * from the data or the set by the formatting options.
     *
     * @param obj data object
     * @return number format
     */
    private NumberFormat nf(Object obj) {
        if (format != null)
            return format;

        if (obj instanceof Display) {
            return ((Display) obj).getFormat();
        }

        return null;
    }

    @Override
    public String format(Array<?> array) {
        if (array == null || array.getArray() == null) {
            return "";
        }

        NumberFormat f = null;
        if (array instanceof Display) {
            f = nf(array);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean hasMore = false;

        // To support all array types, there is no other way than
        // implementing them one by one... curse non-reified generics!

        // int array support
        if (array.getArray() instanceof int[]) {
            int[] data = (int[]) array.getArray();
            if (data.length > maxElements) {
                hasMore = true;
            }
            for (int i = 0; i < Math.min(data.length, maxElements); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(f.format(data[i]));
            }

        // double array support
        } else if (array.getArray() instanceof double[]) {
            double[] data = (double[]) array.getArray();
            if (data.length > maxElements) {
                hasMore = true;
            }
            for (int i = 0; i < Math.min(data.length, maxElements); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(f.format(data[i]));
            }
        } else {
            throw new UnsupportedOperationException("Type " + array.getClass().getName() + " not yet supported.");
        }

        if (hasMore) {
            sb.append(", ...");
        }
        sb.append("]");
        return sb.toString();
    }
}
