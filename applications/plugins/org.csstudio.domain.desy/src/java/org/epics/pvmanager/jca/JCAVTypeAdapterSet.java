/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.Channel;
import gov.aps.jca.dbr.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.*;

/**
 *
 * @author carcassi
 */
public class JCAVTypeAdapterSet implements JCATypeAdapterSet {
    
    @Override
    public Set<JCATypeAdapter> getAdapters() {
        return converters;
    }
    
    // DBR_TIME_Float -> VDouble
    final static JCATypeAdapter DBRFloatToVDouble = new JCATypeAdapter(VDouble.class, DBR_TIME_Float.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VDouble createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VDoubleFromDbr((DBR_TIME_Double) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };

    // DBR_TIME_Double -> VDouble
    final static JCATypeAdapter DBRDoubleToVDouble = new JCATypeAdapter(VDouble.class, DBR_TIME_Double.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VDouble createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VDoubleFromDbr((DBR_TIME_Double) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };
    
    // DBR_TIME_Byte -> VInt
    final static JCATypeAdapter DBRByteToVInt = new JCATypeAdapter(VInt.class, DBR_TIME_Byte.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VInt createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VIntFromDbr((DBR_TIME_Int) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };
    
    // DBR_TIME_Short -> VInt
    final static JCATypeAdapter DBRShortToVInt = new JCATypeAdapter(VInt.class, DBR_TIME_Short.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VInt createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VIntFromDbr((DBR_TIME_Int) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };

    // DBR_TIME_Int -> VInt
    final static JCATypeAdapter DBRIntToVInt = new JCATypeAdapter(VInt.class, DBR_TIME_Int.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VInt createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VIntFromDbr((DBR_TIME_Int) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };

    // DBR_TIME_String -> VString
    final static JCATypeAdapter DBRStringToVString = new JCATypeAdapter(VString.class, DBR_TIME_String.TYPE, null, false) {

            @Override
            public VString createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VStringFromDbr((DBR_TIME_String) value, disconnected);
            }
        };

    // DBR_TIME_String -> VString
    final static JCATypeAdapter DBRByteToVString = new JCATypeAdapter(VString.class, DBR_TIME_Byte.TYPE, null, null) {

            @Override
            public int match(ValueCache<?> cache, Channel channel) {
                if (!longStringPattern.matcher(channel.getName()).matches()) {
                    return 0;
                }
                
                return super.match(cache, channel);
            }

            @Override
            public VString createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VStringFromDbr((DBR_TIME_Byte) value, disconnected);
            }
        };

    // DBR_TIME_Enum -> VEnum
    final static JCATypeAdapter DBREnumToVEnum = new JCATypeAdapter(VEnum.class, DBR_TIME_Enum.TYPE, DBR_LABELS_Enum.TYPE, false) {

            @Override
            public VEnum createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VEnumFromDbr((DBR_TIME_Enum) value, (DBR_LABELS_Enum) metadata, disconnected);
            }
        };
    
    // DBR_TIME_Float -> VFloatArray
    final static JCATypeAdapter DBRFloatToVFloatArray = new JCATypeAdapter(VFloatArray.class, DBR_TIME_Float.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public VFloatArray createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VFloatArrayFromDbr((DBR_TIME_Float) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };
    
    // DBR_TIME_Double -> VDoubleArray
    final static JCATypeAdapter DBRDoubleToVDoubleArray = new JCATypeAdapter(VDoubleArray.class, DBR_TIME_Double.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public VDoubleArray createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VDoubleArrayFromDbr((DBR_TIME_Double) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };
    
    // DBR_TIME_Byte -> VByteArray
    final static JCATypeAdapter DBRByteToVByteArray = new JCATypeAdapter(VByteArray.class, DBR_TIME_Byte.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public int match(ValueCache<?> cache, Channel channel) {
                if (longStringPattern.matcher(channel.getName()).matches()) {
                    return 0;
                }
                
                return super.match(cache, channel);
            }
        
            @Override
            public VByteArray createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VByteArrayFromDbr((DBR_TIME_Byte) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };
    
    // DBR_TIME_Short -> VShortArray
    final static JCATypeAdapter DBRShortToVShortArray = new JCATypeAdapter(VShortArray.class, DBR_TIME_Short.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public VShortArray createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VShortArrayFromDbr((DBR_TIME_Short) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };
    
    // DBR_TIME_Int -> VIntArray
    final static JCATypeAdapter DBRIntToVIntArray = new JCATypeAdapter(VIntArray.class, DBR_TIME_Int.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public VIntArray createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VIntArrayFromDbr((DBR_TIME_Int) value, (DBR_CTRL_Double) metadata, disconnected);
            }
        };
    
    // DBR_TIME_String -> VStringArray
    final static JCATypeAdapter DBRStringToVStringArray = new JCATypeAdapter(VStringArray.class, DBR_TIME_String.TYPE, null, true) {

            @Override
            public VStringArray createValue(DBR value, DBR metadata, boolean disconnected) {
                return new VStringArrayFromDbr((DBR_TIME_String) value, disconnected);
            }
        };

    private static final Set<JCATypeAdapter> converters;
    
    static Pattern longStringPattern = Pattern.compile(".+\\..*\\$.*");
    
    static {
        Set<JCATypeAdapter> newFactories = new HashSet<JCATypeAdapter>();
        // Add all SCALARs
        newFactories.add(DBRFloatToVDouble);
        newFactories.add(DBRDoubleToVDouble);
        newFactories.add(DBRByteToVInt);
        newFactories.add(DBRShortToVInt);
        newFactories.add(DBRIntToVInt);
        newFactories.add(DBRStringToVString);
        newFactories.add(DBRByteToVString);
        newFactories.add(DBREnumToVEnum);

        // Add all ARRAYs
        newFactories.add(DBRFloatToVFloatArray);
        newFactories.add(DBRDoubleToVDoubleArray);
        newFactories.add(DBRByteToVByteArray);
        newFactories.add(DBRShortToVShortArray);
        newFactories.add(DBRIntToVIntArray);
        newFactories.add(DBRStringToVStringArray);
        converters = Collections.unmodifiableSet(newFactories);
    }
    
}
