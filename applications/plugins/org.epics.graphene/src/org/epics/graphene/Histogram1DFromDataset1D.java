/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.IteratorDouble;
import org.epics.util.array.IteratorNumber;

/**
 *
 * @author carcassi
 */
class Histogram1DFromDataset1D implements Histogram1D {
    
    private double minValueRange;
    private double maxValueRange;
    private int minCountRange;
    private int maxCountRange;
    private double[] binValueBoundary;
    private int[] binCount;
    private boolean autoValueRange = true;
    private int nBins = 100;

    @Override
    public double getMinValueRange() {
        return minValueRange;
    }

    @Override
    public double getMaxValueRange() {
        return maxValueRange;
    }

    public void setMaxValueRange(double maxValueRange) {
        this.maxValueRange = maxValueRange;
    }

    public void setMinValueRange(double minValueRange) {
        this.minValueRange = minValueRange;
    }

    @Override
    public int getMaxCountRange() {
        return maxCountRange;
    }

    public void setMaxCountRange(int maxCountRange) {
        this.maxCountRange = maxCountRange;
    }

    @Override
    public int getMinCountRange() {
        return minCountRange;
    }

    public void setMinCountRange(int minCountRange) {
        this.minCountRange = minCountRange;
    }

    @Override
    public int getNBins() {
        return binCount.length;
    }

    @Override
    public double getBinValueBoundary(int index) {
        return binValueBoundary[index];
    }

    @Override
    public int getBinCount(int index) {
        return binCount[index];
    }

    public void setBinCount(int[] binCount) {
        this.binCount = binCount;
    }

    public void setBinValueBoundary(double[] binValueBoundary) {
        this.binValueBoundary = binValueBoundary;
    }
    
    public void setDataset(Point1DDataset dataset) {
        IteratorNumber values = dataset.getValues().iterator();
        if (autoValueRange) {
            this.minValueRange = dataset.getMinValue().doubleValue();
            this.maxValueRange = dataset.getMaxValue().doubleValue();
            binValueBoundary = RangeUtil.createBins(minValueRange, maxValueRange, nBins);
        }
        binCount = new int[nBins];
        while (values.hasNext()) {
            addValueToBin(values.nextDouble());
        }

        autoBinRange();
    }
    
    private void addValueToBin(double value) {
        // Discard value outsie the binning area
        if (value < getBinValueBoundary(0) || value > getBinValueBoundary(nBins)) {
            return;
        }
        
        int bin = (int) Math.floor(NumberUtil.scale(value, getBinValueBoundary(0), getBinValueBoundary(nBins), nBins));
        if (bin == nBins) {
            bin--;
        }
        
        binCount[bin]++;
    }
    
    protected void autoBinRange() {
        int max = NumberUtil.minMax(binCount)[1];
        
        if (max < 10) {
            minCountRange = 0;
            maxCountRange = 10;
        } else if (max < 50) {
            minCountRange = 0;
            maxCountRange = 50;
        } else {
            minCountRange = 0;
            maxCountRange = (max / 100) * 100 + 100;
        }
    }

    @Override
    public void update(Histogram1DUpdate update) {
        if (update.getDataset() != null)
            setDataset(update.getDataset());
    }
    
}
