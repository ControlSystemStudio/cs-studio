package org.csstudio.scan.server.pvaccess;

import java.util.ArrayList;
import java.util.Arrays;

import org.epics.pvaccess.PVFactory;
import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Structure;
import org.epics.pvdata.pv.Type;

public class Mapper
{
    private final static Convert convert = ConvertFactory.getConvert();
    private static final FieldCreate fieldCreate = PVFactory.getFieldCreate();
    private static final PVDataCreate pvDataCreate = PVFactory.getPVDataCreate();


    final PVStructure originStructure;
    final PVStructure copyStructure;
    final int[] toOriginStructure;
    final int[] toCopyStructure;

    public Mapper(PVStructure originStructure, PVStructure pvRequest)
    {
        this(originStructure, pvRequest, null);
    }

    public Mapper(PVStructure originStructure, PVStructure pvRequest, String structureName)
    {
        if(structureName!=null && structureName.length()>0) {
            if(pvRequest.getPVFields().length>0) {
                pvRequest = pvRequest.getStructureField(structureName);
                if(pvRequest==null)
                    throw new IllegalArgumentException("pvRequest does not contain " + structureName + " subfield");
            }
        }

        this.originStructure = originStructure;

        ArrayList<Integer> indexMapping = new ArrayList<Integer>(originStructure.getNumberFields());

        if(pvRequest.getPVFields().length==0)
        {
            copyStructure = pvDataCreate.createPVStructure(originStructure.getStructure());
            // 1-1 mapping
            int fieldCount = copyStructure.getNumberFields();
            for (int i = 0; i < fieldCount; i++)
                indexMapping.add(i);
        }
        else
        {
            indexMapping.add(-1);	// top

            if(pvRequest.getSubField("field")!=null) {
                pvRequest = pvRequest.getStructureField("field");
            }
            Structure structure = createStructure(originStructure, indexMapping, pvRequest);
            this.copyStructure = pvDataCreate.createPVStructure(structure);
			/*
			System.out.println("----------------------------------------------------------");
			System.out.println(originStructure);
			System.out.println(pvRequest);
			System.out.println(indexMapping);
			System.out.println("----------------------------------------------------------");
			*/

        }



        toOriginStructure = new int[copyStructure.getNumberFields()];
        toCopyStructure = new int[originStructure.getNumberFields()];
        Arrays.fill(toCopyStructure, -1);

        int ix = 0;
        for (Integer i : indexMapping)
        {
            int iv = i.intValue();
            toOriginStructure[ix] = iv;
            if (iv != -1)
                toCopyStructure[iv] = ix;
            ix++;
        }
    }

    public PVStructure getCopyStructure()
    {
        return copyStructure;
    }

    public int getCopyStructureIndex(int ix)
    {
        return toCopyStructure[ix];
    }

    public int getOriginStructureIndex(int ix)
    {
        return toOriginStructure[ix];
    }

    public void updateCopyStructure(BitSet copyStructureBitSet)
    {
        boolean doAll = copyStructureBitSet == null || copyStructureBitSet.get(0);
        if (doAll)
        {
            for (int i = 1; i < toOriginStructure.length;)
            {
                final PVField copyField = copyStructure.getSubField(i);
                final PVField originField = originStructure.getSubField(toOriginStructure[i]);
                convert.copy(originField, copyField);
                i = copyField.getNextFieldOffset();
            }
        }
        else
        {
            int i = copyStructureBitSet.nextSetBit(1);
            while (i != -1)
            {
                final PVField copyField = copyStructure.getSubField(i);
                final PVField originField = originStructure.getSubField(toOriginStructure[i]);
                convert.copy(originField, copyField);
                i = copyStructureBitSet.nextSetBit(copyField.getNextFieldOffset());
            }
        }
    }

