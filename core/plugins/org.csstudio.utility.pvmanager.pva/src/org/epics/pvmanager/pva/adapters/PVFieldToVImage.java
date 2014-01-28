/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.BooleanArrayData;
import org.epics.pvdata.pv.ByteArrayData;
import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.PVBooleanArray;
import org.epics.pvdata.pv.PVByteArray;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.ShortArrayData;
import org.epics.vtype.VImage;
import org.epics.vtype.ValueUtil;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVImage implements VImage {

	private final int width;
	private final int height;
	private final byte[] data;
	
	private enum NDColorMode
	{
		NDColorModeMono,    /** Monochromatic image */
		NDColorModeBayer,   /** Bayer pattern image,
										1 value per pixel but with color filter on detector */
		NDColorModeRGB1,    /** RGB image with pixel color interleave,
										data array is [3, NX, NY] */
		NDColorModeRGB2,    /** RGB image with row color interleave,
										data array is [NX, 3, NY]  */
		NDColorModeRGB3,    /** RGB image with plane color interleave,
										data array is [NX, NY, 3]  */
		NDColorModeYUV444,  /** YUV image, 3 bytes encodes 1 RGB pixel */
		NDColorModeYUV422,  /** YUV image, 4 bytes encodes 2 RGB pixel */
		NDColorModeYUV411;   /** YUV image, 6 bytes encodes 4 RGB pixels */
	};
	
			
	/**
	 * @param pvImage
	 * @param disconnected
	 */
	public PVFieldToVImage(PVStructure pvImage, boolean disconnected) {
		
		PVScalarArray valueArray = (PVScalarArray)pvImage.getSubField("value");
		if (valueArray == null)
			throw new IllegalArgumentException("given pvImage structure is missing scalar_t[] value field");

		PVInt colorMode = pvImage.getIntField("colorMode");
		if (colorMode == null)
			throw new IllegalArgumentException("given pvImage structure is missing int colorMode field");
		int cmOrdinal = colorMode.get();
		NDColorMode[] cmValues = NDColorMode.values();
		if (cmOrdinal < 0 || cmOrdinal >= cmValues.length)
			throw new IllegalArgumentException("given pvImage structure field int colorMode is invalid");
		NDColorMode mode = cmValues[cmOrdinal];
			
		
		PVIntArray dim = (PVIntArray)pvImage.getScalarArrayField("dim", ScalarType.pvInt);
		if (dim == null)
			throw new IllegalArgumentException("given pvImage structure is missing int[] dim field");

		int dimLen = dim.getLength();
		if (dimLen != 2 && dimLen != 3)
			throw new IllegalArgumentException("given pvImage structure field int[] dim array size is not valid");

		IntArrayData dimData = new IntArrayData();
		dim.get(0, dim.getLength(), dimData);
		
		int nx = dimData.data[0];
		int ny = dimData.data[1];
		int nz = (dimLen == 3) ? dimData.data[2] : 1;
		
		int valueArraySize = nx * ny * nz;
		if (valueArraySize <= 0)
			throw new IllegalArgumentException("given pvImage structure field int[] dim values are not valid");
		
		if (valueArray.getLength() != valueArraySize)
			throw new IllegalArgumentException("scalar_t[] value array length does not match given dimensions");
		
		switch (mode)
		{
			case NDColorModeMono:
			case NDColorModeBayer:
			{
				
				// 2D value array
				if (nz != 1)
					throw new IllegalArgumentException("invalid dimensions for given color mode");
				
				width = nx;
				height = ny;
				
				switch (valueArray.getScalarArray().getElementType())
				{
					case pvBoolean:
					{
						BooleanArrayData bad = new BooleanArrayData();
						((PVBooleanArray)valueArray).get(0, valueArraySize, bad);
						// convert b&w to BGR
						data = new byte[3*valueArraySize];
						int p = 0;
						for (int i = 0; i < valueArraySize; i++)
						{
							byte c = bad.data[i] ? (byte)-1 : (byte)0;
							// B = G = R = c
							data[p++] = c; 
							data[p++] = c; 
							data[p++] = c;
						}
						break;
					}
					
					case pvByte:
					{
						ByteArrayData bad = new ByteArrayData();
						((PVByteArray)valueArray).get(0, valueArraySize, bad);
						// convert grayscale to BGR
						data = new byte[3*valueArraySize];
						int p = 0;
						for (int i = 0; i < valueArraySize; i++)
						{
							byte c = bad.data[i];
							// B = G = R = c
							data[p++] = c; 
							data[p++] = c; 
							data[p++] = c;
						}
						break;
					}
					
					case pvShort:
					{
						ShortArrayData bad = new ShortArrayData();
						((PVShortArray)valueArray).get(0, valueArraySize, bad);
						// convert grayscale to BGR
						data = new byte[3*valueArraySize];
						int p = 0;
						for (int i = 0; i < valueArraySize; i++)
						{
							byte c = (byte)(bad.data[i] >>> 8);
							// B = G = R = c
							data[p++] = c; 
							data[p++] = c; 
							data[p++] = c;
						}
						break;
					}

					case pvInt:
					{
						IntArrayData bad = new IntArrayData();
						((PVIntArray)valueArray).get(0, valueArraySize, bad);
						// convert grayscale to BGR
						data = new byte[3*valueArraySize];
						int p = 0;
						for (int i = 0; i < valueArraySize; i++)
						{
							byte c = (byte)(bad.data[i] >>> 24);
							// B = G = R = c
							data[p++] = c; 
							data[p++] = c; 
							data[p++] = c;
						}
						break;
					}

					/*
					case pvFloat:
					{
						FloatArrayData bad = new FloatArrayData();
						((PVFloatArray)valueArray).get(0, valueArraySize, bad);
						// convert grayscale to BGR
						data = new byte[3*valueArraySize];
						int p = 0;
						for (int i = 0; i < valueArraySize; i++)
						{
							int tc = (int)(bad.data[i] * 255);   // assuming [0 - 1.0]
							byte c = (tc > 127) ? (byte)(tc-256) : (byte)tc;
							// B = G = R = c
							data[p++] = c; 
							data[p++] = c; 
							data[p++] = c;
						}
						break;
					}

					case pvDouble:
					{
						DoubleArrayData bad = new DoubleArrayData();
						((PVDoubleArray)valueArray).get(0, valueArraySize, bad);
						// convert grayscale to BGR
						data = new byte[3*valueArraySize];
						int p = 0;
						for (int i = 0; i < valueArraySize; i++)
						{
							int tc = (int)(bad.data[i] * 255);	// assuming [0 - 1.0]
							byte c = (tc > 127) ? (byte)(tc-256) : (byte)tc;
							// B = G = R = c
							data[p++] = c; 
							data[p++] = c; 
							data[p++] = c;
						}
						break;
					}
					*/
					
					default:
						throw new IllegalArgumentException("unsupported scalar_t[] value type");
				
				}
				
				break;
			}
			
			case NDColorModeRGB1:
			{
				if (nx != 3)
					throw new IllegalArgumentException("dim[0] should be 3 for NDColorModeRGB1");
				width = ny;
				height = nz;
				
				ByteArrayData bad = new ByteArrayData();
				((PVByteArray)valueArray).get(0, valueArraySize, bad);
				// convert to BGR
				data = new byte[valueArraySize];
				int out = 0; int in = 0;
				while (in < valueArraySize)
				{
					byte r = bad.data[in++]; 
					byte g = bad.data[in++]; 
					byte b = bad.data[in++];
					data[out++] = b; 
					data[out++] = g; 
					data[out++] = r;
				}
				break;
			}

			case NDColorModeRGB2:
			{
				// ny = 3
				if (ny != 3)
					throw new IllegalArgumentException("dim[1] should be 3 for NDColorModeRGB2");
				width = nx;
				height = nz;
				
				ByteArrayData bad = new ByteArrayData();
				((PVByteArray)valueArray).get(0, valueArraySize, bad);
				// convert to BGR
				data = new byte[valueArraySize];
				int out = 0; 
				int nCols = width;
				int nRows = height;
				for (int row = 0; row < nRows; row++)
				{
					int redIn = row * nCols * 3;
					int greenIn = redIn + nCols;
					int blueIn = greenIn + nCols;
					for (int col = 0; col < nCols; col++)
					{
						byte r = bad.data[redIn++]; 
						byte g = bad.data[greenIn++]; 
						byte b = bad.data[blueIn++];
						data[out++] = b; 
						data[out++] = g; 
						data[out++] = r;
					}
				}
				break;
			}
			
			case NDColorModeRGB3:
			{
				// nz = 3
				if (nz != 3)
					throw new IllegalArgumentException("dim[2] should be 3 for NDColorModeRGB3");
				width = nx;
				height = ny;
				
				int imageSize = width * height;

				ByteArrayData bad = new ByteArrayData();
				((PVByteArray)valueArray).get(0, valueArraySize, bad);
				// convert to BGR
				data = new byte[valueArraySize];
				int out = 0; 
				int redIn = 0;
				int greenIn = imageSize;
				int blueIn = imageSize * 2;
				while (redIn < imageSize)
				{
					byte r = bad.data[redIn++]; 
					byte g = bad.data[greenIn++]; 
					byte b = bad.data[blueIn++];
					data[out++] = b; 
					data[out++] = g; 
					data[out++] = r;
				}
				break;
			}
		
			case NDColorModeYUV444:
			case NDColorModeYUV422:
			case NDColorModeYUV411:
			default:
				throw new IllegalArgumentException("unsupported colorMode");
		}				

	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Class<?> type = ValueUtil.typeOf(this);
        builder.append(type.getSimpleName())
                .append("[height=")
                .append(getHeight())
                .append(", width=")
                .append(getWidth());
        builder.append(']');
        return builder.toString();
    }

}
