/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import org.epics.vtype.ValueFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.epics.pvmanager.ReadFunction;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;

/**
 * Converts numeric types to VDouble.
 *
 * @author carcassi
 */
class ColumnOfVTableConverter implements ReadFunction<VType> {
    
    private final ReadFunction<VTable> tableArg;
    private final ReadFunction<VString> columnNameArg;

    /**
     * Creates a new converter from the given function.
     * 
     * @param argument the argument function
     */
    public ColumnOfVTableConverter(ReadFunction<VTable> tableArg, ReadFunction<VString> columnNameArg) {
        this.tableArg = tableArg;
        this.columnNameArg = columnNameArg;
    }

    @Override
    public VType readValue() {
        final VTable table = tableArg.readValue();
        final VString columnName = columnNameArg.readValue();
        if (columnName == null || table == null) {
            return null;
        }
        
        int index = -1;
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (Objects.equals(columnName.getValue(), table.getColumnName(i))) {
                index = i;
            }
        }
        if (index == -1) {
            throw new RuntimeException("Table does not contain column '" + columnName.getValue() + "'");
        }
        
        Class<?> type = table.getColumnType(index);
        
        if (String.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            List<String> data = (List<String>) table.getColumnData(index);
            return ValueFactory.newVStringArray(data, ValueFactory.alarmNone(), ValueFactory.timeNow());
        }
        
        if (Double.TYPE.isAssignableFrom(type)) {
            ListDouble data = (ListDouble) table.getColumnData(index);
            return ValueFactory.newVDoubleArray(data, ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        
        if (Integer.TYPE.isAssignableFrom(type)) {
            ListInt data = (ListInt) table.getColumnData(index);
            return ValueFactory.newVIntArray(data, ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        
        throw new RuntimeException("Unsupported type " + type.getSimpleName());
    }
    
}
