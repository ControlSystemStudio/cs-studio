/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

/**
 * A table. Tables are collections of columns, each of which is composed
 * of a String representing the name of the column and an array of a particular
 * type (all elements of the same column must be of the same type).
 *
 * @author carcassi
 */
public interface VTable extends VType {

    /**
     * The number of columns in the table.
     *
     * @return the number of columns
     */
    int getColumnCount();
    
    /**
     * The number of rows in the table.
     * <p>
     * Currently, is not clear whether all columns actually have the same
     * number of rows, that is if all arrays have the same length. In the
     * case of variable row, this will return the maximum row count, that is
     * the length of the longest array/column.
     * 
     * @return the number of rows
     */
    int getRowCount();

    /**
     * The type of the elements in the column. The column array will be
     * an array of the given type. For primitive types, this function will return
     * the wrapper class, while {@link #getColumnArray(int)} will return
     * an array of primitives.
     *
     * @param column the column index
     * @return the type of this column
     */
    Class<?> getColumnType(int column);

    /**
     * The name of the given column.
     *
     * @param column the column index
     * @return the name of the column
     */
    String getColumnName(int column);

    /**
     * The data for the given column. You should cast this object
     * to an array of the appropriate type.
     *
     * @param column the column index
     * @return the data of the column
     */
    Object getColumnArray(int column);
}
