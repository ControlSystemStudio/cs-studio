/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import static org.csstudio.vtype.pv.PV.logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;

import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.pv.BooleanArrayData;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVBooleanArray;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.pv.Scalar;
import org.epics.pvdata.pv.ScalarArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;

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
        String type = actual_struct.getStructure().getID();
        if (type.startsWith("epics:nt/"))
            type = type.substring(9);
        if (type.equals("NTScalar:1.0"))
            return decodeNTScalar(actual_struct);
        if (type.equals("NTEnum:1.0"))
            return new VTypeForEnum(actual_struct);
        if (type.equals("NTScalarArray:1.0"))
            return decodeNTArray(actual_struct);
        if (type.equals("NTNDArray:1.0"))
            return new VImageForNTNDArray(actual_struct);
        if (type.equals("NTTable:1.0"))
            return decodeNTTable(actual_struct);

        // Handle data that contains a "value", even though not marked as NT*
        final Field value_field = actual_struct.getStructure().getField("value");
        if (value_field instanceof Scalar)
            return decodeNTScalar(actual_struct);
        else if (value_field instanceof ScalarArray)
            return decodeNTArray(actual_struct);
        // TODO: not really sure how to handle arbitrary structures -- no solid use cases yet...
        else if (value_field instanceof Structure)
        {   // Structures with a value field: Treat the value as the value of a VTable
            try
            {
                return decodeAsTableValue(actual_struct.getStructureField("value"), new ArrayList<>());
            }
            catch (Exception ex)
            {
                logger.log(Level.WARNING, "Cannot decode struct, returning string", ex);
                // fall through
            }
        }

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

    /**
     * Decode table from NTTable
     * @param struct
     * @return
     * @throws Exception
     */
    private static VTable decodeNTTable(final PVStructure struct) throws Exception
    {
        final PVScalarArray labels_array = struct.getScalarArrayField("labels", ScalarType.pvString);
        final int labels_length = labels_array.getLength();
        final String [] labels_strings = new String [labels_length];
        convert.toStringArray(labels_array, 0, labels_length, labels_strings, 0);
        // Create a new list for column names (labels), because if any field is a structure,
        // labels need to be created for each element of the structure
        final List<String> names  = new ArrayList<>(Arrays.asList(labels_strings));

        final PVStructure value_struct = struct.getStructureField("value");

        return decodeAsTableValue(value_struct, names);
    }

    /**
     * Decode a PVStructure (like an NTTable "value" field) as a VTable.
     *
     * <p>Sub-structures (fields which are structures) are handled recursively. Their fields are added
     * to the table in the order used by PVStructure.getSubField(int).
     * Labels of sub-structure fields are represented as "sub-structure-label/sub-structure-field-name".
     * This holds for any level of (sub-)(sub-)...(sub-)sub-structure.
     * <p>The NTTable requirement that all "columns" of the table have the same number of "rows" -- that is,
     * each scalar field in the "value" structure has the same number of scalar elements -- is enforced by
     * throwing an Exception if it is violated. This method is slightly more forgiving, since it allows
     * some types of non-scalar fields.
     * @param value_struct Structure to decode
     * @param names Column names. If these are the top-level labels of the value structure, there should be
     *             as many names as there are fields. Otherwise, names should be an empty list. For each field,
     *             if the corresponding name is null or not in the list, the field name is used as its label. If
     *             the column name ends with a "slash" character ('/'), the field name is appended to it.
     * @return VTable representing the values of the structure
     * @throw Exception If the column length constraint is violated, or if a field's type is not supported
     */
    private static VTable decodeAsTableValue(final PVStructure value, final List<String> names) throws Exception
    {
        // Right now, an exception is thrown if scalar values with different lengths are encountered
        // in the same structure. For arbitrary structures, this might not be necessary.
        // It would be simple to add a parameter for ignoring the column lengths constraint.
        // However, this could cause exceptions when displaying the table.
        // For that reason, it might be good to pad the values with null, after first finding the lengths
        // of all columns and determining the maximum length.
        // On the other hand, since there isn't currently a well-defined case for displaying arbitrary
        // structures, there's no real need to implement this at the moment.
        final List<Class<?>> types = new ArrayList<>();
        final List<Object> values  = new ArrayList<>();
        int rowSize = -1;
        Stack<PVField> stack = new Stack<>();
        PVField fields [] = value.getPVFields();
        for (int i = fields.length; i > 0; )
            stack.push(fields[--i]);
        while (!stack.empty())
        {
            PVField field = stack.pop();
            int index = types.size();
            if (index >= names.size())
                names.add(field.getFieldName());
            else
            {
                String name = names.get(index);
                if (name == null)
                    names.set(index, field.getFieldName());
                else if (name.endsWith("/"))
                    names.set(index, name + field.getFieldName());
            }
            if (field instanceof PVScalar)
            {
                if (rowSize != 1 && rowSize >= 0)
                    throw new Exception("Table must have consistent row size");
                getTableTypeAndValue((PVScalar) field, types, values);
            }
            else if (field instanceof PVScalarArray)
            {
                if (getTableTypeAndValue((PVScalarArray) field, types, values) != rowSize && rowSize >= 0)
                    throw new Exception("Table must have consistent row size");
            }
            else if (field instanceof PVStructure)
            {
                PVField [] subfields = ((PVStructure) field).getPVFields();
                String name = names.get(index) + "/";
                names.set(index, name);
                names.addAll(index, Collections.nCopies(subfields.length-1, name));
                for (int i = subfields.length; i-- > 0; )
                    stack.push(subfields[i]);
            }
            else
            {
                throw new Exception(String.format("The field type %s is not supported as a VTable element", field.getField().getType()));
            }
            //TODO: other kinds of Field
        } //end while not empty
        return ValueFactory.newVTable(types, names, values);
    }

    private static void getTableTypeAndValue(PVScalar scalar, List<Class<?>> types, List<Object> values)
    {
        switch(scalar.getScalar().getScalarType())
        {
            case pvDouble:
                types.add(Double.TYPE);
                double double_value = convert.toDouble(scalar);
                values.add(new ArrayDouble(double_value));
                break;
            case pvFloat:
                types.add(Float.TYPE);
                float float_value = convert.toFloat(scalar);
                values.add(new ArrayFloat(float_value));
                break;
            case pvLong:
            case pvUInt:
            case pvULong:
                types.add(Long.TYPE);
                long long_value = convert.toLong(scalar);
                values.add(new ArrayLong(long_value));
                break;
            case pvUShort:
            case pvInt:
                types.add(Integer.TYPE);
                int int_value = convert.toInt(scalar);
                values.add(new ArrayInt(int_value));
                break;
            case pvUByte:
            case pvShort:
                types.add(Short.TYPE);
                short short_value = convert.toShort(scalar);
                values.add(new ArrayShort(short_value));
                break;
            case pvByte:
                types.add(Byte.TYPE);
                byte byte_value = convert.toByte(scalar);
                values.add(new org.diirt.util.array.ArrayByte(byte_value));
                break;
            case pvBoolean: //Table can't handle ArrayBoolean, so use List<Boolean> instead
                types.add(Boolean.TYPE);
                boolean bool_value = ((PVBoolean)scalar).get();
                values.add(Arrays.asList(bool_value));
                break;
            case pvString:
                types.add(String.class);
                String str_value = convert.toString(scalar);
                values.add(Arrays.asList(str_value));
                break;
            //default: //throw exception?
        }
    }

    private static int getTableTypeAndValue(PVScalarArray array, List<Class<?>> types, List<Object> values)
    {
        final int length = array.getLength();
        //int to<X>Array(PVScalarArray pv, int offset, int len, <X>[]to, int toOffset);
        switch(array.getScalarArray().getElementType())
        {
            case pvDouble:
                types.add(Double.TYPE);
                double [] double_value = new double [length];
                convert.toDoubleArray(array, 0, length, double_value, 0);
                values.add(new ArrayDouble(double_value));
                break;
            case pvFloat:
                types.add(Float.TYPE);
                float [] float_value = new float [length];
                convert.toFloatArray(array, 0, length, float_value, 0);
                values.add(new ArrayFloat(float_value));
                break;
            case pvLong:
            case pvUInt:
            case pvULong:
                types.add(Long.TYPE);
                long [] long_value = new long[length];
                convert.toLongArray(array, 0, length, long_value, 0);
                values.add(new ArrayLong(long_value));
                break;
            case pvUShort:
            case pvInt:
                types.add(Integer.TYPE);
                int [] int_value = new int [length];
                convert.toIntArray(array, 0, length, int_value, 0);
                values.add(new ArrayInt(int_value));
                break;
            case pvUByte:
            case pvShort:
                types.add(Short.TYPE);
                short [] short_value = new short [length];
                convert.toShortArray(array, 0, length, short_value, 0);
                values.add(new ArrayShort(short_value));
                break;
            case pvByte:
                types.add(Byte.TYPE);
                byte [] byte_value = new byte [length];
                convert.toByteArray(array, 0, length, byte_value, 0);
                values.add(new org.diirt.util.array.ArrayByte(byte_value));
                break;
            case pvBoolean:
                types.add(Boolean.TYPE);
                //No Convert method for boolean. Have to do it the hard way.
                boolean [] bool_value = getArray((PVBooleanArray)array, length);
                List<Boolean> value = new ArrayList<Boolean>(length);
                for (boolean bool : bool_value)
                    value.add(bool);
                values.add(value);
                break;
            case pvString:
                types.add(String.class);
                String [] str_value = new String [length];
                convert.toStringArray(array, 0, length, str_value, 0);
                values.add(Arrays.asList(str_value));
                break;
            //default: //throw exception?
        }
        return length;
    }

    //based off double [] getArray(PVDoubleArray) example from pvDataJava documentation
    private static boolean[] getArray(PVBooleanArray pv, final int len)
    {
        boolean[] storage = new boolean[len];
        BooleanArrayData data = new BooleanArrayData();
        int offset = 0;
        while(offset < len) {
            int num = pv.get(offset,(len-offset),data);
            System.arraycopy(data.data,data.offset,storage,offset,num);
            offset += num;
        }
        return storage;
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
