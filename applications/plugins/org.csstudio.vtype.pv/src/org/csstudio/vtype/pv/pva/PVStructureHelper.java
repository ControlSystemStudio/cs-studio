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
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** Helper for reading & writing PVStructure
 * 
 *  <p>Based on ideas from org.epics.pvmanager.pva
 *  @author Kay Kasemir
 */
class PVStructureHelper
{
    final public static Convert convert = ConvertFactory.getConvert();

    /** @param struct {@link PVStructure} to read
     *  @return {@link VType} for data in the structure
     *  @throws Exception on error
     */
    public static VType getVType(final PVStructure struct) throws Exception
    {
        final String type = struct.getStructure().getID();
        if (type.equals("uri:ev4:nt/2012/pwd:NTScalar"))
            return decodeNTScalar(struct);
        if (type.equals("uri:ev4:nt/2012/pwd:NTEnum"))
            return new VTypeForEnum(struct);
        return ValueFactory.newVString(type,
                ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "Unknown type"),
                ValueFactory.timeNow());
    }

    private static VType decodeNTScalar(final PVStructure struct) throws Exception
    {
        final Field field = struct.getStructure().getField("value");
        if (! (field instanceof Scalar))
            throw new Exception("Expected Scalar value");
        final ScalarType type = ((Scalar) field).getScalarType();
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
            return ValueFactory.newVString(type.name(),
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
        final PVField field = structure.getSubField(name);
        if (field instanceof PVScalar)
            return convert.toDouble((PVScalar)field);
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
        final PVStringArray choices = (PVStringArray)
                structure.getScalarArrayField(name, ScalarType.pvString);
        int i=0, left = choices.getLength();
        StringArrayData label_text = new StringArrayData();
        // TODO Check result, call until left == 0?
        choices.get(i, left, label_text);
        return Arrays.asList(label_text.data);
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
