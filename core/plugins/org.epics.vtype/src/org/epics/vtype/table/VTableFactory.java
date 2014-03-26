/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype.table;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.BufferInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;
import org.epics.util.array.ListNumbers;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;
import static org.epics.vtype.ValueFactory.*;

/**
 *
 * @author carcassi
 */
public class VTableFactory {
 
    public static VTable join(List<VTable> tables) {
        return join(tables.toArray(new VTable[tables.size()]));
    }
    
    public static VTable join(VTable... tables) {
        if (tables.length == 0) {
            return null;
        }
        
        if (tables.length == 1) {
            return tables[0];
        }
        
        // Find columns to join
        Map<String, int[]> commonColumnsIndexes = null;
        for (int nTable = 0; nTable < tables.length; nTable++) {
            VTable vTable = tables[nTable];
            if (commonColumnsIndexes == null) {
                commonColumnsIndexes = new HashMap<>();
                for (int i = 0; i < vTable.getColumnCount(); i++) {
                    int[] indexes = new int[tables.length];
                    indexes[0] = i;
                    commonColumnsIndexes.put(vTable.getColumnName(i), indexes);
                    
                }
            } else {
                commonColumnsIndexes.keySet().retainAll(columnNames(vTable));
                for (int i = 0; i < vTable.getColumnCount(); i++) {
                    if (commonColumnsIndexes.keySet().contains(vTable.getColumnName(i))) {
                        commonColumnsIndexes.get(vTable.getColumnName(i))[nTable] = i;
                    }
                }
            }
        }
        
        if (commonColumnsIndexes.isEmpty()) {
            throw new UnsupportedOperationException("Case not implemented yet");
        }
        
        List<EqualValueFilter> filters = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : commonColumnsIndexes.entrySet()) {
            int[] indexes = entry.getValue();
            filters.add(new EqualValueFilter(Arrays.asList(tables), indexes));
        }
        
        // Find rows
        boolean done = false;
        List<BufferInt> rowIndexes = new ArrayList<>();
        for (int i = 0; i < tables.length; i++) {
            rowIndexes.add(new BufferInt());
            if (tables[i].getRowCount() == 0) {
                done = true;
            }
        }
        int[] currentIndexes = new int[tables.length];
        while (!done) {
            boolean match = true;
            for (EqualValueFilter filter : filters) {
                match = match && filter.filterRow(currentIndexes);
            }
            if (match) {
                for (int i = 0; i < currentIndexes.length; i++) {
                    rowIndexes.get(i).addInt(currentIndexes[i]);
                }
            }
            boolean needsIncrement = true;
            int offset = currentIndexes.length - 1;
            while (needsIncrement) {
                currentIndexes[offset]++;
                if (currentIndexes[offset] == tables[offset].getRowCount()) {
                    currentIndexes[offset] = 0;
                    offset--;
                    if (offset == -1) {
                        done = true;
                        needsIncrement = false;
                    }
                } else {
                    needsIncrement = false;
                }
            }
        }
        
        List<String> columnNames = new ArrayList<>();
        List<Class<?>> columnTypes = new ArrayList<>();
        List<Object> columnData = new ArrayList<>();
        for (int nColumn = 0; nColumn < tables[0].getColumnCount(); nColumn++) {
            columnNames.add(tables[0].getColumnName(nColumn));
            Class<?> type = tables[0].getColumnType(nColumn);
            if (type.isPrimitive()) {
                columnTypes.add(double.class);
                columnData.add(createView((ListNumber) tables[0].getColumnData(nColumn), rowIndexes.get(0)));
            } else {
                columnTypes.add(type);
                columnData.add(createView((List<?>) tables[0].getColumnData(nColumn), rowIndexes.get(0)));
            }
        }
        for (int i = 1; i < tables.length; i++) {
            VTable vTable = tables[i];
            for (int nColumn = 0; nColumn < vTable.getColumnCount(); nColumn++) {
                if (!commonColumnsIndexes.containsKey(vTable.getColumnName(nColumn))) {
                    columnNames.add(vTable.getColumnName(nColumn));
                    Class<?> type = vTable.getColumnType(nColumn);
                    if (type.isPrimitive()) {
                        columnTypes.add(double.class);
                        columnData.add(createView((ListNumber) vTable.getColumnData(nColumn), rowIndexes.get(i)));
                    } else {
                        columnTypes.add(type);
                        columnData.add(createView((List<?>) vTable.getColumnData(nColumn), rowIndexes.get(i)));
                    }
                }
            }
        }
        
