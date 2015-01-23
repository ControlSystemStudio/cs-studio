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

import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Scalar;
import org.epics.pvdata.pv.ScalarArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

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
     *  @param field Specific field to read, i.e. don't go by NT* type
     *  @return {@link VType} for data in the structure
     *  @throws Exception on error
     */
    public static VType getVType(final PVStructure orig_struct, final int value_offset) throws Exception
    {
        final PVStructure struct;

        if (value_offset <= 0)
            struct = orig_struct;
        else
        {   // Extract field from struct
            struct = orig_struct.getSubField(PVStructure.class, value_offset);
            if (struct == null)
                throw new Exception("Cannot locate field offset " + value_offset + " in " + orig_struct);
        }

        // Handle normative types
        final String type = struct.getStructure().getID();
        if (type.equals("epics:nt/NTScalar:1.0"))
            return decodeNTScalar(struct);
        if (type.equals("epics:nt/NTEnum:1.0"))
            return new VTypeForEnum(struct);
        if (type.equals("epics:nt/NTScalarArray:1.0"))
            return decodeNTArray(struct);

        // Handle data that contains a "value", even though not marked as NT*
        final Field value_field = struct.getStructure().getField("value");
        if (value_field instanceof Scalar)
            return decodeNTScalar(struct);
        else if (value_field instanceof ScalarArray)
            return decodeNTArray(struct);

        // Create string that indicates name of unknown type
        return ValueFactory.newVString(struct.getStructure().toString(),
                ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "Unknown type"),
                ValueFactory.timeNow());
    }

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
