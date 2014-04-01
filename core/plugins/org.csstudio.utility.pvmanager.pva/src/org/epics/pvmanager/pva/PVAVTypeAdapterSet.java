/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.StandardFieldFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvmanager.pva.adapters.PVFieldNTHistogramToVIntArray;
import org.epics.pvmanager.pva.adapters.PVFieldNTHistogramToVLongArray;
import org.epics.pvmanager.pva.adapters.PVFieldNTHistogramToVShortArray;
import org.epics.pvmanager.pva.adapters.PVFieldNTMatrixToVDoubleArray;
import org.epics.pvmanager.pva.adapters.PVFieldNTNameValueToVTable;
import org.epics.pvmanager.pva.adapters.PVFieldToVBoolean;
import org.epics.pvmanager.pva.adapters.PVFieldToVByte;
import org.epics.pvmanager.pva.adapters.PVFieldToVByteArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVDouble;
import org.epics.pvmanager.pva.adapters.PVFieldToVDoubleArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVEnum;
import org.epics.pvmanager.pva.adapters.PVFieldToVFloat;
import org.epics.pvmanager.pva.adapters.PVFieldToVFloatArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVImage;
import org.epics.pvmanager.pva.adapters.PVFieldToVInt;
import org.epics.pvmanager.pva.adapters.PVFieldToVIntArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVLong;
import org.epics.pvmanager.pva.adapters.PVFieldToVLongArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVShort;
import org.epics.pvmanager.pva.adapters.PVFieldToVShortArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVStatistics;
import org.epics.pvmanager.pva.adapters.PVFieldToVString;
import org.epics.pvmanager.pva.adapters.PVFieldToVStringArray;
import org.epics.pvmanager.pva.adapters.PVFieldToVTable;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VByte;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VFloat;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VImage;
import org.epics.vtype.VInt;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VLong;
import org.epics.vtype.VLongArray;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VShort;
import org.epics.vtype.VShortArray;
import org.epics.vtype.VStatistics;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;

/**
 *
 * @author msekoranja
 */
public class PVAVTypeAdapterSet implements PVATypeAdapterSet {
    
	private static final FieldCreate fieldCreate = FieldFactory.getFieldCreate();
    @Override
    public Set<PVATypeAdapter> getAdapters() {
        return converters;
    }
    
    //  -> VDouble
    final static PVATypeAdapter ToVDouble = new PVATypeAdapter(
    		VDouble.class, 
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalar", "scalar_t" }, 
    		new Field[]
    				{
    					fieldCreate.createScalar(ScalarType.pvDouble)
    				})
    	{
            @Override
            public VDouble createValue(PVStructure message, Field valueType, boolean disconnected) {
                return new PVFieldToVDouble(message, disconnected);
            }
        };

    //  -> VFloat
    final static PVATypeAdapter ToVFloat = new PVATypeAdapter(
    		VFloat.class, 
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalar", "scalar_t" }, 
    		new Field[]
    				{
    					fieldCreate.createScalar(ScalarType.pvFloat)
    				})
    	{
            @Override
            public VFloat createValue(PVStructure message, Field valueType, boolean disconnected) {
                return new PVFieldToVFloat(message, disconnected);
            }
        };
        