    public void updateOriginStructure(BitSet copyStructureBitSet)
    {
        boolean doAll = copyStructureBitSet == null || copyStructureBitSet.get(0);
        if (doAll)
        {
            for (int i = 1; i < toOriginStructure.length;)
            {
                final PVField copyField = copyStructure.getSubField(i);
                final PVField originField = originStructure.getSubField(toOriginStructure[i]);
                convert.copy(copyField, originField);
                i = copyField.getNextFieldOffset();
            }
        }
        else
        {
            int i = copyStructureBitSet.nextSetBit(1);
            while (i != -1)
            {
                final PVField copyField = copyStructure.getSubField(i);
                final PVField originField = originStructure.getSubField(toOriginStructure[i]);
                convert.copy(copyField, originField);
                i = copyStructureBitSet.nextSetBit(copyField.getNextFieldOffset());
            }
        }
    }

    public void updateCopyStructureOriginBitSet(BitSet originStructureBitSet, BitSet copyBitSet)
    {
        copyBitSet.clear();
        boolean doAll = originStructureBitSet.get(0);
        if (doAll)
        {
            copyBitSet.set(0);
            for (int i = 1; i < toOriginStructure.length;)
            {
                final PVField copyField = copyStructure.getSubField(i);
                final PVField originField = originStructure.getSubField(toOriginStructure[i]);
                convert.copy(originField, copyField);
                i = copyField.getNextFieldOffset();
            }
        }
        else
        {
            int i = originStructureBitSet.nextSetBit(1);
            while (i != -1)
            {
                int toCopyIndex = toCopyStructure[i];
                if (toCopyIndex != -1)
                {
                    copyBitSet.set(toCopyIndex);
                    final PVField copyField = copyStructure.getSubField(toCopyIndex);
                    final PVField originField = originStructure.getSubField(i);
                    convert.copy(originField, copyField);
                    i = originStructureBitSet.nextSetBit(originField.getNextFieldOffset());
                }
                else
                {
                    final PVField originField = originStructure.getSubField(i);
                    i = originStructureBitSet.nextSetBit(originField.getNextFieldOffset());
                }
            }
        }
    }

    private static void addMapping(PVField pvRecordField, ArrayList<Integer> indexMapping) {
        if (pvRecordField.getField().getType() == Type.structure)
        {
            indexMapping.add(pvRecordField.getFieldOffset());
            PVStructure struct = (PVStructure)pvRecordField;
            for (PVField pvField : struct.getPVFields())
                addMapping(pvField, indexMapping);
        }
        else
        {
            indexMapping.add(pvRecordField.getFieldOffset());
        }
    }
/*
    private static Structure createStructure(PVStructure pvRecord, ArrayList<Integer> indexMapping, PVStructure pvFromRequest) {
        PVField[] pvFromFields = pvFromRequest.getPVFields();
        int length = pvFromFields.length;
        ArrayList<Field> fieldList = new ArrayList<Field>(length);
        ArrayList<String> fieldNameList = new ArrayList<String>(length);
        for(int i=0; i<length; i++) {
        	PVField pvField = pvFromFields[i];
        	if(pvField.getField().getType()==Type.structure) {
        		PVStructure pvStruct = (PVStructure)pvField;
        		PVField pvLeaf = pvStruct.getSubField("leaf.source");
        		if(pvLeaf!=null && (pvLeaf instanceof PVString)){
        			PVString pvString = (PVString)pvLeaf;
        			PVField pvRecordField = pvRecord.getSubField(pvString.get());
        			if(pvRecordField!=null) {
        				addMapping(pvRecordField, indexMapping);
        				fieldNameList.add(pvString.get());
        				fieldList.add(pvRecordField.getField());
        			}
        		} else {
    				indexMapping.add(-1);		// fake structure, will not be mapped
    				fieldNameList.add("fake");
        			fieldList.add(createStructure(pvRecord,indexMapping,pvStruct));
        		}
        	} else {
        		PVString pvString = (PVString)pvFromFields[i];
        		String n = ((PVStructure)pvString.getParent()).getStructure().getFieldName(i);
        		if(n.equals("fieldList")) {
        			String[] fieldNames = commaPattern.split(pvString.get());
        			for(int j=0; j<fieldNames.length; j++) {
        				String name = fieldNames[j].trim();
        				PVField pvRecordField = pvRecord.getSubField(name);
        				if(pvRecordField!=null) {
            				addMapping(pvRecordField, indexMapping);
            				fieldNameList.add(name);
        					fieldList.add(pvRecordField.getField());
        				}
        			}
        		} else {
        			PVField pvRecordField = pvRecord.getSubField(pvString.get().trim());
        			if(pvRecordField!=null) {
        				addMapping(pvRecordField, indexMapping);
        				fieldNameList.add(pvString.get());
        				fieldList.add(pvRecordField.getField());
        			}
        		}
        	}
        }
        Field[] fields = new Field[fieldList.size()];
        fields = fieldList.toArray(fields);
        String[] names = new String[fieldNameList.size()];
        names = fieldNameList.toArray(names);
        return fieldCreate.createStructure(names, fields);
    }
*/


