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

        // If one column name was null, fill it with the index
        if (xValues == null && yValues != null) {
            xValues = ListNumbers.linearList(0, 1, yValues.size());
        }
        if (yValues == null && xValues != null) {
            yValues = ListNumbers.linearList(0, 1, xValues.size());
        }

        // If both column names were null, use the first two numeric columns
        if (xValues == null && yValues == null) {
            for (int i = 0; i < vTable.getColumnCount(); i++) {
                if (vTable.getColumnType(i).isPrimitive()) {
                    if (xValues == null) {
                        xValues = (ListNumber) vTable.getColumnData(i);
                    } else if (yValues == null) {
                        yValues = (ListNumber) vTable.getColumnData(i);
                    }
                }
            }
            
            if (yValues == null) {
                throw new IllegalArgumentException("Couldn't find two numeric columns");
            }
        }
        
        return Point2DDatasets.lineData(xValues, yValues);
    }
}