    //  -> VByte
    final static PVATypeAdapter ToVByte = new PVATypeAdapter(
    		VByte.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalar", "scalar_t" },
    		new Field[]
    				{
    					fieldCreate.createScalar(ScalarType.pvByte),
    					fieldCreate.createScalar(ScalarType.pvUByte)
    				})
    	{

            @Override
            public VByte createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVByte(message, disconnected);
            }
        };

    //  -> VShort
    final static PVATypeAdapter ToVShort = new PVATypeAdapter(
    		VShort.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalar", "scalar_t" },
    		new Field[]
    				{
    					fieldCreate.createScalar(ScalarType.pvShort),
    					fieldCreate.createScalar(ScalarType.pvUShort)
    				})
    	{

            @Override
            public VShort createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVShort(message, disconnected);
            }
        };

    //  -> VInt
    final static PVATypeAdapter ToVInt = new PVATypeAdapter(
    		VInt.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalar", "scalar_t" },
    		new Field[]
    				{
    					fieldCreate.createScalar(ScalarType.pvInt),
    					fieldCreate.createScalar(ScalarType.pvUInt)
    				})
    	{

            @Override
            public VInt createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVInt(message, disconnected);
            }
        };
        
    //  -> VLong
    final static PVATypeAdapter ToVLong = new PVATypeAdapter(
    		VLong.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalar", "scalar_t" },
    		new Field[]
    				{
    					fieldCreate.createScalar(ScalarType.pvLong),
    					fieldCreate.createScalar(ScalarType.pvULong),
    				})
    	{

            @Override
            public VLong createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVLong(message, disconnected);
            }
        };
        
    //  -> VBoolean
    final static PVATypeAdapter ToVBoolean = new PVATypeAdapter(
    		VBoolean.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalar", "scalar_t" },
    		new Field[]
    				{
    					fieldCreate.createScalar(ScalarType.pvBoolean)
    				})
    	{

            @Override
            public VBoolean createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVBoolean(message, disconnected);
            }
        };
        
    //  -> VString
    final static PVATypeAdapter ToVString = new PVATypeAdapter(
    		VString.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalar", "scalar_t" },
			fieldCreate.createScalar(ScalarType.pvString))
    	{
            @Override
            public VString createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVString(message, disconnected);
            }
        };
            
    //  -> VEnum
    final static PVATypeAdapter ToVEnum = new PVATypeAdapter(
    		VEnum.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTEnum", "enum_t" },
    		StandardFieldFactory.getStandardField().enumerated())
    	{
            @Override
            public VEnum createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVEnum(message, disconnected);
            }
        };

    //  -> VArrayDouble
    final static PVATypeAdapter ToVArrayDouble = new PVATypeAdapter(
    		VDoubleArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalarArray", "scalar_t[]" },
    		fieldCreate.createScalarArray(ScalarType.pvDouble))
    	{
            @Override
            public VDoubleArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVDoubleArray(message, disconnected);
            }
        };

    //  -> VArrayFloat
    final static PVATypeAdapter ToVArrayFloat = new PVATypeAdapter(
    		VFloatArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalarArray", "scalar_t[]" },
    		fieldCreate.createScalarArray(ScalarType.pvFloat))
    	{
            @Override
            public VFloatArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVFloatArray(message, disconnected);
            }
        };
        
    //  -> VArrayInt
    final static PVATypeAdapter ToVArrayInt = new PVATypeAdapter(
    		VIntArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalarArray", "scalar_t[]" },
    		fieldCreate.createScalarArray(ScalarType.pvInt))
    	{
            @Override
            public VIntArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVIntArray(message, disconnected);
            }
        };

    //  -> VArrayLong
    final static PVATypeAdapter ToVArrayLong = new PVATypeAdapter(
    		VLongArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalarArray", "scalar_t[]" },
    		fieldCreate.createScalarArray(ScalarType.pvLong))
    	{
            @Override
            public VLongArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVLongArray(message, disconnected);
            }
        };
        
    //  -> VArrayShort
    final static PVATypeAdapter ToVArrayShort = new PVATypeAdapter(
    		VShortArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalarArray", "scalar_t[]" },
    		fieldCreate.createScalarArray(ScalarType.pvShort))
    	{
            @Override
            public VShortArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVShortArray(message, disconnected);
            }
        };
        
    //  -> VArrayByte
    final static PVATypeAdapter ToVArrayByte = new PVATypeAdapter(
    		VByteArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalarArray", "scalar_t[]" },
    		fieldCreate.createScalarArray(ScalarType.pvByte))
    	{
            @Override
            public VByteArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVByteArray(message, disconnected);
            }
        };
        
    //  -> VArrayString
    final static PVATypeAdapter ToVArrayString = new PVATypeAdapter(
    		VStringArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTScalarArray", "scalar_t[]" },
    		fieldCreate.createScalarArray(ScalarType.pvString))
    	{
            @Override
            public VStringArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVStringArray(message, disconnected);
            }
        };
        
    //  -> VImage
    final static PVATypeAdapter ToVImage = new PVATypeAdapter(
    		VImage.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTImage" })
    	{
            @Override
            public VImage createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVImage(message, disconnected);
            }
        };

    //  -> VTable
    final static PVATypeAdapter ToVTable = new PVATypeAdapter(
    		VTable.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTTable" })
    	{
            @Override
            public VTable createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVTable(message, disconnected);
            }
        };

    //  -> VDoubleArray as matrix (NTMatrix support)
    final static PVATypeAdapter ToVDoubleArrayAsMatrix = new PVATypeAdapter(
    		VDoubleArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTMatrix" })
    	{
            @Override
            public VDoubleArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldNTMatrixToVDoubleArray(message, disconnected);
            }
        };
        
    //  -> VTable as name-value (NTNameValue support)
    final static PVATypeAdapter ToVTableAsNameValue = new PVATypeAdapter(
    		VTable.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTNameValue" })
    	{
            @Override
            public VTable createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldNTNameValueToVTable(message, disconnected);
            }
        };

    //  -> VStatistics 
    final static PVATypeAdapter ToVStatistics = new PVATypeAdapter(
    		VStatistics.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTAggregate" })
    	{
            @Override
            public VStatistics createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	return new PVFieldToVStatistics(message, disconnected);
            }
        };

    //  -> VNumberArray (NTHistogram support) 
    final static PVATypeAdapter ToVNumberArrayAsHistogram = new PVATypeAdapter(
    		VNumberArray.class,
    		new String[] { "uri:ev4:nt/2012/pwd:NTHistogram" })
    	{
            @Override
            public VNumberArray createValue(final PVStructure message, Field valueType, boolean disconnected) {
            	PVField valueField = message.getSubField("value");
            	if (valueField instanceof PVScalarArray)
            	{
            		switch (((PVScalarArray)valueField).getScalarArray().getElementType())
            		{
            			case pvShort: return new PVFieldNTHistogramToVShortArray(message, disconnected);
            			case pvInt  : return new PVFieldNTHistogramToVIntArray(message, disconnected);
            			case pvLong : return new PVFieldNTHistogramToVLongArray(message, disconnected);
            			default:
                    		throw new RuntimeException("NTHistogram 'value' scalar array must be { short[] | int[] | long[] }.");
            		}
            		
            	}
            	else
            		throw new RuntimeException("NTHistogram does not have a scalar array 'value' field.");
            }
        };

    private static final Set<PVATypeAdapter> converters;
    
    static {
        Set<PVATypeAdapter> newFactories = new HashSet<PVATypeAdapter>();
        
        // Add all SCALARs
        newFactories.add(ToVDouble);
        newFactories.add(ToVFloat);
        newFactories.add(ToVByte);
        newFactories.add(ToVShort);
        newFactories.add(ToVInt);
        newFactories.add(ToVLong);
        newFactories.add(ToVString);
        newFactories.add(ToVEnum);
        newFactories.add(ToVBoolean);

        // Add all ARRAYs
        newFactories.add(ToVArrayDouble);
        newFactories.add(ToVArrayFloat);
        newFactories.add(ToVArrayByte);
        newFactories.add(ToVArrayShort);
        newFactories.add(ToVArrayInt);
        newFactories.add(ToVArrayLong);
        newFactories.add(ToVArrayString);
        //newFactories.add(ToVArrayEnum);
        // no VBooleanArray
        
        newFactories.add(ToVImage);
        newFactories.add(ToVTable);
        newFactories.add(ToVDoubleArrayAsMatrix);	// NTMatrix support
        newFactories.add(ToVTableAsNameValue);	// NTNameValue support

        newFactories.add(ToVStatistics); // NTAggregate support
        newFactories.add(ToVNumberArrayAsHistogram); // NTHistogram support

        converters = Collections.unmodifiableSet(newFactories);
    }
    
    // TODO
    /*
VEnumArray - not explicitly a NT
VMultiDouble - missing Display
VMultiEnum  - missing Display
VMultiInt - missing Display
VMultiString  - missing Display
    */
}
