/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.Cell2DDataset;
import org.epics.graphene.Range;
import org.epics.graphene.RangeUtil;
import org.epics.graphene.Statistics;
import org.epics.graphene.StatisticsUtil;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ndarray.Array2DDouble;

/**
 *
 * @author carcassi
 */
class Cell2DDatasetFromVNumberArray implements Cell2DDataset {
    private Statistics statistics;
    private final ListNumber xBoundaries;
    private final ListNumber yBoundaries;
    private final Range xRange;
    private final Range yRange;
    private final Range displayRange;

    private final int xCount;
    private final int yCount;
    private final VNumberArray data;
    private final ListNumber values;
    
    private final Array2DDouble array2D;

    public Cell2DDatasetFromVNumberArray(VNumberArray data) {
        this.data = data;
        this.values = data.getData();
        if (data.getSizes().size() == 1) {
            this.xBoundaries = data.getDimensionDisplay().get(0).getCellBoundaries();
            this.yBoundaries = new ArrayDouble(0, 1);
            this.array2D = new Array2DDouble(new ArrayInt(1, xBoundaries.size() - 1), false, data.getDimensionDisplay().get(0).isReversed());
        } else if (data.getSizes().size() == 2) {
            this.xBoundaries = data.getDimensionDisplay().get(1).getCellBoundaries();
            this.yBoundaries = data.getDimensionDisplay().get(0).getCellBoundaries();
            this.array2D = new Array2DDouble(data.getSizes(), data.getDimensionDisplay().get(1).isReversed(), data.getDimensionDisplay().get(0).isReversed());
        } else {
            throw new IllegalArgumentException("Array is 3D or more");
        }
        
        this.xRange = RangeUtil.range(xBoundaries.getDouble(0), xBoundaries.getDouble(xBoundaries.size() - 1));
        this.yRange = RangeUtil.range(yBoundaries.getDouble(0), yBoundaries.getDouble(yBoundaries.size() - 1));
        this.xCount = xBoundaries.size() - 1;
        this.yCount = yBoundaries.size() - 1;
        // TODO: better way to handle if display limits are not set?
        double low = data.getLowerDisplayLimit();
        double high = data.getUpperDisplayLimit();
        if (values instanceof ListByte) {
            if (low == high) {
                low = Byte.MIN_VALUE;
                high = Byte.MAX_VALUE;
            }
            low = Math.max(Byte.MIN_VALUE, low);
            high = Math.min(Byte.MAX_VALUE, high);
        }
        this.displayRange = RangeUtil.range(low, high);
    }

    @Override
    public double getValue(int x, int y) {
        return values.getDouble(array2D.getIndex(x, y));
    }

    @Override
    public Statistics getStatistics() {
        if (statistics == null) {
            statistics =  StatisticsUtil.lazyStatisticsOf(values);
        }
        return statistics;
    }

    @Override
    public Range getDisplayRange() {
        return displayRange;
    }

    @Override
    public ListNumber getXBoundaries() {
        return xBoundaries;
    }

    @Override
    public Range getXRange() {
        return xRange;
    }

    @Override
    public int getXCount() {
        return xCount;
    }

    @Override
    public ListNumber getYBoundaries() {
        return yBoundaries;
    }

    @Override
    public Range getYRange() {
        return yRange;
    }

    @Override
    public int getYCount() {
        return yCount;
    }
    
}
