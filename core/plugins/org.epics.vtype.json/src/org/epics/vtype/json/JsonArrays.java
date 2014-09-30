/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype.json;

import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.epics.util.array.ArrayByte;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ArrayLong;
import org.epics.util.array.ArrayShort;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListLong;
import org.epics.util.array.ListShort;

/**
 * Utility classes to convert JSON arrays to and from Lists and ListNumbers.
 *
 * @author carcassi
 */
public class JsonArrays {
    
    /**
     * Checks whether the array contains only numbers.
     * 
     * @param array a JSON array
     * @return true if all elements are JSON numbers
     */
    public static boolean isNumericArray(JsonArray array) {
        for (JsonValue value : array) {
            if (!(value instanceof JsonNumber)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks whether the array contains only strings.
     * 
     * @param array a JSON array
     * @return true if all elements are JSON strings
     */
    public static boolean isStringArray(JsonArray array) {
        for (JsonValue value : array) {
            if (!(value instanceof JsonString)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Converts the given numeric JSON array to a ListDouble.
     * 
     * @param array an array of numbers
     * @return a new ListDouble
     */
    public static ListDouble toListDouble(JsonArray array) {
        double[] values = new double[array.size()];
        for (int i = 0; i < values.length; i++) {
            if (array.isNull(i)) {
                values[i] = Double.NaN;
            } else {
                values[i] = array.getJsonNumber(i).doubleValue();
            }
        }
        return new ArrayDouble(values);
    }
    
    /**
     * Converts the given numeric JSON array to a ListFloat.
     * 
     * @param array an array of numbers
     * @return a new ListFloat
     */
    public static ListFloat toListFloat(JsonArray array) {
        float[] values = new float[array.size()];
        for (int i = 0; i < values.length; i++) {
            if (array.isNull(i)) {
                values[i] = Float.NaN;
            } else {
                values[i] = (float) array.getJsonNumber(i).doubleValue();
            }
        }
        return new ArrayFloat(values);
    }

    /**
     * Converts the given numeric JSON array to a ListLong.
     * 
     * @param array an array of numbers
     * @return a new ListLong
     */
    public static ListLong toListLong(JsonArray array) {
        long[] values = new long[array.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = (long) array.getJsonNumber(i).longValue();
        }
        return new ArrayLong(values);
    }
    
    /**
     * Converts the given numeric JSON array to a ListInt.
     * 
     * @param array an array of numbers
     * @return a new ListInt
     */
    public static ListInt toListInt(JsonArray array) {
        int[] values = new int[array.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) array.getJsonNumber(i).intValue();
        }
        return new ArrayInt(values);
    }

    /**
     * Converts the given numeric JSON array to a ListShort.
     * 
     * @param array an array of numbers
     * @return a new ListShort
     */
    public static ListShort toListShort(JsonArray array) {
        short[] values = new short[array.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = (short) array.getJsonNumber(i).intValue();
        }
        return new ArrayShort(values);
    }

    /**
     * Converts the given numeric JSON array to a ListByte.
     * 
     * @param array an array of numbers
     * @return a new ListByte
     */
    public static ListByte toListByte(JsonArray array) {
        byte[] values = new byte[array.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = (byte) array.getJsonNumber(i).intValue();
        }
        return new ArrayByte(values);
    }

    /**
     * Converts the given string JSON array to a List of Strings.
     * 
     * @param array an array of strings
     * @return a new List of Strings
     */
    public static List<String> toListString(JsonArray array) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            strings.add(array.getString(i));
        }
        return strings;
    }

    
}
