/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListNumber;
import org.epics.util.array.ListNumbers;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;

/**
 *
 * @author carcassi
 */
public class VTableFactory {
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
    
    public static Column column(final String name, final ListNumberProvider dataProvider) {
        return new Column(name, dataProvider.getType(), true) {
            @Override
            public Object getData(int size) {
                return dataProvider.createListNumber(size);
            }
        };
    } 

    public static ListNumberProvider range(final double min, final double max) {
        return new ListNumberProvider(double.class) {

            @Override
            public ListNumber createListNumber(final int size) {
                return ListNumbers.linearListFromRange(min, max, size);
            }
        };
    }
    
    public static ListNumberProvider step(final double initialValue, final double increment) {
        return new ListNumberProvider(double.class) {

            @Override
            public ListNumber createListNumber(int size) {
                return ListNumbers.linearList(initialValue, increment, size);
            }
        };
    }

}
