/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Various utility methods for runtime handling of the types defined in
 * this package.
 *
 * @author carcassi
 */
public class ValueUtil {

    private ValueUtil() {
        // Can't instanciate
    }

    private static Collection<Class<?>> types = Arrays.<Class<?>>asList(VByteArray.class, VDouble.class,
            VDoubleArray.class, VEnum.class, VEnumArray.class, VFloatArray.class,
            VInt.class, VIntArray.class, VMultiDouble.class, VMultiEnum.class,
            VMultiInt.class, VMultiString.class, VShortArray.class,
            VStatistics.class, VString.class, VStringArray.class, VTable.class);

    /**
     * Returns the type of the object by returning the class object of one
     * of the VXxx interfaces. The getClass() methods returns the
     * concrete implementation type, which is of little use. If no
     * super-interface is found, Object.class is used.
     * 
     * @param obj an object implementing a standard type
     * @return the type is implementing
     */
    public static Class<?> typeOf(Object obj) {
        if (obj == null)
            return null;

        return typeOf(obj.getClass());
    }

    private static Class<?> typeOf(Class<?> clazz) {
        if (clazz.equals(Object.class))
            return Object.class;

        for (int i = 0; i < clazz.getInterfaces().length; i++) {
            Class<?> interf = clazz.getInterfaces()[i];
            if (types.contains(interf))
                return interf;
        }

        return typeOf(clazz.getSuperclass());
    }

    /**
     * Extracts the alarm information if present.
     *
     * @param obj an object implementing a standard type
     * @return the alarm information for the object
     */
    public static Alarm alarmOf(Object obj) {
        if (obj instanceof Alarm)
            return (Alarm) obj;
        return null;
    }

    /**
     * Extracts the time information if present.
     *
     * @param obj an object implementing a standard type
     * @return the time information for the object
     */
    public static Time timeOf(Object obj) {
        if (obj instanceof Time)
            return (Time) obj;
        return null;
    }

    /**
     * Extracts the display information if present.
     *
     * @param obj an object implementing a standard type
     * @return the display information for the object
     */
    public static Display displayOf(Object obj) {
        if (!(obj instanceof Display))
            return null;
        Display display = (Display) obj;
        if (display.getLowerAlarmLimit() == null || display.getLowerDisplayLimit() == null)
            return null;
        return display;
    }
    
    /**
     * Extracts the numericValueOf the object and normalizes according
     * to the display range.
     * 
     * @param obj an object implementing a standard type
     * @return the value normalized in its display range, or null
     *         if no value or display information is present
     */
    public static Double normalizedNumericValueOf(Object obj) {
        return normalize(numericValueOf(obj), displayOf(obj));
    }

    /**
     * Normalizes the given value according to the given display information.
     *
     * @param value a value
     * @param display the display information
     * @return the normalized value, or null of either value or display is null
     */
    public static Double normalize(Number value, Display display) {
        if (value == null || display == null) {
            return null;
        }

        return (value.doubleValue() - display.getLowerDisplayLimit()) / (display.getUpperDisplayLimit() - display.getLowerDisplayLimit());
    }

    /**
     * Normalizes the given value according to the given range;
     *
     * @param value a value
     * @param lowValue the lowest value in the range
     * @param highValue the highest value in the range
     * @return the normalized value, or null if any value is null
     */
    public static Double normalize(Number value, Number lowValue, Number highValue) {
        if (value == null || lowValue == null || highValue == null) {
            return null;
        }

        return (value.doubleValue() - lowValue.doubleValue()) / (highValue.doubleValue() - lowValue.doubleValue());
    }

    /**
     * Extracts a numeric value for the object. If it's a numeric scalar,
     * the value is returned. If it's a numeric array, the first element is
     * returned. If it's a numeric multi array, the value of the first
     * element is returned.
     *
     * @param obj an object implementing a standard type
     * @return the numeric value
     */
    public static Double numericValueOf(Object obj) {
        if (obj instanceof Scalar) {
            Object value = ((Scalar) obj).getValue();
            if (value instanceof Number)
                return ((Number) value).doubleValue();
        }

        if (obj instanceof Array) {
            Object array = ((Array) obj).getArray();
            if (array instanceof byte[]) {
                byte[] tArray = (byte[]) array;
                if (tArray.length != 0)
                    return (double) tArray[0];
            }
            if (array instanceof short[]) {
                short[] tArray = (short[]) array;
                if (tArray.length != 0)
                    return (double) tArray[0];
            }
            if (array instanceof int[]) {
                int[] tArray = (int[]) array;
                if (tArray.length != 0)
                    return (double) tArray[0];
            }
            if (array instanceof float[]) {
                float[] tArray = (float[]) array;
                if (tArray.length != 0)
                    return (double) tArray[0];
            }
            if (array instanceof double[]) {
                double[] tArray = (double[]) array;
                if (tArray.length != 0)
                    return (double) tArray[0];
            }
        }

        if (obj instanceof MultiScalar) {
            List values = ((MultiScalar) obj).getValues();
            if (!values.isEmpty())
                return numericValueOf(values.get(0));
        }

        return null;
    }

    /**
     * Converts a VImage to an AWT BufferedImage, so that it can be displayed.
     * The content of the vImage buffer is copied, so further changes
     * to the VImage will not modify the BufferedImage.
     *
     * @param vImage the image to be converted
     * @return a new BufferedImage
     */
    public static BufferedImage toImage(VImage vImage) {
        BufferedImage image = new BufferedImage(vImage.getWidth(), vImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        System.arraycopy(vImage.getData(), 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData(), 0,
                vImage.getWidth() * vImage.getHeight() * 3);
        return image;
    }

    /**
     * Converts an AWT BufferedImage to a VImage.
     * <p>
     * Currently, only TYPE_3BYTE_BGR is supported
     * 
     * @param image
     * @return a new image
     */
    public static VImage toVImage(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_3BYTE_BGR) {
            throw new IllegalArgumentException("Only BufferedImages of type TYPE_3BYTE_BGR can currently be converted to VImage");
        }

        byte[] buffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        return ValueFactory.newVImage(image.getHeight(), image.getWidth(), buffer);
    }
    
    /**
     * Returns a comparator with the order defined by the timestamp.
     * 
     * @param <T> a Time type
     * @return a comparator
     */
    public static <T extends Time> Comparator<T> timeComparator() {
        return new Comparator<T>() {

            @Override
            public int compare(T o1, T o2) {
                return o1.getTimeStamp().compareTo(o2.getTimeStamp());
            }
        };
    }
}
