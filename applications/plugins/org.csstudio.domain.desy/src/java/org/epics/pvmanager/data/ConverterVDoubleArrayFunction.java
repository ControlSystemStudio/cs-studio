/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import org.epics.pvmanager.Function;

/**
 * Converts numeric types to VDouble.
 *
 * @author carcassi
 */
class ConverterVDoubleArrayFunction extends Function<VDoubleArray> {
    
    private final Function<?> argument;

    /**
     * Creates a new converter from the given function.
     * 
     * @param argument the argument function
     */
    public ConverterVDoubleArrayFunction(Function<?> argument) {
        this.argument = argument;
    }

    @Override
    public VDoubleArray getValue() {
        Object value = argument.getValue();
        if (value instanceof VDoubleArray) {
            return (VDoubleArray) value;
        }
        
        // Converts VFloatArray to VDoubleArray
        if (value instanceof VFloatArray) {
            final VFloatArray vFloat = (VFloatArray) value;
            float[] input = vFloat.getArray();
            double[] output = new double[input.length];
            for (int i = 0; i < output.length; i++) {
                output[i] = input[i];
            }
            return ValueFactory.newVDoubleArray(output, vFloat.getSizes(), vFloat,
                vFloat, vFloat);
        }
        
        // Converts VIntArray to VDoubleArray
        if (value instanceof VIntArray) {
            final VIntArray vInt = (VIntArray) value;
            int[] input = vInt.getArray();
            double[] output = new double[input.length];
            for (int i = 0; i < output.length; i++) {
                output[i] = input[i];
            }
            return ValueFactory.newVDoubleArray(output, vInt.getSizes(), vInt,
                vInt, vInt);
        }
        
        // Converts VShortArray to VDoubleArray
        if (value instanceof VShortArray) {
            final VShortArray vShort = (VShortArray) value;
            short[] input = vShort.getArray();
            double[] output = new double[input.length];
            for (int i = 0; i < output.length; i++) {
                output[i] = input[i];
            }
            return ValueFactory.newVDoubleArray(output, vShort.getSizes(), vShort,
                vShort, vShort);
        }
        
        // Converts VByteArray to VDoubleArray
        if (value instanceof VByteArray) {
            final VByteArray vByte = (VByteArray) value;
            byte[] input = vByte.getArray();
            double[] output = new double[input.length];
            for (int i = 0; i < output.length; i++) {
                output[i] = input[i];
            }
            return ValueFactory.newVDoubleArray(output, vByte.getSizes(), vByte,
                vByte, vByte);
        }
        
        // No convertion available
        throw new UnsupportedOperationException("Cannot convert a " + value.getClass().getSimpleName() + " to a VDoubleArray.");
    }
    
}
