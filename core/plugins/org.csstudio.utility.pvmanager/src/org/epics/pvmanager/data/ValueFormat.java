/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import org.epics.util.array.ArrayByte;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ArrayShort;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListShort;

/**
 * Formats a data type to a String representation. This class provide default
 * implementations that can format scalars and arrays to an arbitrary
 * precision and a maximum number of array elements.
 *
 * @author carcassi
 */
public abstract class ValueFormat extends Format {

    // Number format to be used to format primitive values
    private NumberFormat numberFormat;

    /**
     * Formats the given data object. For scalars and arrays redirects
     * to the appropriate methods. For anything else uses Object.toString().
     *
     * @param data data object to format
     * @return a String representation
     */
    @Override
    public StringBuffer format(Object data, StringBuffer toAppendTo, FieldPosition pos) {
        if (data == null)
            return toAppendTo;

        if (data instanceof Scalar)
            return format((Scalar) data, toAppendTo, pos);

        if (data instanceof Array)
            return format((Array) data, toAppendTo, pos);

        return toAppendTo.append(data);
    }

    /**
     * Formats an scalar.
     *
     * @param scalar data object to format
     * @return a String representation
     */
    public String format(Scalar scalar) {
        return format((Object) scalar);
    }

    /**
     * Formats an array.
     *
     * @param array data object to format
     * @return a String representation
     */
    public String format(Array array) {
        return format((Object) array);
    }

    /**
     * Formats a scalar.
     *
     * @param scalar data object to format
     * @param toAppendTo output buffer
     * @param pos the field position
     * @return the output buffer
     */
    protected abstract StringBuffer format(Scalar scalar, StringBuffer toAppendTo, FieldPosition pos);

    /**
     * Formats an array.
     *
     * @param array data object to format
     * @param toAppendTo output buffer
     * @param pos the field position
     * @return the output buffer
     */
    protected abstract StringBuffer format(Array array, StringBuffer toAppendTo, FieldPosition pos);

    /**
     * Returns the NumberFormat used to format the numeric values.
     * If null, it will use the NumberFormat from the value Display.
     *
     * @return a NumberFormat
     */
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    /**
     * Changes the NumberFormat used to format the numeric values.
     * If null, it will use the NumberFormat from the value Display.
     *
     * @param numberFormat a NumberFormat
     */
    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Object parseObject(String source, Object reference) {
        if (reference instanceof VDouble) {
            return parseDouble(source);
        }
        if (reference instanceof VFloat) {
            return parseFloat(source);
        }
        if (reference instanceof VInt) {
            return parseInt(source);
        }
        if (reference instanceof VShort) {
            return parseShort(source);
        }
        if (reference instanceof VByte) {
            return parseByte(source);
        }
        if (reference instanceof VString) {
            return parseString(source);
        }
        if (reference instanceof VEnum) {
            return parseEnum(source, ((VEnum) reference).getLabels());
        }
        if (reference instanceof VDoubleArray) {
            return parseDoubleArray(source);
        }
        if (reference instanceof VFloatArray) {
            return parseFloatArray(source);
        }
        if (reference instanceof VIntArray) {
            return parseIntArray(source);
        }
        if (reference instanceof VShortArray) {
            return parseShortArray(source);
        }
        if (reference instanceof VByteArray) {
            return parseByteArray(source);
        }
        if (reference instanceof VStringArray) {
            return parseStringArray(source);
        }
        if (reference instanceof VEnumArray) {
            return parseEnumArray(source, ((VEnumArray) reference).getLabels());
        }
        
        throw new IllegalArgumentException("Type " + ValueUtil.typeOf(reference) + " is not supported");
    }
    
    public double parseDouble(String source) {
        try {
            double value = Double.parseDouble(source);
            return value;
        } catch (NumberFormatException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    public float parseFloat(String source) {
        try {
            float value = Float.parseFloat(source);
            return value;
        } catch (NumberFormatException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    public int parseInt(String source) {
        try {
            int value = Integer.parseInt(source);
            return value;
        } catch (NumberFormatException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    public short parseShort(String source) {
        try {
            short value = Short.parseShort(source);
            return value;
        } catch (NumberFormatException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    public byte parseByte(String source) {
        try {
            byte value = Byte.parseByte(source);
            return value;
        } catch (NumberFormatException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    public String parseString(String source) {
        return source;
    }
    
    public int parseEnum(String source, List<String> labels) {
        int index = labels.indexOf(source);
        if (index != -1) {
            return index;
        }
        throw new RuntimeException(source  + " is not part of enum " + labels);
    }
    
    public ListDouble parseDoubleArray(String source) {
        String[] tokens = source.split(",");
        double[] values = new double[tokens.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = parseDouble(tokens[i].trim());
        }
        return new ArrayDouble(values);
    }
    
    public ListFloat parseFloatArray(String source) {
        String[] tokens = source.split(",");
        float[] values = new float[tokens.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = parseFloat(tokens[i].trim());
        }
        return new ArrayFloat(values);
    }
    
    public ListInt parseIntArray(String source) {
        String[] tokens = source.split(",");
        int[] values = new int[tokens.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = parseInt(tokens[i].trim());
        }
        return new ArrayInt(values);
    }
    
    public ListShort parseShortArray(String source) {
        String[] tokens = source.split(",");
        short[] values = new short[tokens.length];
        for (short i = 0; i < values.length; i++) {
            values[i] = parseShort(tokens[i].trim());
        }
        return new ArrayShort(values);
    }
    
    public ListByte parseByteArray(String source) {
        String[] tokens = source.split(",");
        byte[] values = new byte[tokens.length];
        for (byte i = 0; i < values.length; i++) {
            values[i] = parseByte(tokens[i].trim());
        }
        return new ArrayByte(values);
    }
    
    public List<String> parseStringArray(String source) {
        String[] tokens = source.split(",");
        List<String> values = new ArrayList<>();
        for (String token : tokens) {
            values.add(token.trim());
        }
        return values;
    }
    
    public ListInt parseEnumArray(String source, List<String> labels) {
        String[] tokens = source.split(",");
        int[] values = new int[tokens.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = parseEnum(tokens[i].trim(), labels);
        }
        return new ArrayInt(values);
    }

}