    private static Structure createStructure(PVStructure pvRecord, ArrayList<Integer> indexMapping, PVStructure pvFromRequest) {
        if(pvFromRequest.getStructure().getFieldNames().length==0) {
            // 1-1 mapping
            int fieldCount = pvRecord.getNumberFields();
            for (int i = 1; i < fieldCount; i++)
                indexMapping.add(i);
            return pvRecord.getStructure();
        }
        Field field = createField(pvRecord,indexMapping,pvFromRequest);
        if(field==null) return null;
        if(field.getType()==Type.structure) return (Structure)field;
        String[] fieldNames = new String[1];
        Field[] fields = new Field[1];
        String name = getFullName(pvFromRequest,"");
        int ind = name.lastIndexOf('.');
        if(ind>0) name = name.substring(ind+1);
        fieldNames[0] = name;
        fields[0] = field;
        return fieldCreate.createStructure(fieldNames, fields);
    }

    private static Field createField(PVStructure pvRecord, ArrayList<Integer> indexMapping, PVStructure pvFromRequest) {
        PVField[] pvFromRequestFields = pvFromRequest.getPVFields();
        String[] fromRequestFieldNames = pvFromRequest.getStructure().getFieldNames();
        int length = pvFromRequestFields.length;
        int number = 0;
        int indopt = -1;
        for(int i=0; i<length; i++) {
            if(!fromRequestFieldNames[i].equals("_options")) {
                number++;
            } else {
                indopt = i;
            }
        }
        if(number==0) return pvRecord.getStructure();
        if(number==1) {
            String nameFromRecord = "";
            nameFromRecord = getFullName(pvFromRequest,nameFromRecord);
            PVField pvRecordField = pvRecord.getSubField(nameFromRecord);
            if(pvRecordField==null) return null;
            Type recordFieldType = pvRecordField.getField().getType();
            if(recordFieldType!=Type.structure)
            {
                addMapping(pvRecordField, indexMapping);		// msekoran
                return pvRecordField.getField();
            }
            PVStructure pvSubFrom = (PVStructure)pvFromRequest.getSubField(nameFromRecord);
            PVField[] pvs = pvSubFrom.getPVFields();
            length = pvs.length;
            number = 0;
            for(int i=0; i<length; i++) {
                if(!pvs[i].getFieldName().equals("_options")) {
                    number++;
                }
            }
            //if(number==0) return pvRecordField.getField();
            Field[] fields = new Field[1];
            String[] fieldNames = new String[1];
            fieldNames[0] = pvRecordField.getFieldName();
            if(number==0) {
                fields[0] = pvRecordField.getField();
            } else {
                fields[0] = createField((PVStructure)pvRecordField,indexMapping,pvSubFrom);
            }
            return fieldCreate.createStructure(fieldNames, fields);
        }
        ArrayList<Field> fieldList = new ArrayList<Field>(number);
        ArrayList<String> fieldNameList = new ArrayList<String>(number);
        for(int i=0; i<length; i++) {
            if(i==indopt) continue;
            PVStructure arg = (PVStructure)pvFromRequestFields[i];
            PVStructure yyy = (PVStructure)pvFromRequestFields[i];
            String zzz = getFullName(yyy,"");
            String full = fromRequestFieldNames[i];
            if(zzz.length()>0) {
                full += "." + zzz;
                arg = getSubStructure(yyy,zzz);
            }
            PVField pvRecordField = pvRecord.getSubField(full);
            if(pvRecordField==null) continue;
            Field field = pvRecordField.getField();
            if(field.getType()!=Type.structure) {
                addMapping(pvRecordField, indexMapping);		// msekoran
                fieldNameList.add(full);
                fieldList.add(field);
                continue;
            }
            Field xxx = createField((PVStructure)pvRecordField,indexMapping,arg);
            if(xxx!=null) {
                addMapping(pvRecordField, indexMapping);		// msekoran

                fieldNameList.add(fromRequestFieldNames[i]);
                fieldList.add(xxx);
            }
        }
        Field[] fields = new Field[fieldList.size()];
        String[] fieldNames = new String[fieldNameList.size()];
        fields = fieldList.toArray(fields);
        fieldNames = fieldNameList.toArray(fieldNames);
        boolean makeValue = true;
        int indValue = -1;
        for(int i=0;i<fieldNames.length; i++) {
            if(fieldNames[i].endsWith("value")) {
                if(indValue==-1) {
                    indValue = i;
                } else {
                    makeValue = false;
                }
            }
        }
        for(int i=0;i<fieldNames.length; i++) {
            if(makeValue==true&&indValue==i) {
                fieldNames[i] = "value";
            } else {
                String xxx = fieldNames[i];
                int ind = xxx.indexOf('.');
                if(ind>0) fieldNames[i] = xxx.substring(0, ind);
            }
        }
        return fieldCreate.createStructure(fieldNames, fields);
    }

