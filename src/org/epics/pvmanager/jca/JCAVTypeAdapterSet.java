/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import org.epics.vtype.VFloat;
import org.epics.vtype.VInt;
import org.epics.vtype.VString;
import org.epics.vtype.VShort;
import org.epics.vtype.VEnum;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VByte;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VShortArray;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VDouble;
import org.epics.vtype.VFloatArray;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.epics.pvmanager.ValueCache;

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
    final static JCATypeAdapter DBRFloatToVFloat = new JCATypeAdapter(VFloat.class, DBR_TIME_Float.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VFloat createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VFloatFromDbr((DBR_TIME_Float) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };

    // DBR_TIME_Double -> VDouble
    final static JCATypeAdapter DBRDoubleToVDouble = new JCATypeAdapter(VDouble.class, DBR_TIME_Double.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VDouble createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VDoubleFromDbr((DBR_TIME_Double) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };
    
    // DBR_TIME_Byte -> VInt
    final static JCATypeAdapter DBRByteToVByte = new JCATypeAdapter(VByte.class, DBR_TIME_Byte.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VByte createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VByteFromDbr((DBR_TIME_Byte) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };
    
    // DBR_TIME_Short -> VInt
    final static JCATypeAdapter DBRShortToVShort = new JCATypeAdapter(VShort.class, DBR_TIME_Short.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VShort createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VShortFromDbr((DBR_TIME_Short) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };

    // DBR_TIME_Int -> VInt
    final static JCATypeAdapter DBRIntToVInt = new JCATypeAdapter(VInt.class, DBR_TIME_Int.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            public VInt createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VIntFromDbr((DBR_TIME_Int) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };

    // DBR_TIME_String -> VString
    final static JCATypeAdapter DBRStringToVString = new JCATypeAdapter(VString.class, DBR_TIME_String.TYPE, null, false) {

            @Override
            public VString createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VStringFromDbr((DBR_TIME_String) value, connPayload);
            }
        };

    // DBR_TIME_String -> VString
    final static JCATypeAdapter DBRByteToVString = new JCATypeAdapter(VString.class, DBR_TIME_Byte.TYPE, null, null) {

            @Override
            public int match(ValueCache<?> cache, JCAConnectionPayload connPayload) {
                if (!connPayload.isLongString()) {
                    return 0;
                }
                
                return super.match(cache, connPayload);
            }

            @Override
            public VString createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VStringFromDbr((DBR_TIME_Byte) value, connPayload);
            }
        };

    // DBR_TIME_Enum -> VEnum
    final static JCATypeAdapter DBREnumToVEnum = new JCATypeAdapter(VEnum.class, DBR_TIME_Enum.TYPE, DBR_LABELS_Enum.TYPE, false) {

            @Override
            public VEnum createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VEnumFromDbr((DBR_TIME_Enum) value, (DBR_LABELS_Enum) metadata, connPayload);
            }
        };
    
    // DBR_TIME_Float -> VFloatArray
    final static JCATypeAdapter DBRFloatToVFloatArray = new JCATypeAdapter(VFloatArray.class, DBR_TIME_Float.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public VFloatArray createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VFloatArrayFromDbr((DBR_TIME_Float) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };
    
    // DBR_TIME_Double -> VDoubleArray
    final static JCATypeAdapter DBRDoubleToVDoubleArray = new JCATypeAdapter(VDoubleArray.class, DBR_TIME_Double.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public VDoubleArray createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VDoubleArrayFromDbr((DBR_TIME_Double) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };
    
    // DBR_TIME_Byte -> VByteArray
    final static JCATypeAdapter DBRByteToVByteArray = new JCATypeAdapter(VByteArray.class, DBR_TIME_Byte.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public int match(ValueCache<?> cache, JCAConnectionPayload connPayload) {
                if (connPayload.isLongString()) {
                    return 0;
                }
                
                return super.match(cache, connPayload);
            }
        
            @Override
            public VByteArray createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VByteArrayFromDbr((DBR_TIME_Byte) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };
    
    // DBR_TIME_Short -> VShortArray
    final static JCATypeAdapter DBRShortToVShortArray = new JCATypeAdapter(VShortArray.class, DBR_TIME_Short.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public VShortArray createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VShortArrayFromDbr((DBR_TIME_Short) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };
    
    // DBR_TIME_Int -> VIntArray
    final static JCATypeAdapter DBRIntToVIntArray = new JCATypeAdapter(VIntArray.class, DBR_TIME_Int.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            public VIntArray createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VIntArrayFromDbr((DBR_TIME_Int) value, (DBR_CTRL_Double) metadata, connPayload);
            }
        };
    
    // DBR_TIME_String -> VStringArray
    final static JCATypeAdapter DBRStringToVStringArray = new JCATypeAdapter(VStringArray.class, DBR_TIME_String.TYPE, null, true) {

            @Override
            public VStringArray createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload) {
                return new VStringArrayFromDbr((DBR_TIME_String) value, connPayload);
            }
        };

    private static final Set<JCATypeAdapter> converters;
    
    static {
        Set<JCATypeAdapter> newFactories = new HashSet<JCATypeAdapter>();
        // Add all SCALARs
        newFactories.add(DBRFloatToVFloat);
        newFactories.add(DBRDoubleToVDouble);
        newFactories.add(DBRByteToVByte);
        newFactories.add(DBRShortToVShort);
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
