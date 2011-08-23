/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.epics.pvmanager.Function;

/**
 *
 * @author carcassi
 */
public class VTableAggregationFunction extends Function<VTable> {
    
    private final List<List<Function<?>>> functions;
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
                ((String[]) array)[pos] = ((VString) value).getValue();
            }
        });
        arrayAdders.put(Double.TYPE, new ArrayAdder() {

            @Override
            @SuppressWarnings("unchecked")
            public void addValue(Object array, int pos, Object value) {
                ((double[]) array)[pos] = ((Scalar<Number>) value).getValue().doubleValue();
            }
        });
        arrayAdders.put(Integer.TYPE, new ArrayAdder() {

            @Override
            @SuppressWarnings("unchecked")
            public void addValue(Object array, int pos, Object value) {
                ((int[]) array)[pos] = ((Scalar<Number>) value).getValue().intValue();
            }
        });
    }

    public VTableAggregationFunction(List<List<Function<?>>> functions, List<String> names) {
        this.functions = functions;
        this.names = names;
    }

    @Override
    public VTable getValue() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        List<Object> values = new ArrayList<Object>();
        
        for (List<Function<?>> columnFunctions : functions) {
            List<Object> columnValues = new ArrayList<Object>();
            Class<?> columnType = null;
            
            // Extract all values and determine column type
            for (Function<?> function : columnFunctions) {
                Object value = function.getValue();
                columnType = validateType(value, columnType);
                
                // Types don't match
                if (columnType == null) {
                    throw new RuntimeException("Values for column " + names.get(types.size()) + " are not all of the same valid column type (currently only VString, VDouble and VInt).");
                }
                
                columnValues.add(value);
            }
            
            // Prepare column array
            Object array = java.lang.reflect.Array.newInstance(columnType, columnValues.size());
            for (int i = 0; i < columnValues.size(); i++) {
                arrayAdders.get(columnType).addValue(array, i, columnValues.get(i));
            }
            
            // Done with this column
            types.add(columnType);
            values.add(array);
        }
        
        return new IVTable(types, names, values);
    }
    
    private static interface ArrayAdder {
        void addValue(Object array, int pos, Object value);
    }
    
    private Class<?> validateType(Object value, Class<?> oldType) {
        // Type of the final array
        Class<?> newType = typeConversion.get(ValueUtil.typeOf(value));
        if (oldType == null)
            return newType;
        
        if (newType == null)
            return null;
        
        if (newType.equals(oldType))
            return oldType;
        
        // Convert integers to double if mixed column
        if (newType.equals(Double.class) && oldType.equals(Integer.class))
            return newType;
        if (newType.equals(Integer.class) && oldType.equals(Double.class))
            return oldType;
        
        return null;
    }
    
}