        return ValueFactory.newVTable(columnTypes, columnNames, columnData);
    }
    
    private static Object selectColumnData(VTable table, int column, ListInt indexes) {
        Class<?> type = table.getColumnType(column);
        if (type.isPrimitive()) {
            return createView((ListNumber) table.getColumnData(column), indexes);
        } else {
            return createView((List<?>) table.getColumnData(column), indexes);
        }
    }
    
    private static <T> List<T> createView(final List<T> list, final ListInt indexes) {
        return new AbstractList<T>() {

            @Override
            public T get(int index) {
                return list.get(indexes.getInt(index));
            }

            @Override
            public int size() {
                return indexes.size();
            }
        };
    }
    
    private static ListNumber createView(final ListNumber list, final ListInt indexes) {
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return list.getDouble(indexes.getInt(index));
            }

            @Override
            public int size() {
                return indexes.size();
            }
        };
    }
    
    public static VTable select(final VTable table, final ListInt indexes) {
        List<String> names = columnNames(table);
        List<Class<?>> types = columnTypes(table);
        List<Object> data = new AbstractList<Object>() {

            @Override
            public Object get(int index) {
                return selectColumnData(table, index, indexes);
            }

            @Override
            public int size() {
                return table.getColumnCount();
            }
        };
        return ValueFactory.newVTable(types, names, data);
    }
    
    public static VTable newVTable(Column... columns) {
        List<String> columnNames = new ArrayList<>();
        columnNames.addAll(Collections.<String>nCopies(columns.length, null));
        List<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.addAll(Collections.<Class<?>>nCopies(columns.length, null));
        List<Object> columnData = new ArrayList<>();
        columnData.addAll(Collections.nCopies(columns.length, null));
        
        int size = -1;
        // First add all the static columns
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            if (!column.isGenerated()) {
                Object data = column.getData(size);
                size = sizeOf(data);
                columnNames.set(i, column.getName());
                columnTypes.set(i, column.getType());
                columnData.set(i, data);
            }
        }
        
        if (size == -1) {
            throw new IllegalArgumentException("At least one column must be of a defined size");
        }
        
        // Then add all the generated columns
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            if (column.isGenerated()) {
                Object data = column.getData(size);
                columnNames.set(i, column.getName());
                columnTypes.set(i, column.getType());
                columnData.set(i, data);
            }
        }
        
        return ValueFactory.newVTable(columnTypes, columnNames, columnData);
    }
    
    private static int sizeOf(Object data) {
        if (data instanceof ListNumber) {
            return ((ListNumber) data).size();
        } else {
            return ((List<?>) data).size();
        }
    }
    
    public static Column column(String name, final VNumberArray numericArray) {
        // TODO: for now rewrapping in VDouble. Will need to make table work with
        // all primitive types so that this is not an issue
        
        final ListDouble data;
        if (numericArray.getData() instanceof ListDouble) {
            data = (ListDouble) numericArray.getData();
        } else  {
            data = new ListDouble() {

                @Override
                public double getDouble(int index) {
                    return numericArray.getData().getDouble(index);
                }

                @Override
                public int size() {
                    return numericArray.getData().size();
                }
            };
        }
        
        return new Column(name, double.class, false) {
            @Override
            public Object getData(int size) {
                if (size >= 0) {
                    if (size != data.size()) {
                        throw new IllegalArgumentException("Column size does not match the others (this is " + data.size() + " previous is " + size);
                    }
                }
                return data;
            }
        };
    }
    
    public static Column column(String name, final VStringArray stringArray) {
        final List<String> data = stringArray.getData();
        
        return new Column(name, String.class, false) {
            @Override
            public Object getData(int size) {
                if (size >= 0) {
                    if (size != data.size()) {
                        throw new IllegalArgumentException("Column size does not match the others (this is " + data.size() + " previous is " + size);
                    }
                }
                return data;
            }
        };
    }
    
    public static Column column(final String name, final ListNumberProvider dataProvider) {
        return new Column(name, dataProvider.getType(), true) {
            @Override
            public Object getData(int size) {
                return dataProvider.createListNumber(size);
            }
        };
    } 

    public static ListNumberProvider range(final double min, final double max) {
        return new Range(min, max);
    }
    
    private static class Range extends ListNumberProvider {
        
        private final double min;
        private final double max;

        public Range(double min, double max) {
            super(double.class);
            this.min = min;
            this.max = max;
        }

        @Override
        public ListNumber createListNumber(int size) {
            return ListNumbers.linearListFromRange(min, max, size);
        }
    };
    
    public static ListNumberProvider step(final double initialValue, final double increment) {
        return new Step(initialValue, increment);
    }
    
    private static class Step extends ListNumberProvider {
        
        private final double initialValue;
        private final double increment;

        public Step(double initialValue, double increment) {
            super(double.class);
            this.initialValue = initialValue;
            this.increment = increment;
        }

        @Override
        public ListNumber createListNumber(int size) {
            return ListNumbers.linearList(initialValue, increment, size);
        }
    };
    
    public static VTable extractRow(VTable vTable, int row) {
        if (vTable == null || row >= vTable.getRowCount() || row < 0) {
            return null;
        }
        List<String> columnNames = new ArrayList<>(vTable.getColumnCount());
        List<Class<?>> columnTypes = new ArrayList<>(vTable.getColumnCount());
        List<Object> columnData = new ArrayList<>(vTable.getColumnCount());
        for (int nCol = 0; nCol < vTable.getColumnCount(); nCol++) {
            columnNames.add(vTable.getColumnName(nCol));
            columnTypes.add(vTable.getColumnType(nCol));
            columnData.add(extractColumnData(vTable.getColumnData(nCol), row));
        }
        return ValueFactory.newVTable(columnTypes, columnNames, columnData);
    }
    
    private static Object extractColumnData(Object columnData, int... rows) {
        if (columnData instanceof List) {
            List<Object> data = new ArrayList<>(rows.length);
            for (int i = 0; i < rows.length; i++) {
                int j = rows[i];
                data.add(((List<?>) columnData).get(j));
            }
            return data;
        } else if (columnData instanceof ListNumber) {
            double[] data = new double[rows.length];
            for (int i = 0; i < rows.length; i++) {
                int j = rows[i];
                data[i] = ((ListNumber) columnData).getDouble(j);
            }
            return new ArrayDouble(data);
        }
        return null;
    }
    
    public static List<String> columnNames(final VTable vTable) {
        return new AbstractList<String>() {
            @Override
            public String get(int index) {
                return vTable.getColumnName(index);
            }

            @Override
            public int size() {
                return vTable.getColumnCount();
            }
        };
    }
    
    public static List<Class<?>> columnTypes(final VTable vTable) {
        return new AbstractList<Class<?>>() {
            @Override
            public Class<?> get(int index) {
                return vTable.getColumnType(index);
            }

            @Override
            public int size() {
                return vTable.getColumnCount();
            }
        };
    }
    
    public static VTable valueTable(List<? extends VType> values) {
        return valueTable(null, values);
    }
    
    public static VTable valueTable(List<String> names, List<? extends VType> values) {
        int nullValue = values.indexOf(null);
        if (nullValue != -1) {
            values = new ArrayList<>(values);
            if (names != null) {
                names = new ArrayList<>(names);
            }
            for (int i = values.size() - 1; i >=0; i--) {
                if (values.get(i) == null) {
                    values.remove(i);
                    if (names != null) {
                        names.remove(i);
                    }
                }
            }
        }
        
        if (values.isEmpty()) {
            return valueNumberTable(names, values);
        }
        
        if (values.get(0) instanceof VNumber) {
            for (VType vType : values) {
                if (!(vType instanceof VNumber)) {
                    throw new IllegalArgumentException("Values do not match (VNumber and " + ValueUtil.typeOf(vType).getSimpleName());
                }
            }
            return valueNumberTable(names, values);
        }
        
        throw new IllegalArgumentException("Type " + ValueUtil.typeOf(values.get(0)).getSimpleName() + " not supported for value table");
    }
    
    private static VTable valueNumberTable(List<String> names, List<? extends VType> values) {
        double[] data = new double[values.size()];
        List<String> severity = new ArrayList<>();
        List<String> status = new ArrayList<>();
        
        for (int i = 0; i < values.size(); i++) {
            VNumber vNumber = (VNumber) values.get(i);
            data[i] = vNumber.getValue().doubleValue();
            severity.add(vNumber.getAlarmSeverity().name());
            status.add(vNumber.getAlarmName());
        }
        
        if (names == null) {
            return newVTable(column("Value", newVDoubleArray(new ArrayDouble(data), alarmNone(), timeNow(), displayNone())),
                    column("Severity", newVStringArray(severity, alarmNone(), timeNow())),
                    column("Status", newVStringArray(status, alarmNone(), timeNow())));
        } else {
            return newVTable(column("Name", newVStringArray(names, alarmNone(), timeNow())),
                    column("Value", newVDoubleArray(new ArrayDouble(data), alarmNone(), timeNow(), displayNone())),
                    column("Severity", newVStringArray(severity, alarmNone(), timeNow())),
                    column("Status", newVStringArray(status, alarmNone(), timeNow())));
        }
    }
}
