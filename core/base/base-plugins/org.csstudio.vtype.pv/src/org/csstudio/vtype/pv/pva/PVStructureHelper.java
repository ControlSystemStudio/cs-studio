/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import java.util.Arrays;
import java.util.List;

import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VImage;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVByteArray;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.pv.Scalar;
import org.epics.pvdata.pv.ScalarArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.StructureArrayData;

/** Helper for reading & writing PVStructure
 *
 *  <p>Based on ideas from org.epics.pvmanager.pva
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class PVStructureHelper
{
    final public static Convert convert = ConvertFactory.getConvert();

    /** @param struct {@link PVStructure} to read
     *  @param value_offset Specific field to read
     *  @return {@link VType} for field in the structure
     *  @throws Exception on error
     */
    public static VType getVType(final PVStructure struct, final int value_offset) throws Exception
    {
        final PVStructure actual_struct;

        if (value_offset <= 0)
            actual_struct = struct;
        else
        {
            // Extract field from struct
            final PVField field;
            try
            {
                field = struct.getSubField(value_offset);
            }
            catch (Exception ex)
            {
                throw new Exception("Cannot decode field offset " + value_offset + " in " + struct, ex);
            }
            if (field instanceof PVStructure)
                actual_struct = (PVStructure) field;
            else if (field instanceof PVScalar)
                return decodeScalar((PVScalar) field);
            else if (field instanceof PVScalarArray)
                return decodeArray((PVScalarArray) field);
            else if (field instanceof PVUnion)
                return decodeUnion((PVUnion) field);
            else
                throw new Exception("Cannot decode " + field + " in " + struct);
        }

        // Handle normative types
        final String type = actual_struct.getStructure().getID();
        if (type.equals("epics:nt/NTScalar:1.0"))
            return decodeNTScalar(actual_struct);
        if (type.equals("epics:nt/NTEnum:1.0"))
            return new VTypeForEnum(actual_struct);
        if (type.equals("epics:nt/NTScalarArray:1.0"))
            return decodeNTArray(actual_struct);
        if (type.equals("epics:nt/NTNDArray:1.0"))
            return decodeNTNDArray(actual_struct);

        // Handle data that contains a "value", even though not marked as NT*
        final Field value_field = actual_struct.getStructure().getField("value");
        if (value_field instanceof Scalar)
            return decodeNTScalar(actual_struct);
        else if (value_field instanceof ScalarArray)
            return decodeNTArray(actual_struct);

        // Create string that indicates name of unknown type
        return ValueFactory.newVString(actual_struct.getStructure().toString(),
                ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "Unknown type"),
                ValueFactory.timeNow());
    }

    /** Attempt to decode a scalar {@link VType}
     *  @param field {@link PVScalar}
     *  @return Value
     *  @throws Exception on error decoding the scalar
     */
    private static VType decodeScalar(final PVScalar field) throws Exception
    {
        final ScalarType type = field.getScalar().getScalarType();
        switch (type)
        {
        case pvDouble:
            return ValueFactory.newVDouble(convert.toDouble(field));
        case pvFloat:
            return ValueFactory.newVFloat(convert.toFloat(field), ValueFactory.alarmNone(),
                    ValueFactory.timeNow(), ValueFactory.displayNone());
        case pvLong:
        case pvUInt: // Update UInt to Long
        case pvULong: // Keep ULong as Long
            return ValueFactory.newVLong(convert.toLong(field), ValueFactory.alarmNone(),
                                         ValueFactory.timeNow(), ValueFactory.displayNone());
        case pvInt:
        case pvUShort: // Update UShort to Int
            return ValueFactory.newVInt(convert.toInt(field), ValueFactory.alarmNone(),
                    ValueFactory.timeNow(), ValueFactory.displayNone());
        case pvShort:
        case pvUByte: // Update UByte to Short
            return ValueFactory.newVShort(convert.toShort(field), ValueFactory.alarmNone(),
                                          ValueFactory.timeNow(), ValueFactory.displayNone());
        case pvByte:
            return ValueFactory.newVByte(convert.toByte(field), ValueFactory.alarmNone(),
                                         ValueFactory.timeNow(), ValueFactory.displayNone());
        case pvBoolean:
            return ValueFactory.newVBoolean(convert.toInt(field) != 0, ValueFactory.alarmNone(),
                                            ValueFactory.timeNow());
        case pvString:
            return ValueFactory.newVString(convert.toString(field), ValueFactory.alarmNone(),
                                           ValueFactory.timeNow());
        default:
            throw new Exception("Cannot handle " + type.name());
        }
    }

    /** Attempt to decode an array {@link VType}
     *  @param pv_array {@link PVScalarArray}
     *  @return Value
     *  @throws Exception on error decoding the array
     */
    private static VType decodeArray(final PVScalarArray pv_array) throws Exception
    {
        final Field field = pv_array.getField();
        if (! (field instanceof ScalarArray))
            return null;
        final ScalarType type = ((ScalarArray) field).getElementType();
        final int length = pv_array.getLength();
        switch (type)
        {
        case pvDouble:
        {
            final double[] data = new double[length];
            PVStructureHelper.convert.toDoubleArray(pv_array, 0, length, data, 0);
            return ValueFactory.newVDoubleArray(new ArrayDouble(data), ValueFactory.alarmNone(),
                                                ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        case pvFloat:
        {
            final float[] data = new float[length];
            PVStructureHelper.convert.toFloatArray(pv_array, 0, length, data, 0);
            return ValueFactory.newVFloatArray(new ArrayFloat(data), ValueFactory.alarmNone(),
                    ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        case pvLong:
        case pvULong:
        case pvUInt:
        {
            final long[] data = new long[length];
            PVStructureHelper.convert.toLongArray(pv_array, 0, length, data, 0);
            return ValueFactory.newVLongArray(new ArrayLong(data), ValueFactory.alarmNone(),
                                              ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        case pvInt:
        case pvUShort:
        {
            final int[] data = new int[length];
            PVStructureHelper.convert.toIntArray(pv_array, 0, length, data, 0);
            return ValueFactory.newVIntArray(new ArrayInt(data), ValueFactory.alarmNone(),
                    ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        case pvShort:
        case pvByte: // There is no ValueFactory.newVByteArray, so upgrade to short
        case pvUByte:
        {
            final short[] data = new short[length];
            PVStructureHelper.convert.toShortArray(pv_array, 0, length, data, 0);
            return ValueFactory.newVShortArray(new ArrayShort(data), ValueFactory.alarmNone(),
                                               ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        // There is no convert.toBoolArray(), and pvaSrv example has no boolArray01 example to test
//        case pvBoolean:
//        {
//            final boolean[] data = new boolean[length];
//            PVStructureHelper.convert.toBoolArray(pv_array, 0, length, data, 0);
//            return ValueFactory.newVBooleanArray(new ArrayBoolean(data), ValueFactory.alarmNone(),
//                                                 ValueFactory.timeNow());
//        }
        case pvString:
        {
            final String[] data = new String[length];
            PVStructureHelper.convert.toStringArray(pv_array, 0, length, data, 0);
            return ValueFactory.newVStringArray(Arrays.asList(data), ValueFactory.alarmNone(),
                                                ValueFactory.timeNow());
        }
        default:
            throw new Exception("Cannot handle " + type.name());
        }
    }

    /** Attempt to decode a {@link VType} from a union
     *  @param pv_union {@link PVUnion}
     *  @return Value
     *  @throws Exception on error decoding the array
     */
    private static VType decodeUnion(final PVUnion pv_union) throws Exception
    {
        final PVField value = pv_union.get();
        if (value instanceof PVScalar)
            return decodeScalar((PVScalar) value);
        if (value instanceof PVScalarArray)
            return decodeArray((PVScalarArray) value);
        throw new Exception("Canot decode union from " + value);
    }

    /** Decode 'value', 'timeStamp', 'alarm' of NTScalar
     *  @param struct
     *  @return
     *  @throws Exception
     */
    private static VType decodeNTScalar(final PVStructure struct) throws Exception
    {
        final PVScalar field = struct.getSubField(PVScalar.class, "value");
        if (field == null)
            throw new Exception("Expected struct with scalar 'value', got " + struct);
        final ScalarType type = field.getScalar().getScalarType();
        switch (type)
        {
        case pvDouble:
            return new VTypeForDouble(struct);
        case pvFloat:
            return new VTypeForFloat(struct);
        case pvInt:
        case pvUInt:
            return new VTypeForInt(struct);
        case pvLong:
        case pvULong:
            return new VTypeForLong(struct);
        case pvString:
            return new VTypeForString(struct);
        case pvShort:
        case pvUShort:
            return new VTypeForShort(struct);
        case pvByte:
        case pvUByte:
            return new VTypeForByte(struct);
        default:
            return ValueFactory.newVString(struct.getStructure().toString(),
                    ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "Unknown scalar type"),
                    ValueFactory.timeNow());
        }
    }

    /** Decode 'value', 'timeStamp', 'alarm' of NTArray
     *  @param struct
     *  @return
     *  @throws Exception
     */
    private static VType decodeNTArray(final PVStructure struct) throws Exception
    {
        final Field field = struct.getStructure().getField("value");
        if (! (field instanceof ScalarArray)) // Also handles field == null
            throw new Exception("Expected struct with scalar array 'value', got " + struct);
        final ScalarType type = ((ScalarArray) field).getElementType();
        switch (type)
        {
        case pvDouble:
            return new VTypeForDoubleArray(struct);
        case pvFloat:
            return new VTypeForFloatArray(struct);
        case pvInt:
        case pvUInt:
            return new VTypeForIntArray(struct);
        case pvLong:
        case pvULong:
            return new VTypeForLongArray(struct);
        case pvShort:
        case pvUShort:
            return new VTypeForShortArray(struct);
        case pvByte:
        case pvUByte:
            return new VTypeForByteArray(struct);
        case pvString:
            return new VTypeForStringArray(struct);
        default:
            return ValueFactory.newVString(struct.getStructure().toString(),
                    ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "Unknown scalar type"),
                    ValueFactory.timeNow());
        }
    }

    /** Decode image from NTNDArray
     *  @param struct
     *  @return
     *  @throws Exception
     */
    private static VImage decodeNTNDArray(final PVStructure struct) throws Exception
    {
        final PVStructureArray dim_field = struct.getSubField(PVStructureArray.class, "dimension");
        if (dim_field == null  ||  dim_field.getLength() < 2)
            throw new Exception("Need at least 2 dimensions, got " + dim_field);
        final StructureArrayData dim = new StructureArrayData();
        dim_field.get(0, 2, dim);
        // Could use dim.data[0].getSubField(PVInt.class, 1).get(),
        // but fetching by field name in case structure changes
        final int width = dim.data[0].getIntField("size").get();
        final int height = dim.data[1].getIntField("size").get();
        final int size = width * height;

        final PVUnion value_field = struct.getUnionField("value");
        final PVField value = value_field.get();
        if (! (value instanceof PVScalarArray))
            throw new Exception("Expected array for NTNDArray 'value', got " + value);

        final byte[] data = new byte[size];
        if (value instanceof PVByteArray)
            PVStructureHelper.convert.toByteArray((PVByteArray)value, 0, size, data, 0);
        else
            throw new Exception("Cannot extract byte[] from " + value);

        return ValueFactory.newVImage(height, width, data);
    }

    /** @param structure {@link PVStructure} from which to read
     *  @param name Name of a field in that structure
     *  @param default Value Value to use if field does not exist
     *  @return Number found in field or default
     */
    public static Double getDoubleValue(final PVStructure structure, final String name, final Double defaultValue)
    {
        final PVScalar field = structure.getSubField(PVScalar.class, name);
        if (field != null)
            return convert.toDouble(field);
        else
            return defaultValue;
    }

    /** @param structure {@link PVStructure} from which to read
     *  @param name Name of a field in that structure
     *  @return Array of strings
     *  @throws Exception on error
     */
    public static List<String> getStrings(final PVStructure structure, final String name) throws Exception
    {
        final PVStringArray choices = structure.getSubField(PVStringArray.class, name);
        final int length = choices.getLength();
        final String[] labels = new String[length];
        convert.toStringArray(choices, 0, length, labels, 0);
        return Arrays.asList(labels);
    }

    /** @param field {@link PVField} to write
     *  @param new_value Value to write
     *  @throws Exception on error
     */
    public static void setField(final PVField field, final Object new_value) throws Exception
    {
        if (field instanceof PVScalar)
        {
            final PVScalar scalar = (PVScalar)field;
            if (new_value instanceof Double  ||
                new_value instanceof Float)
                convert.fromDouble(scalar, ((Number)new_value).doubleValue());
            else if (new_value instanceof Number) // Int, short, byte
                convert.fromLong(scalar, ((Number)new_value).longValue());
            else if (new_value instanceof String)
                convert.fromString(scalar, (String) new_value);
            else if (new_value instanceof Boolean)
            {
                if (! (scalar instanceof PVBoolean))
                    throw new Exception("Cannot set " + scalar.getClass().getName() + " to boolean");
                ((PVBoolean) scalar).put((Boolean) new_value);
            }
            else
                throw new Exception("Cannot set " + scalar.getClass().getName() + " " + new_value);
        }
        else
            throw new Exception("Cannot set " + field.getClass().getName());
    }
}
