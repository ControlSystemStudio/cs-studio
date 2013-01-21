/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.epics.pvmanager.util.NumberFormats;

/**
 * Default implementation for formatting.
 *
 * @author carcassi
 */
public class SimpleValueFormat extends ValueFormat {

    private int maxElements;

    /**
     * Formats any scalar and array, by using the server side formatting
     * and limiting the elements of the array displayed to maxElements.
     *
     * @param maxElements maximum number of array elements converted to string
     */
    public SimpleValueFormat(int maxElements) {
        this.maxElements = maxElements;
    }

    @Override
    protected StringBuffer format(Scalar scalar, StringBuffer toAppendTo, FieldPosition pos) {
        if (scalar == null || scalar.getValue() == null) {
            return toAppendTo;
        }

        if (scalar instanceof Display && nf(scalar) != null) {
            NumberFormat f = nf(scalar);
            return f.format(scalar.getValue(), toAppendTo, pos);
        }

        toAppendTo.append(scalar.getValue());
        return toAppendTo;
    }

    /**
     * Returns the appropriate NumberFormat: either the one
     * from the data or the set by the formatting options.
     *
     * @param obj data object
     * @return number format
     */
    private NumberFormat nf(Object obj) {
        if (getNumberFormat() != null)
            return getNumberFormat();

        if (obj instanceof Display) {
            return ((Display) obj).getFormat();
        }

        return null;
    }

    @Override
    protected StringBuffer format(Array<?> array, StringBuffer toAppendTo, FieldPosition pos) {
        if (array == null || array.getArray() == null) {
            return toAppendTo;
        }

        NumberFormat f = null;
        if (array instanceof Display) {
            f = nf(array);
        }

        toAppendTo.append("[");
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
                    toAppendTo.append(", ");
                }
                toAppendTo.append(f.format(data[i]));
            }

        // double array support
        } else if (array.getArray() instanceof double[]) {
            double[] data = (double[]) array.getArray();
            if (data.length > maxElements) {
                hasMore = true;
            }
            for (int i = 0; i < Math.min(data.length, maxElements); i++) {
                if (i != 0) {
                    toAppendTo.append(", ");
                }
                toAppendTo.append(f.format(data[i]));
            }
        } else {
            throw new UnsupportedOperationException("Type " + array.getClass().getName() + " not yet supported.");
        }

        if (hasMore) {
            toAppendTo.append(", ...");
        }
        toAppendTo.append("]");
        return toAppendTo;
    }
}
