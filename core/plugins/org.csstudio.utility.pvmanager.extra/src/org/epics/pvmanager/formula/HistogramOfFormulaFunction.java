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

import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;

/**
 * @author shroffk
 * 
 */
public class HistogramOfFormulaFunction implements FormulaFunction {

    @Override
    public boolean isPure() {
	return true;
    }

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

    @Override
    public Object calculate(List<Object> args) {
	VNumberArray numberArray = (VNumberArray) args.get(0);
        if (numberArray == null) {
            return null;
        }
        Statistics stats = StatisticsUtil.statisticsOf(numberArray.getData());
        int nBins = 100;

        IteratorNumber newValues = numberArray.getData().iterator();
        double minValueRange = stats.getMinimum().doubleValue();
        double maxValueRange = stats.getMaximum().doubleValue();
        
        ListNumber xBoundaries = ListNumbers.linearListFromRange(minValueRange, maxValueRange, nBins + 1);
        Range xRange = stats;
        String unit = numberArray.getUnits();
        int[] binData = new int[nBins];
        while (newValues.hasNext()) {
            double value = newValues.nextDouble();
            // Check value in range
            if (Ranges.contains(xRange, value)) {

                int bin = (int) Math.floor(Ranges.normalize(xRange, value) * nBins);
                if (bin == nBins) {
                    bin--;
                }

                binData[bin]++;
            }
        }
        
	return newVNumberArray(new ArrayInt(binData), new ArrayInt(nBins), Arrays.asList(newDisplay(xBoundaries, unit)),
		numberArray, numberArray, displayNone());
    }

}
