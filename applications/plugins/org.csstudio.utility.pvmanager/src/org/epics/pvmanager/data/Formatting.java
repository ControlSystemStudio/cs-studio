/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 * Formats a data type to a String representation. This class provide default
 * implementations that can format scalars and arrays to an arbitrary
 * precision and a maximum number of array elements.
 *
 * @author carcassi
 */
public abstract class Formatting {

    /**
     * Formats the given data object. For scalars and arrays redirects
     * to the appropriate methods. For anything else uses Object.toString().
     *
     * @param data data object to format
     * @return a String representation
     */
    public String format(Object data) {
        if (data == null)
            return "";

        if (data instanceof Scalar)
            return format((Scalar) data);

        if (data instanceof Array)
            return format((Array) data);

        return data.toString();
    }

    /**
     * Formats a scalar.
     *
     * @param scalar data object to format
     * @return a String representation
     */
    protected abstract String format(Scalar<?> scalar);

    /**
     * Formats an array.
     *
     * @param array data object to format
     * @return a String representation
     */
    protected abstract String format(Array<?> array);

    /**
     * Formats any scalar and array, by using the server side formatting
     * and limiting the elements of the array displayed to maxElements.
     *
     * @param maxElements maximum number of array elements converted to string
     * @return a new set of format options
     */
    public static Formatting newFormatting(int maxElements) {
        return new DefaultFormatting(maxElements);
    }

    /**
     * Formats any scalar and array, by overriding the precision
     * and limiting the elements of the array displayed to maxElements.
     *
     * @param precision number of digits
     * @param maxElements maximum number of array elements converted to string
     * @return a new set of format options
     */
    public static Formatting newFormatting(int precision, int maxElements) {
        return new DefaultFormatting(precision, maxElements);
    }

}
