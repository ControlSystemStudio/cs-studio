/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_TIME_Byte;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import gov.aps.jca.dbr.DBR_TIME_Float;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.dbr.DBR_TIME_Short;
import gov.aps.jca.dbr.DBR_TIME_String;
import java.util.HashSet;
import java.util.Set;
import org.epics.pvmanager.data.VByteArray;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VFloatArray;
import org.epics.pvmanager.data.VInt;
import org.epics.pvmanager.data.VIntArray;
import org.epics.pvmanager.data.VShortArray;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VStringArray;

/**
 *
 * @author carcassi
 */
abstract class VTypeFactory<TValue, TEpicsValue, TEpicsMeta> {

    private final Class valueType;
    private final DBRType epicsValueType;
    private final DBRType epicsMetaType;
    private final boolean array;

    public VTypeFactory(Class<TValue> valueType, DBRType epicsValueType, DBRType epicsMetaType, boolean array) {
        this.valueType = valueType;
        this.epicsValueType = epicsValueType;
        this.epicsMetaType = epicsMetaType;
        this.array = array;
    }

    public Class getValueType() {
        return valueType;
    }

    public DBRType getEpicsMetaType() {
        return epicsMetaType;
    }

    public DBRType getEpicsValueType() {
        return epicsValueType;
    }

    public boolean isArray() {
        return array;
    }

    abstract TValue createValue(TEpicsValue value, TEpicsMeta metadata, boolean disconnected);
    
    private static Set<VTypeFactory<?, ?, ?>> factories = new HashSet<VTypeFactory<?, ?, ?>>();

    private static boolean dbrTypeMatch(DBRType aType, DBRType anotherType) {
        return aType.isBYTE() && anotherType.isBYTE() ||
                aType.isDOUBLE() && anotherType.isDOUBLE() ||
                aType.isENUM() && anotherType.isENUM() ||
                aType.isFLOAT() && anotherType.isFLOAT() ||
                aType.isINT() && anotherType.isINT() ||
                aType.isSHORT() && anotherType.isSHORT() ||
                aType.isSTRING() && anotherType.isSTRING();
    }

    private static boolean matchCount(int aCount, boolean isArray) {
        return aCount == 1 && !isArray ||
                aCount > 1 && isArray;
    }

    static VTypeFactory matchFor(Class<?> desiredType, DBRType dbrType, int elementCount) {
        for (VTypeFactory<?, ?, ?> vTypeFactory : factories) {
            if (desiredType.isAssignableFrom(vTypeFactory.getValueType()) &&
                    dbrTypeMatch(dbrType, vTypeFactory.getEpicsValueType()) &&
                    matchCount(elementCount, vTypeFactory.isArray())) {
                return vTypeFactory;
            }
        }
        throw new UnsupportedOperationException("Type " + desiredType.getName() + " is not assignable from " + dbrType.getName() + ", element count " + elementCount);
    }

