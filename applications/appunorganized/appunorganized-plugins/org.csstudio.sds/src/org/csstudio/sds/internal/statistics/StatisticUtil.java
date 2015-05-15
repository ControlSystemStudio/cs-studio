/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.internal.statistics;

import java.util.HashMap;

/**
 * Utility for the statistical analysis of SDS performance.
 *
 * @author Sven Wende
 * @version $Revision: 1.2 $
 */
public final class StatisticUtil {

    /**
     * Stores the execution counters for categories.
     */
    private HashMap<MeasureCategoriesEnum, Integer> _executionCounter;

    /**
     * Stores the execution time sums for categories.
     */
    private HashMap<MeasureCategoriesEnum, Long> _executionTimeSums;

    /**
     * The starting time.
     */
    private long _startTime;

    /**
     * The shared instance of this class.
     */
    private static StatisticUtil _instance;

    /**
     * Private constructor, to prevent instantiation.
     */
    private StatisticUtil() {
        init();
    }

    /**
     * Return the shared instance.
     *
     * @return The shared instance.
     */
    public static StatisticUtil getInstance() {
        if (_instance == null) {
            _instance = new StatisticUtil();
        }

        return _instance;
    }

    /**
     * Tracks the execution of a runnable for the specified category.
     *
     * @param category
     *            the category
     * @param timeNeeded
     *            the time it took to fully execute the runnable
     */
    public synchronized void trackExecution(
            final MeasureCategoriesEnum category, final long timeNeeded) {
        incrementExecutionCounter(category);
        incrementExecutionTime(category, timeNeeded);
    }

    /**
     * Increments the execution counter for the specified category.
     *
     * @param category
     *            the category
     */
    private synchronized void incrementExecutionCounter(
            final MeasureCategoriesEnum category) {
        int count = 1;
        if (_executionCounter.containsKey(category)) {
            count = _executionCounter.get(category);
        }

        count++;

        _executionCounter.put(category, count);
    }

    /**
     * Increments the execution time sume for the specified category.
     *
     * @param category
     *            the category
     * @param timeNeeded
     *            the time it took to fully execute the runnable
     * @author swende
     */
    private synchronized void incrementExecutionTime(
            final MeasureCategoriesEnum category, final long timeNeeded) {
        long time = 1;
        if (_executionTimeSums.containsKey(category)) {
            time = _executionTimeSums.get(category);
        }

        time += timeNeeded;

        _executionTimeSums.put(category, time);
    }

    /**
     * Reset the statistics.
     */
    public void init() {
        _executionCounter = new HashMap<MeasureCategoriesEnum, Integer>();
        _executionTimeSums = new HashMap<MeasureCategoriesEnum, Long>();
        _startTime = System.currentTimeMillis();
    }

    /**
     * @return The overall running time since the last reset.
     */
    public long getRunningTime() {
        return System.currentTimeMillis() - _startTime;
    }

    /**
     * Return the execution count for the given category.
     *
     * @param category
     *            A measure cotegory.
     * @return The execution count for the given category.
     */
    public int getExecutionCount(final MeasureCategoriesEnum category) {
        int result = 0;

        if (_executionCounter.containsKey(category)) {
            result = _executionCounter.get(category);
        }

        return result;
    }

    /**
     * Return the execution time sum for the given category.
     *
     * @param category
     *            A measure cotegory.
     * @return The execution time sum for the given category.
     */
    public long getExecutionTimeSum(final MeasureCategoriesEnum category) {
        long result = 0L;

        if (_executionTimeSums.containsKey(category)) {
            result = _executionTimeSums.get(category);
        }

        return result;
    }

    /**
     * Return the average calls per ms for the given category.
     *
     * @param category
     *            A measure cotegory.
     * @return The average calls per ms for the given category.
     */
    public double getAverageCallsPerMs(final MeasureCategoriesEnum category) {
        long runningTime = getRunningTime();
        int amount = getExecutionCount(category);

        double result = 0.0;

        if (amount > 0 && runningTime > 0) {
            result = (double) amount / runningTime;
        }

        return result;
    }

    /**
     * Return the average execution time per call for the given category.
     *
     * @param category
     *            A measure cotegory.
     * @return The average execution time per call for the given category.
     */
    public double getAverageExecutionTimePerCall(
            final MeasureCategoriesEnum category) {
        long executionTimeSum = getExecutionTimeSum(category);
        int count = getExecutionCount(category);

        double result = 0.0;

        if (count > 0 && executionTimeSum > 0) {
            result = (double) executionTimeSum / count;
        }

        return result;
    }

    /**
     * Return the average time between calls for the given category.
     *
     * @param category
     *            A measure cotegory.
     * @return The average time between calls for the given category.
     */
    public double getAverageTimeBetweenCalls(
            final MeasureCategoriesEnum category) {
        long runningTime = getRunningTime();
        int count = getExecutionCount(category);

        double result = 0.0;

        if (count > 0 && runningTime > 0) {
            result = (double) runningTime / count;
        }

        return result;
    }
}
