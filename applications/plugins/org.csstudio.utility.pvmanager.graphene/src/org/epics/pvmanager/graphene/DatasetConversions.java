/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.Point2DDataset;
import org.epics.graphene.Point2DDatasets;
import org.epics.util.array.ListNumber;
import org.epics.util.array.ListNumbers;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueUtil;

/**
 * Utility class to convert VTypes into graphene datasets.
 * 
 * @author carcassi
 */
public class DatasetConversions {
    
    /**
     * Converts a VTable into a Point2DDataset.
     * 
     * @param vTable the table containing the data
     * @param xColumn the column for the x values
     * @param yColumn the column for the y values
     * @return the dataset
     */
    public static Point2DDataset point2DDatasetFromVTable(VTable vTable,
            String xColumn, String yColumn) {
        // Extract x and y using column names
        ListNumber xValues = ValueUtil.numericColumnOf(vTable, xColumn);
        ListNumber yValues = ValueUtil.numericColumnOf(vTable, yColumn);
        
        // Fill the missing columns with the first available columns
        for (int i = 0; i < vTable.getColumnCount(); i++) {
            if (vTable.getColumnType(i).isPrimitive()) {
                // Don't reuse the same column
                if (!vTable.getColumnName(i).equals(xColumn) &&
                        !vTable.getColumnName(i).equals(yColumn)) {
                    if (xValues == null) {
                        xValues = (ListNumber) vTable.getColumnData(i);
                    } else if (yValues == null) {
                        yValues = (ListNumber) vTable.getColumnData(i);
                    }
                }
            }
        }
            
        if (xValues == null || yValues == null) {
            throw new IllegalArgumentException("Couldn't find two numeric columns");
        }
        
        return Point2DDatasets.lineData(xValues, yValues);
    }
}
