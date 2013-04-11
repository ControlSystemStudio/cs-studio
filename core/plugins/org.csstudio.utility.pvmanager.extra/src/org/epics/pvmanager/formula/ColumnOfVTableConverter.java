/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import org.epics.vtype.ValueFactory;
import java.util.Arrays;
import java.util.Objects;
import org.epics.pvmanager.ReadFunction;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
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
        
        Object data = table.getColumnArray(index);
        
        if (data instanceof String[]) {
            return ValueFactory.newVStringArray(Arrays.asList((String[]) data), ValueFactory.alarmNone(), ValueFactory.timeNow());
        }
        
        if (data instanceof double[]) {
            return ValueFactory.newVDoubleArray(new ArrayDouble((double[]) data), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        
        if (data instanceof int[]) {
            return ValueFactory.newVIntArray(new ArrayInt((int[]) data), ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
        }
        
        throw new RuntimeException("Unsupported type " + data.getClass().getSimpleName());
    }
    
}