    private static String getFullName(PVStructure pvFromRequest,String nameFromRecord) {
        PVField[] pvFields = pvFromRequest.getPVFields();
        int len = pvFields.length;
        if(len==1) {
            pvFromRequest = (PVStructure) pvFields[0];
            if(pvFromRequest.getFieldName().equals("_options")) return nameFromRecord;
            if(nameFromRecord.length()!=0) nameFromRecord += ".";
            nameFromRecord += pvFromRequest.getFieldName();
            return getFullName(pvFromRequest,nameFromRecord);
        }
        if(len==2) {
            PVField subField = null;
            if(pvFields[0].getFieldName().equals("_options")) {
                subField = pvFields[1];
            } else if(pvFields[1].getFieldName().equals("_options")) {
                subField = pvFields[1];
            }
            if(subField!=null) {
                if(nameFromRecord.length()!=0) nameFromRecord += ".";
                nameFromRecord += subField.getFieldName();
                return getFullName((PVStructure)subField,nameFromRecord);
            }
        }
        return nameFromRecord;
    }

    private static PVStructure getSubStructure(PVStructure pvFromRequest,String nameFromRecord) {
        PVField[] pvFields = pvFromRequest.getPVFields();
        int len = pvFields.length;
        if(len==1) {
            pvFromRequest = (PVStructure) pvFields[0];
            if(pvFromRequest.getFieldName().equals("_options")) return pvFromRequest;
            if(nameFromRecord.length()!=0) nameFromRecord += ".";
            nameFromRecord += pvFromRequest.getFieldName();
            return getSubStructure((PVStructure)pvFields[0],nameFromRecord);
        }
        if(len==2) {
            PVField subField = null;
            if(pvFields[0].getFieldName().equals("_options")) {
                subField = pvFields[1];
            } else if(pvFields[1].getFieldName().equals("_options")) {
                subField = pvFields[1];
            }
            if(subField!=null) {
                if(nameFromRecord.length()!=0) nameFromRecord += ".";
                nameFromRecord += subField.getFieldName();
                return getSubStructure((PVStructure)subField,nameFromRecord);
            }
        }
        return pvFromRequest;
    }
}