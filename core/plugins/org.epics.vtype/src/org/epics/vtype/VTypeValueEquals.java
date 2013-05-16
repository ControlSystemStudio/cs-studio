/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import java.util.AbstractList;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.TimestampFormat;

/**
 * Helper class that provides functions to test value equality.
 *
 * @author carcassi
 */
public class VTypeValueEquals {
    private VTypeValueEquals() {
        // Do not create
    }

    /**
     * Checks whether the two table have the same data: equal column names, number
     * of rows and columns and their value.
     * 
     * @param arg1 a table
     * @param arg2 another table
     * @return true if the values are equals
     */
    public static boolean valueEquals(VTable arg1, VTable arg2) {
        if (arg1.getColumnCount() != arg2.getColumnCount()) {
            return false;
        }
        
        if (arg1.getRowCount() != arg2.getRowCount()) {
            return false;
        }
        
        for (int i = 0; i < arg1.getColumnCount(); i++) {
            if (!arg1.getColumnName(i).equals(arg2.getColumnName(i))) {
                return false;
            }

            if (!arg1.getColumnType(i).equals(arg2.getColumnType(i))) {
                return false;
            }
            
            if (!arg1.getColumnData(i).equals(arg2.getColumnData(i))) {
                return false;
            }
        }
        
        return true;
    }

}
