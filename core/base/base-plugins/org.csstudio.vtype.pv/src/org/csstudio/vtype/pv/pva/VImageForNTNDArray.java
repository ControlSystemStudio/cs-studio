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
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.PVUByteArray;
import org.epics.pvdata.pv.PVUIntArray;
import org.epics.pvdata.pv.PVULongArray;
import org.epics.pvdata.pv.PVUShortArray;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.pv.StructureArrayData;

/** VImage for a ListNumber
 *  @author Kay Kasemir
 *  @author Amanda Carpenter - handle unsigned VImageDataType, detect VImageType
 */
@SuppressWarnings("nls")
class VImageForNTNDArray extends VTypeTimeAlarmBase implements VImage
{
    private final int width, height;
    private final ListNumber data;
    private final VImageDataType data_type;
    private final VImageType image_type;

    // Could just use VImageType.values()[i+1] instead of color_mode_types[i], but
    // (a) there are more VImageType values than defined color modes, and
    // (b) the NTNDArray specification for color mode might eventually change
    private static final VImageType color_mode_types[] = { VImageType.TYPE_MONO, VImageType.TYPE_BAYER,
            VImageType.TYPE_RGB1, VImageType.TYPE_RGB2, VImageType.TYPE_RGB3, VImageType.TYPE_YUV444,
            VImageType.TYPE_YUV422, VImageType.TYPE_YUV411 };

    public VImageForNTNDArray(final PVStructure struct) throws Exception
    {
        // Decode timestamp, alarm
        super(struct);

        // Get dimensions
        final PVStructureArray dim_field = struct.getSubField(PVStructureArray.class, "dimension");
        if (dim_field == null || dim_field.getLength() < 2)
            throw new Exception("Need at least 2 dimensions, got " + dim_field);
        final StructureArrayData dim = new StructureArrayData();
        dim_field.get(0, 2, dim);
        // Could use dim.data[0].getSubField(PVInt.class, 1).get(),
        // but fetching by field name in case structure changes
        final int n_dims = dim.data.length;
        int dimensions[] = new int[n_dims];
        for (int i = 0; i < n_dims; ++i)
            dimensions[i] = dim.data[i].getIntField("size").get();

        final PVUnion value_field = struct.getUnionField("value");
        final PVField value = value_field.get();
        if (!(value instanceof PVScalarArray))
            throw new Exception("Expected array for NTNDArray 'value', got " + value);

        // Try to get value of color mode attribute
        // TODO: bayer color mode requires a bayerPattern attribute as well...
        final PVStructureArray attribute_field = struct.getSubField(PVStructureArray.class, "attribute");
        int colorMode = -1;
        if (attribute_field != null && attribute_field.getLength() > 0)
        {
            final StructureArrayData attr = new StructureArrayData();
            attribute_field.get(0, 2, attr);
            for (PVStructure attribute : attr.data)
            {
                PVString name_field = attribute.getStringField("name");
                if (name_field != null && "colorMode".equalsIgnoreCase(name_field.get()))
                {
                    PVField color_mode_field = attribute.getUnionField("value").get();
                    if (!(color_mode_field instanceof PVInt))
                    {
                        // TODO: log warning, or throw exception?
                    }
                    else
                    {
                        colorMode = ((PVInt) color_mode_field).get();
                        break;
                    }
                }
            }
        }
        if (colorMode == -1)
        { // Unsuccessful, so try to guess color mode from dimensions
            if (dimensions.length < 3)
                colorMode = 0; // mono
            else if (dimensions.length > 3)
                colorMode = -1; // custom
            else if (dimensions[0] == 3)
                colorMode = 2; // RGB1
            else if (dimensions[1] == 3)
                colorMode = 3; // RGB2
            else if (dimensions[2] == 3)
                colorMode = 4; // RGB3
        }
        else if (colorMode <= 4 && colorMode >= 2 && dimensions.length != 3)
        {
            throw new Exception("Color mode " + colorMode + " (" + color_mode_types[colorMode] +
                    ") requires 3 dimensions, got " + dimensions.length);
        }

        if (colorMode >= color_mode_types.length || colorMode < 0)
            image_type = VImageType.TYPE_CUSTOM;
        else
            image_type = color_mode_types[colorMode];

        // Init. width, height, and size
        int size;
        switch (image_type)
        {
            case TYPE_RGB1:
                width = dimensions[1];
                height = dimensions[2];
                size = dimensions[0] * dimensions[1] * dimensions[2];
                break;
            case TYPE_RGB2:
                width = dimensions[0];
                height = dimensions[2];
                size = dimensions[0] * dimensions[1] * dimensions[2];
                break;
            case TYPE_RGB3:
                width = dimensions[0];
                height = dimensions[1];
                size = dimensions[0] * dimensions[1] * dimensions[2];
                break;
            default:
                width = dimensions[0];
                height = dimensions[1];
                size = width * height;
        }

        // Get data and data type
        if (value instanceof PVByteArray)
        {
            final byte[] values = new byte[size];
            PVStructureHelper.convert.toByteArray((PVByteArray) value, 0, size, values, 0);
            data = new ArrayByte(values);
            data_type = VImageDataType.pvByte;
        }
        else if (value instanceof PVUByteArray)
        {
            final byte[] values = new byte[size];
            PVStructureHelper.convert.toByteArray((PVUByteArray) value, 0, size, values, 0);
            data = new ArrayByte(values);
            data_type = VImageDataType.pvUByte;
        }
        else if (value instanceof PVShortArray)
        {
            final short[] values = new short[size];
            PVStructureHelper.convert.toShortArray((PVShortArray) value, 0, size, values, 0);
            data = new ArrayShort(values);
            data_type = VImageDataType.pvShort;
        }
        else if (value instanceof PVUShortArray)
        {
            final short[] values = new short[size];
            PVStructureHelper.convert.toShortArray((PVUShortArray) value, 0, size, values, 0);
            data = new ArrayShort(values);
            data_type = VImageDataType.pvUShort;
        }
        else if (value instanceof PVIntArray)
        {
            final int[] values = new int[size];
            PVStructureHelper.convert.toIntArray((PVIntArray) value, 0, size, values, 0);
            data = new ArrayInt(values);
            data_type = VImageDataType.pvInt;
        }
        else if (value instanceof PVUIntArray)
        {
            final int[] values = new int[size];
            PVStructureHelper.convert.toIntArray((PVUIntArray) value, 0, size, values, 0);
            data = new ArrayInt(values);
            data_type = VImageDataType.pvUInt;
        }
        else if (value instanceof PVLongArray)
        {
            final long[] values = new long[size];
            PVStructureHelper.convert.toLongArray((PVLongArray) value, 0, size, values, 0);
            data = new ArrayLong(values);
            data_type = VImageDataType.pvLong;
        }
        else if (value instanceof PVULongArray)
        {
            final long[] values = new long[size];
            PVStructureHelper.convert.toLongArray((PVULongArray) value, 0, size, values, 0);
            data = new ArrayLong(values);
            data_type = VImageDataType.pvULong;
        }
        else
            throw new Exception("Cannot decode NTNDArray type of value " + value);
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
