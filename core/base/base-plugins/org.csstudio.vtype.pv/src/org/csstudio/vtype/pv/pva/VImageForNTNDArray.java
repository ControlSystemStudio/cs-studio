/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import org.diirt.util.array.ArrayByte;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.VImage;
import org.diirt.vtype.VImageDataType;
import org.diirt.vtype.VImageType;
import org.epics.pvdata.pv.PVByteArray;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.pv.StructureArrayData;

/** VImage for a ListNumber
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class VImageForNTNDArray extends VTypeTimeAlarmBase implements VImage
{
    private final int width, height;
    private final ListNumber data;
    private final VImageDataType data_type;
    private final VImageType image_type;

    public VImageForNTNDArray(final PVStructure struct) throws Exception
    {
        // Decode timestamp, alarm
        super(struct);

        final PVStructureArray dim_field = struct.getSubField(PVStructureArray.class, "dimension");
        if (dim_field == null  ||  dim_field.getLength() < 2)
            throw new Exception("Need at least 2 dimensions, got " + dim_field);
        final StructureArrayData dim = new StructureArrayData();
        dim_field.get(0, 2, dim);
        // Could use dim.data[0].getSubField(PVInt.class, 1).get(),
        // but fetching by field name in case structure changes
        width = dim.data[0].getIntField("size").get();
        height = dim.data[1].getIntField("size").get();
        final int size = width * height;

        final PVUnion value_field = struct.getUnionField("value");
        final PVField value = value_field.get();
        if (! (value instanceof PVScalarArray))
            throw new Exception("Expected array for NTNDArray 'value', got " + value);

        // TODO: Not determining VImageType from codec, colorMode, ..
        //       For now claiming everything is grayscale,
        //       or custom of there is no VImageType.TYPE_*_GRAY for that data type.
        if (value instanceof PVByteArray)
        {
            final byte[] values = new byte[size];
            PVStructureHelper.convert.toByteArray((PVByteArray)value, 0, size, values, 0);
            data = new ArrayByte(values);
            data_type = VImageDataType.pvByte;
            image_type = VImageType.TYPE_BYTE_GRAY;
        }
        else if (value instanceof PVShortArray)
        {
            final short[] values = new short[size];
            PVStructureHelper.convert.toShortArray((PVShortArray)value, 0, size, values, 0);
            data = new ArrayShort(values);
            data_type = VImageDataType.pvShort;
            image_type = VImageType.TYPE_USHORT_GRAY;
        }
        else if (value instanceof PVIntArray)
        {
            final int[] values = new int[size];
            PVStructureHelper.convert.toIntArray((PVIntArray)value, 0, size, values, 0);
            data = new ArrayInt(values);
            data_type = VImageDataType.pvInt;
            image_type = VImageType.TYPE_CUSTOM;
        }
        else if (value instanceof PVLongArray)
        {
            final long[] values = new long[size];
            PVStructureHelper.convert.toLongArray((PVLongArray)value, 0, size, values, 0);
            data = new ArrayLong(values);
            data_type = VImageDataType.pvLong;
            image_type = VImageType.TYPE_CUSTOM;
        }
        else
            throw new Exception("Cannot extract byte[] from " + value);
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public ListNumber getData()
    {
        return data;
    }

    @Override
    public VImageDataType getDataType()
    {
        return data_type;
    }

    @Override
    public VImageType getVImageType()
    {
        return image_type;
    }
}
