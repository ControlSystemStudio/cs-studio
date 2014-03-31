/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import java.util.Collections;
import java.util.List;
import org.epics.graphene.Point2DDataset;
import org.epics.graphene.Point2DDatasets;
import org.epics.graphene.Point3DWithLabelDataset;
import org.epics.graphene.Point3DWithLabelDatasets;
import org.epics.util.array.ListDouble;
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
    
    /**
     * Converts a VTable into a Point2DDataset.
     * 
     * @param vTable the table containing the data
     * @param xColumn the column name for the x values
     * @param yColumn the column name for the y values
     * @param sizeColumn the column name for the size values
     * @param colorColumn the column name for the size values
     * @return the dataset
     */
    public static Point3DWithLabelDataset point3DDatasetFromVTable(VTable vTable,
            String xColumn, String yColumn, String sizeColumn, String colorColumn) {
        // Extract x and y using column names
        ListNumber xValues = ValueUtil.numericColumnOf(vTable, xColumn);
        ListNumber yValues = ValueUtil.numericColumnOf(vTable, yColumn);
        ListNumber sizeValues = ValueUtil.numericColumnOf(vTable, sizeColumn);
        List<String> colorValues = ValueUtil.stringColumnOf(vTable, colorColumn);

        // If none of the columns where specified, find the first column that fits
        if (xValues == null && yValues == null && sizeValues == null && colorValues == null) {
            // Fill the missing columns with the first available columns
            for (int i = 0; i < vTable.getColumnCount(); i++) {
                if (vTable.getColumnType(i).isPrimitive()) {
                    if (xValues == null) {
                        xValues = (ListNumber) vTable.getColumnData(i);
                    } else if (yValues == null) {
                        yValues = (ListNumber) vTable.getColumnData(i);
                    } else if (sizeValues == null) {
                        sizeValues = (ListNumber) vTable.getColumnData(i);
                    }
                } else if (vTable.getColumnType(i).equals(String.class)) {
                    if (colorValues == null) {
                        @SuppressWarnings("unchecked")
                        List<String> list = (List<String>) vTable.getColumnData(i);
                        colorValues = list;
                    }
                }
            }
            
            if (xValues == null || yValues == null) {
                throw new IllegalArgumentException("Couldn't find two numeric columns for X and Y");
            }            
        }
            
        if (xValues == null || yValues == null) {
            throw new IllegalArgumentException("X and Y must both be specified");
        }

        // If sizes is missing, generate a 0 columns
        final int nValues = xValues.size();
        if (sizeValues == null) {
            sizeValues = new ListDouble() {

                @Override
                public double getDouble(int index) {
                    return 0;
                }

                @Override
                public int size() {
                    return nValues;
                }
            };
        }
        
        // If color is missing, generate a "None" column
        if (colorValues == null) {
            colorValues = Collections.nCopies(xValues.size(), "None");
        }
        
        return Point3DWithLabelDatasets.build(xValues, yValues, sizeValues, colorValues);
    }
}
