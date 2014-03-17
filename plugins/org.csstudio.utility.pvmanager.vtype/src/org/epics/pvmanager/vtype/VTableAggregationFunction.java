/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.vtype;

import org.epics.vtype.VString;
import org.epics.vtype.ValueUtil;
import org.epics.vtype.VInt;
import org.epics.vtype.VTable;
import org.epics.vtype.Scalar;
import org.epics.vtype.VDouble;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.epics.pvmanager.ReadFunction;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.vtype.ValueFactory;

/**
 *
 * @author carcassi
 */
class VTableAggregationFunction implements ReadFunction<VTable> {
    
    private final List<List<ReadFunction<?>>> functions;
    private final List<String> names;
    private static final Map<Class<?>, Class<?>> typeConversion;
    private static final Map<Class<?>, ArrayAdder> arrayAdders;
    
    static {
        typeConversion = new HashMap<Class<?>, Class<?>>();
        typeConversion.put(VString.class, String.class);
        typeConversion.put(VDouble.class, Double.TYPE);
        typeConversion.put(VInt.class, Integer.TYPE);
        
        arrayAdders = new HashMap<Class<?>, ArrayAdder>();
        arrayAdders.put(String.class, new ArrayAdder() {

            @Override
            public void addValue(Object array, int pos, Object value) {
                if (value != null)
                   ((String[]) array)[pos] = ((VString) value).getValue();
            }

            @Override
            public Object finalizeData(Object data) {
                return Arrays.asList((String[]) data);
            }
        });
        arrayAdders.put(Double.TYPE, new ArrayAdder() {

            @Override
            @SuppressWarnings("unchecked")
            public void addValue(Object array, int pos, Object value) {
                double converted = Double.NaN;
                if (value != null)
                    converted = ((Number) ((Scalar) value).getValue()).doubleValue();
                ((double[]) array)[pos] = converted;
            }

            @Override
            public Object finalizeData(Object data) {
                return new ArrayDouble((double[]) data);
            }
        });
        arrayAdders.put(Integer.TYPE, new ArrayAdder() {

            @Override
            @SuppressWarnings("unchecked")
            public void addValue(Object data, int pos, Object value) {
                int converted = 0;
                if (value != null)
                    converted = ((Number) ((Scalar) value).getValue()).intValue();
                ((int[]) data)[pos] = converted;
            }

            @Override
            public Object finalizeData(Object data) {
                return new ArrayInt((int[]) data);
            }
            
        });
    }

    public VTableAggregationFunction(List<List<ReadFunction<?>>> functions, List<String> names) {
        this.functions = functions;
        this.names = names;
    }

    @Override
    public VTable readValue() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        List<Object> values = new ArrayList<Object>();
        
        for (List<ReadFunction<?>> columnFunctions : functions) {
            List<Object> columnValues = new ArrayList<Object>();
            Class<?> columnType = null;
            
            // Extract all values and determine column type
            for (ReadFunction<?> function : columnFunctions) {
                Object value = function.readValue();
                columnType = validateType(value, columnType, names.get(types.size()));
                
                columnValues.add(value);
            }
            
            // If no type is found, the column will be empty.
            // Default to an array of Strings
            if (columnType == null)
                columnType = String.class;
            
            // Prepare column array
            Object data = java.lang.reflect.Array.newInstance(columnType, columnValues.size());
            for (int i = 0; i < columnValues.size(); i++) {
                arrayAdders.get(columnType).addValue(data, i, columnValues.get(i));
            }
            
            // Done with this column
            types.add(columnType);
            values.add(arrayAdders.get(columnType).finalizeData(data));
        }
        
        return ValueFactory.newVTable(types, names, values);
    }
    
    private static interface ArrayAdder {
        void addValue(Object data, int pos, Object value);
        Object finalizeData(Object data);
    }
    
    private Class<?> validateType(Object value, Class<?> oldType, String columnName) {
        if (value == null)
            return oldType;
        
        // Type of the final array
        Class<?> newType = typeConversion.get(ValueUtil.typeOf(value));
        if (oldType == null)
            return newType;
        
        if (newType != null) {
            if (newType.equals(oldType))
                return oldType;

            // Convert integers to double if mixed column
            if (newType.equals(Double.TYPE) && oldType.equals(Integer.TYPE))
                return newType;
            if (newType.equals(Integer.TYPE) && oldType.equals(Double.TYPE))
                return oldType;
        }
        
                
        // Types don't match
        throw new RuntimeException("Values for column " + columnName + " are not all of the same valid column type: can't convert "
                + value.getClass().getSimpleName() + " to " + oldType.getSimpleName() + " - currently only VString, VDouble and VInt).");
    }
    
}
