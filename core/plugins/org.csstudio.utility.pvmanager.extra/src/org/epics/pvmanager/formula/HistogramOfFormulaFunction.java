/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.*;

import java.util.Arrays;
import java.util.List;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.IteratorNumber;
import org.epics.util.array.ListNumber;
import org.epics.util.array.ListNumbers;
import org.epics.util.stats.Range;
import org.epics.util.stats.Ranges;
import org.epics.util.stats.Statistics;
import org.epics.util.stats.StatisticsUtil;
import org.epics.util.text.NumberFormats;

import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;

/**
 * @author shroffk
 * 
 */
class HistogramOfFormulaFunction extends StatefulFormulaFunction {

    @Override
    public boolean isVarArgs() {
	return false;
    }

    @Override
    public String getName() {
	return "histogramOf";
    }

    @Override
    public String getDescription() {
	return "Returns a histograms of the elements in the array.";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
	return Arrays.<Class<?>> asList(VNumberArray.class);
    }

    @Override
    public List<String> getArgumentNames() {
	return Arrays.asList("Array", "index");
    }

    @Override
    public Class<?> getReturnType() {
	return VNumber.class;
    }
    
    private VNumberArray previousValue;
    private VNumberArray previousResult;
    private double previousMaxCount;
    private Range previousXRange;

    @Override
    public Object calculate(List<Object> args) {
	VNumberArray numberArray = (VNumberArray) args.get(0);
        if (numberArray == null) {
            return null;
        }
        
        // If no change, return previous
        if (previousValue == numberArray) {
            return previousResult;
        }
        
        Statistics stats = StatisticsUtil.statisticsOf(numberArray.getData());
        int nBins = 100;
        Range aggregatedRange = Ranges.aggregateRange(stats, previousXRange);
        Range xRange;
        if (Ranges.overlap(aggregatedRange, stats) >= 0.75) {
            xRange = aggregatedRange;
        } else {
            xRange = stats;
        }

        IteratorNumber newValues = numberArray.getData().iterator();
        double minValueRange = xRange.getMinimum().doubleValue();
        double maxValueRange = xRange.getMaximum().doubleValue();
        
        ListNumber xBoundaries = ListNumbers.linearListFromRange(minValueRange, maxValueRange, nBins + 1);
        String unit = numberArray.getUnits();
        int[] binData = new int[nBins];
        double maxCount = 0;
        while (newValues.hasNext()) {
            double value = newValues.nextDouble();
            // Check value in range
            if (Ranges.contains(xRange, value)) {

                int bin = (int) Math.floor(Ranges.normalize(xRange, value) * nBins);
                if (bin == nBins) {
                    bin--;
                }

                binData[bin]++;
                if (binData[bin] > maxCount) {
                    maxCount = binData[bin];
                }
            }
        }
        
        if (previousMaxCount > maxCount && previousMaxCount < maxCount * 2.0) {
            maxCount = previousMaxCount;
        }
        
        previousMaxCount = maxCount;
        previousXRange = xRange;
        previousValue = numberArray;
        previousResult = newVNumberArray(new ArrayInt(binData), new ArrayInt(nBins), Arrays.asList(newDisplay(xBoundaries, unit)),
		numberArray, numberArray, newDisplay(0.0, 0.0, 0.0, "count", NumberFormats.format(0), maxCount, maxCount, maxCount, Double.NaN, Double.NaN));
        
        return previousResult;
    }

}