    static {
        Set<VTypeFactory<?, ?, ?>> newFactories = new HashSet<VTypeFactory<?, ?, ?>>();
        // Add all SCALARs
        // DBR_TIME_Float -> VDouble
        newFactories.add(new VTypeFactory<VDouble, DBR_TIME_Float, DBR_CTRL_Double>(VDouble.class, DBR_TIME_Float.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            protected VDouble createValue(DBR_TIME_Float value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VDoubleFromDbr(value, metadata, disconnected);
            }
        });
        // DBR_CTRL_Double -> VDouble
        newFactories.add(new VTypeFactory<VDouble, DBR_TIME_Double, DBR_CTRL_Double>(VDouble.class, DBR_TIME_Double.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            protected VDouble createValue(DBR_TIME_Double value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VDoubleFromDbr(value, metadata, disconnected);
            }
        });
        // DBR_TIME_Byte -> VInt
        newFactories.add(new VTypeFactory<VInt, DBR_TIME_Byte, DBR_CTRL_Double>(VInt.class, DBR_TIME_Byte.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            protected VInt createValue(DBR_TIME_Byte value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VIntFromDbr(value, metadata, disconnected);
            }
        });
        // DBR_CTRL_Short -> VInt
        newFactories.add(new VTypeFactory<VInt, DBR_TIME_Short, DBR_CTRL_Double>(VInt.class, DBR_TIME_Short.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            protected VInt createValue(DBR_TIME_Short value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VIntFromDbr(value, metadata, disconnected);
            }
        });
        // DBR_CTRL_Int -> VInt
        newFactories.add(new VTypeFactory<VInt, DBR_TIME_Int, DBR_CTRL_Double>(VInt.class, DBR_TIME_Int.TYPE, DBR_CTRL_Double.TYPE, false) {

            @Override
            protected VInt createValue(DBR_TIME_Int value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VIntFromDbr(value, metadata, disconnected);
            }
        });
        newFactories.add(new VTypeFactory<VString, DBR_TIME_String, DBR_TIME_String>(VString.class, DBR_TIME_String.TYPE, null, false) {

            @Override
            protected VString createValue(DBR_TIME_String value, DBR_TIME_String metadata, boolean disconnected) {
                return new VStringFromDbr(value, disconnected);
            }
        });
        newFactories.add(new VTypeFactory<VEnum, DBR_TIME_Enum, DBR_LABELS_Enum>(VEnum.class, DBR_TIME_Enum.TYPE, DBR_LABELS_Enum.TYPE, false) {

            @Override
            protected VEnum createValue(DBR_TIME_Enum value, DBR_LABELS_Enum metadata, boolean disconnected) {
                return new VEnumFromDbr(value, metadata, disconnected);
            }
        });
        newFactories.add(new VTypeFactory<VDoubleArray, DBR_TIME_Double, DBR_CTRL_Double>(VDoubleArray.class, DBR_TIME_Double.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            protected VDoubleArray createValue(DBR_TIME_Double value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VDoubleArrayFromDbr(value, metadata, disconnected);
            }
        });
        newFactories.add(new VTypeFactory<VFloatArray, DBR_TIME_Float, DBR_CTRL_Double>(VFloatArray.class, DBR_TIME_Float.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            protected VFloatArray createValue(DBR_TIME_Float value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VFloatArrayFromDbr(value, metadata, disconnected);
            }
        });
        newFactories.add(new VTypeFactory<VByteArray, DBR_TIME_Byte, DBR_CTRL_Double>(VByteArray.class, DBR_TIME_Byte.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            protected VByteArray createValue(DBR_TIME_Byte value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VByteArrayFromDbr(value, metadata, disconnected);
            }
        });
        newFactories.add(new VTypeFactory<VShortArray, DBR_TIME_Short, DBR_CTRL_Double>(VShortArray.class, DBR_TIME_Short.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            protected VShortArray createValue(DBR_TIME_Short value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VShortArrayFromDbr(value, metadata, disconnected);
            }
        });
        newFactories.add(new VTypeFactory<VIntArray, DBR_TIME_Int, DBR_CTRL_Double>(VIntArray.class, DBR_TIME_Int.TYPE, DBR_CTRL_Double.TYPE, true) {

            @Override
            protected VIntArray createValue(DBR_TIME_Int value, DBR_CTRL_Double metadata, boolean disconnected) {
                return new VIntArrayFromDbr(value, metadata, disconnected);
            }
        });
        newFactories.add(new VTypeFactory<VStringArray, DBR_TIME_String, DBR_TIME_String>(VStringArray.class, DBR_TIME_String.TYPE, null, true) {

            @Override
            protected VStringArray createValue(DBR_TIME_String value, DBR_TIME_String metadata, boolean disconnected) {
                return new VStringArrayFromDbr(value, disconnected);
            }
        });
        factories = newFactories;
    }

}
