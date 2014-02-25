/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import org.epics.vtype.ValueFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;

/**
 * Extracts a columns from a VTable.
 *
 * @author carcassi
 */
class ColumnOfVTableFunction implements FormulaFunction {

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public String getName() {
        return "columnOf";
    }

    @Override
    public String getDescription() {
        return "Extracts a column from the given table";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VTable.class, VString.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("table", "columName");
    }

    @Override
    public Class<?> getReturnType() {
        return VType.class;
    }

    @Override
    public Object calculate(final List<Object> args) {
        VTable table = (VTable) args.get(0);
        VString columnName = (VString) args.get(1);
        
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
