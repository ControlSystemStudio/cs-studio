/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.DoubleArrayData;
import org.epics.pvdata.pv.FloatArrayData;
import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.PVByteArray;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVFloatArray;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVUByteArray;
import org.epics.pvdata.pv.PVUIntArray;
import org.epics.pvdata.pv.PVULongArray;
import org.epics.pvdata.pv.PVUShortArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.vtype.VTable;
import org.epics.vtype.VTypeToString;
import org.epics.vtype.ValueFactory;

/**
 * @author msekoranja
 * @author dkumar
 */
public class PVFieldNTNameValueToVTable implements VTable {

  private List<Class<?>> types;
  private List<String> names;
  private List<Object> values;
  private int rowCount;

  private static final Convert convert = ConvertFactory.getConvert();

  /**
   * @param pvField
   * @param disconnected
   */
  public PVFieldNTNameValueToVTable(PVStructure pvField, boolean disconnected) {

    PVStringArray namesField =
      (PVStringArray) pvField.getScalarArrayField("name", ScalarType.pvString);

    PVScalarArray scalarArray = (PVScalarArray) pvField.getSubField("value");

    if ((namesField == null) || (scalarArray == null)) {
      this.names = null;
      types = null;
      values = null;
      rowCount = -1;
      return;
    }

    StringArrayData namesData = new StringArrayData();
    namesField.get(0, namesField.getLength(), namesData);
    this.names = Arrays.asList(namesData.data);

    int numCols = this.names.size();

    this.types = new ArrayList<Class<?>>(numCols);
    this.values = new ArrayList<Object>(numCols);

    // TODO why all to int?!!
    for (int col = 0; col < numCols; col++) {

      if (scalarArray instanceof PVDoubleArray) {

        types.add(double.class);
        DoubleArrayData data = new DoubleArrayData();
        ((PVDoubleArray) scalarArray).get(col, 1, data);
        values.add(ValueFactory.newVDouble(data.data[col], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVFloatArray) {

    	types.add(float.class);
        FloatArrayData data = new FloatArrayData();
        ((PVFloatArray) scalarArray).get(col, 1, data);
        values.add(ValueFactory.newVFloat(data.data[col], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVIntArray) {

        types.add(int.class);
        IntArrayData data = new IntArrayData();
        ((PVIntArray) scalarArray).get(col, 1, data);
        values.add(ValueFactory.newVInt(data.data[col], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVUIntArray) {

        types.add(int.class);
        IntArrayData data = new IntArrayData();
        ((PVUIntArray) scalarArray).get(col, 1, data);
        values.add(ValueFactory.newVInt(data.data[col], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVByteArray) {

        types.add(int.class);
        int[] intArr = new int[1];
        convert.toIntArray(scalarArray,col,1,intArr,0);
        values.add(ValueFactory.newVInt(intArr[0], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVUByteArray) {

        types.add(int.class);
        int[] intArr = new int[1];
        convert.toIntArray(scalarArray,col,1,intArr,0);
        values.add(ValueFactory.newVInt(intArr[0], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVLongArray) {

        types.add(int.class);
        int[] intArr = new int[1];
        convert.toIntArray(scalarArray,col,1,intArr,0);
        values.add(ValueFactory.newVInt(intArr[0], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVULongArray) {

        types.add(int.class);
        int[] intArr = new int[1];
        convert.toIntArray(scalarArray,col,1,intArr,0);
        values.add(ValueFactory.newVInt(intArr[0], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVShortArray) {

        types.add(int.class);
        int[] intArr = new int[1];
        convert.toIntArray(scalarArray,col,1,intArr,0);
        values.add(ValueFactory.newVInt(intArr[0], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVUShortArray) {

        types.add(int.class);
        int[] intArr = new int[1];
        convert.toIntArray(scalarArray,col,1,intArr,0);
        values.add(ValueFactory.newVInt(intArr[0], ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone()));

      } else if (scalarArray instanceof PVStringArray) {

        types.add(String.class);
        StringArrayData data = new StringArrayData();
        ((PVStringArray) scalarArray).get(col, 1, data);
        values.add(ValueFactory.newVString(data.data[col], ValueFactory.alarmNone(), ValueFactory.timeNow()));

      } else {
        throw new IllegalArgumentException("Array not supported");
      }
    }
  }

  @Override
  public int getColumnCount() {
    return this.names.size();
  }

  @Override
  public int getRowCount() {
    return this.rowCount;
  }

  @Override
  public Class<?> getColumnType(int column) {
    return this.types.get(column);
  }

  @Override
  public String getColumnName(int column) {
    return this.names.get(column);
  }

  @Override
  public Object getColumnData(int column) {
    return this.values.get(column);
  }

  @Override
  public String toString() {
    return VTypeToString.toString(this);
  }

}
